package me.bickositieff.raspio.ui.serverSelect

import android.Manifest
import android.app.Activity
import android.companion.AssociationRequest
import android.companion.CompanionDeviceManager
import android.companion.WifiDeviceFilter
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.bickositieff.raspio.MainActivity
import me.bickositieff.raspio.R
import me.bickositieff.raspio.api.RaspioServerDecorator
import me.bickositieff.raspio.databinding.ActivityServerSelectBinding
import me.bickositieff.raspio.generated.ApiHolder
import me.bickositieff.raspio.generated.api.NetworkApi
import java.net.ConnectException
import java.net.SocketTimeoutException

class ServerSelectActivity : AppCompatActivity() {
    private val tag = "ServerSelectActivity"
    private val RQ_ACCESS_FINE_LOCATION = 420
    private val viewModel: ServerSelectViewModel by viewModels()

    val temp = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
        val resultCode = activityResult.resultCode
        val data = activityResult.data
        when(resultCode) {
            Activity.RESULT_OK -> {
                val deviceToPair: ScanResult? = data?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE)
                deviceToPair?.let { device ->
                    connectWifi(device)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RQ_ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lookupDevice()
            }
        }
    }

    private fun connectWifi(device: ScanResult) {
        WifiUtils.withContext(this@ServerSelectActivity).enableWifi();
        WifiUtils.withContext(this@ServerSelectActivity)
            .connectWith(device.SSID, "")
            .onConnectionResult(object : ConnectionSuccessListener {
                override fun success() {
                    Toast.makeText(this@ServerSelectActivity, "Connected to WiFi", Toast.LENGTH_SHORT).show()
                }

                override fun failed(errorCode: ConnectionErrorCode) {
                    Toast.makeText(this@ServerSelectActivity, "Failed to connect", Toast.LENGTH_SHORT).show()
                }
            }).start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityServerSelectBinding = DataBindingUtil.setContentView(this, R.layout.activity_server_select)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        lifecycleScope.launchWhenCreated { checkExisting(binding) }

        binding.serverSelectConfirm.setOnClickListener {
            lifecycleScope.launch() {
                val ip = binding.serverSelectIPEditText.text.toString()

                val success = checkConnection(ip)

                if (success) {
                    binding.serverSelectIPInput.error = null
                    // Connection established
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this@ServerSelectActivity)
                    with(sharedPrefs.edit()) {
                        putString("ip", ip)
                        apply()
                    }
                    startActivity(Intent(this@ServerSelectActivity, MainActivity::class.java))
                    finish()
                } else {
                    // Connection failed
                    binding.serverSelectIPInput.error = getString(R.string.shr_error_ip)
                }
            }
        }

        binding.scanwifiButton.setOnClickListener {
            viewModel.loading.value = true
            val deviceFilter: WifiDeviceFilter = WifiDeviceFilter.Builder()
                //.setNamePattern(Pattern.compile("RASPIO_"))
                .build()
            val pairingRequest: AssociationRequest = AssociationRequest.Builder()
                .addDeviceFilter(deviceFilter)
                .build()
            val deviceManager : CompanionDeviceManager = getSystemService(Context.COMPANION_DEVICE_SERVICE) as CompanionDeviceManager
            deviceManager.associate(pairingRequest,
                object : CompanionDeviceManager.Callback() {
                    override fun onDeviceFound(chooserLauncher: IntentSender) {
                        viewModel.loading.value = false
                        temp.launch(IntentSenderRequest.Builder(chooserLauncher).build())
                    }

                    override fun onFailure(error: CharSequence?) {
                        viewModel.loading.value = false
                        Toast.makeText(this@ServerSelectActivity, "No WIFI found in area", Toast.LENGTH_LONG).show()
                    }
                }, null)


        }

        binding.scanwifiButton.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), RQ_ACCESS_FINE_LOCATION)
            } else {
                lookupDevice()
            }
        }

        binding.serverSelectIPInput.setStartIconOnClickListener {
            var finished = false
            viewModel.loading.postValue(true)
            lifecycleScope.launch {
                val nsdManager = (getSystemService(Context.NSD_SERVICE) as NsdManager)
                val resolveListener = object : NsdManager.ResolveListener {

                    override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                        lifecycleScope.launch {
                            withContext(Dispatchers.Main) {
                                binding.serverSelectIPInput.error = getString(R.string.ip_not_resolvable)
                            }
                        }
                        Log.e(tag, "Resolve failed: $errorCode")
                        viewModel.loading.postValue(false)
                    }

                    override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                        Log.e(tag, "Resolve Succeeded. $serviceInfo")
                        lifecycleScope.launch {
                            withContext(Dispatchers.Main) {
                                binding.serverSelectIPInput.error = null
                            }
                        }
                        lifecycleScope.launch {
                            withContext(Dispatchers.Main) {
                                binding.serverSelectIPEditText.setText(
                                    serviceInfo.host.toString().removePrefix("/")
                                )
                            }
                        }
                        viewModel.loading.postValue(false)
                        finished = true
                    }
                }

                val discoveryListener = object : NsdManager.DiscoveryListener {
                    override fun onDiscoveryStarted(regType: String) {
                        Log.d(tag, "Service discovery started")
                    }

                    override fun onServiceFound(service: NsdServiceInfo) {
                        Log.d(tag, "Service discovery success$service")
                        when {
                            service.serviceType != "_raspio._tcp." ->
                                Log.d(tag, "Unknown Service Type: ${service.serviceType}")

                            service.serviceType == "_raspio._tcp." -> {
                                nsdManager.stopServiceDiscovery(this)
                                nsdManager.resolveService(service, resolveListener)
                                viewModel.loading.postValue(false)
                            }
                        }
                    }

                    override fun onServiceLost(service: NsdServiceInfo) {
                        Log.e(tag, "service lost: $service")
                        viewModel.loading.postValue(false)
                    }

                    override fun onDiscoveryStopped(serviceType: String) {
                        Log.i(tag, "Discovery stopped: $serviceType")
                        lifecycleScope.launch {
                            withContext(Dispatchers.Main) {
                                binding.serverSelectIPInput.error = getString(R.string.not_found_on_local_network)
                            }
                        }
                        viewModel.loading.postValue(false)
                    }

                    override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                        lifecycleScope.launch {
                            withContext(Dispatchers.Main) {
                                binding.serverSelectIPInput.error = getString(R.string.scan_failed)
                            }
                        }
                        viewModel.loading.postValue(false)
                        Log.e(tag, "Discovery failed: Error code:$errorCode")
                        nsdManager.stopServiceDiscovery(this)
                    }

                    override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                        lifecycleScope.launch {
                            withContext(Dispatchers.Main) {
                                binding.serverSelectIPInput.error = getString(R.string.scan_failed)
                            }
                        }
                        viewModel.loading.postValue(false)
                        Log.e(tag, "Discovery failed: Error code:$errorCode")
                        nsdManager.stopServiceDiscovery(this)
                    }
                }
                nsdManager.discoverServices("_raspio._tcp", NsdManager.PROTOCOL_DNS_SD, discoveryListener)
                delay(5000)
                if (!finished) {
                    nsdManager.stopServiceDiscovery(discoveryListener)
                    viewModel.loading.postValue(false)
                }
            }
        }
    }

    private fun lookupDevice() {
        viewModel.loading.value = true
        val deviceFilter: WifiDeviceFilter = WifiDeviceFilter.Builder()
            //.setNamePattern(Pattern.compile("RASPIO_"))
            .build()
        val pairingRequest: AssociationRequest = AssociationRequest.Builder()
            .addDeviceFilter(deviceFilter)
            .build()
        val deviceManager: CompanionDeviceManager = getSystemService(COMPANION_DEVICE_SERVICE) as CompanionDeviceManager
        deviceManager.associate(pairingRequest,
            object : CompanionDeviceManager.Callback() {
                override fun onDeviceFound(chooserLauncher: IntentSender) {
                    viewModel.loading.value = false
                    temp.launch(IntentSenderRequest.Builder(chooserLauncher).build())
                }

                override fun onFailure(error: CharSequence?) {
                    viewModel.loading.value = false
                    Toast.makeText(this@ServerSelectActivity, "No WIFI found in area", Toast.LENGTH_LONG).show()
                }
            }, null
        )


    }

    private suspend fun checkExisting(binding: ActivityServerSelectBinding) {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val address = sharedPrefs.getString("ip", null)

        if (address == null) { // No address saved
            viewModel.loading.value = false
            return
        }

        if (checkConnection(address)) {
            // Connection established
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            // Connection failed
            binding.serverSelectIPInput.error = getString(R.string.shr_error_ip)
            return
        }
    }

    private suspend fun checkConnection(ip: String): Boolean {
        Log.i(tag, "Checking connection to $ip...")
        viewModel.loading.value = true

        @Suppress("HttpUrlsUsage")
        ApiHolder.decorator = RaspioServerDecorator("http://$ip:3000")

        return try {
            NetworkApi.getPing().isSuccessful.also {
                Log.i(tag, "Connection to $ip success: $it")
            }
        } catch (e: SocketTimeoutException) {
            Log.i(tag, "Connection to $ip timed out.")
            false
        } catch (e: ConnectException) {
            Log.i(tag, "Connection to $ip could not be established.")
            false
        }.also {
            viewModel.loading.value = false
        }
    }
}