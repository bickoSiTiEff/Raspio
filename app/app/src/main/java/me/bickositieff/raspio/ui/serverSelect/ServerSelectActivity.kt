package me.bickositieff.raspio.ui.serverSelect

import android.content.Context
import android.content.Intent
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    private val viewModel: ServerSelectViewModel by viewModels()

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