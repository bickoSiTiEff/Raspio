package me.bickositieff.raspio.ui.serverSelect

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.launch
import me.bickositieff.raspio.MainActivity
import me.bickositieff.raspio.R
import me.bickositieff.raspio.api.RaspioServerDecorator
import me.bickositieff.raspio.databinding.ActivityServerSelectBinding
import me.bickositieff.raspio.generated.ApiHolder
import me.bickositieff.raspio.generated.api.NetworkApi
import java.net.ConnectException
import java.net.InetAddress
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

        binding.serverSelectIPScan.setOnClickListener {
            lifecycleScope.launch {
                val nsdManager = (getSystemService(Context.NSD_SERVICE) as NsdManager)
                val resolveListener = object : NsdManager.ResolveListener {

                    override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                        Toast.makeText(this@ServerSelectActivity, "Couldn't resolve IP of Raspberry Pi", Toast.LENGTH_LONG).show()
                        Log.e(TAG, "Resolve failed: $errorCode")
                    }

                    override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                        Log.e(TAG, "Resolve Succeeded. $serviceInfo")
                        Toast.makeText(this@ServerSelectActivity, "Resolved IP of local Raspberry Pi", Toast.LENGTH_LONG).show()
                        binding.serverSelectIP.setText(serviceInfo.host.toString())
                    }
                }

                val discoveryListener = object : NsdManager.DiscoveryListener {
                    override fun onDiscoveryStarted(regType: String) {
                        Log.d(TAG, "Service discovery started")
                    }

                    override fun onServiceFound(service: NsdServiceInfo) {
                        Log.d(TAG, "Service discovery success$service")
                        when {
                            service.serviceType != "_raspio._tcp." ->
                                Log.d(TAG, "Unknown Service Type: ${service.serviceType}")

                            service.serviceType == "_raspio._tcp." -> {
                                nsdManager.stopServiceDiscovery(this)
                                nsdManager.resolveService(service, resolveListener)
                            }
                        }
                    }

                    override fun onServiceLost(service: NsdServiceInfo) {
                        Log.e(TAG, "service lost: $service")
                    }

                    override fun onDiscoveryStopped(serviceType: String) {
                        Log.i(TAG, "Discovery stopped: $serviceType")
                    }

                    override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                        Toast.makeText(this@ServerSelectActivity, "Scan for Raspberry Pi failed", Toast.LENGTH_LONG).show()
                        Log.e(TAG, "Discovery failed: Error code:$errorCode")
                        nsdManager.stopServiceDiscovery(this)
                    }

                    override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                        Toast.makeText(this@ServerSelectActivity, "Scan for Raspberry Pi failed", Toast.LENGTH_LONG).show()
                        Log.e(TAG, "Discovery failed: Error code:$errorCode")
                        nsdManager.stopServiceDiscovery(this)
                    }
                }
                nsdManager.discoverServices("_raspio._tcp", NsdManager.PROTOCOL_DNS_SD, discoveryListener)
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