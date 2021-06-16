package me.bickositieff.raspio.ui.serverSelect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import me.bickositieff.raspio.MainActivity
import me.bickositieff.raspio.R
import me.bickositieff.raspio.api.RaspioServerDecorator
import me.bickositieff.raspio.databinding.ActivityServerSelectBinding
import me.bickositieff.raspio.generated.ApiHolder
import me.bickositieff.raspio.generated.api.NetworkApi
import java.net.SocketTimeoutException

class ServerSelectActivity : AppCompatActivity() {
    private val tag = "ServerSelectActivity"

    private val viewModel: ServerSelectViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenCreated { checkExisting() }

        val binding: ActivityServerSelectBinding = DataBindingUtil.setContentView(this, R.layout.activity_server_select)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.serverSelectConfirm.setOnClickListener {
            lifecycleScope.launch() {
                val ip = binding.serverSelectIP.text.toString()

                val success = checkConnection(ip)

                if (success) {
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
                    Snackbar.make(findViewById(android.R.id.content), R.string.server_timeout, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun checkExisting() {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val address = sharedPrefs.getString("ip", null)
            ?: return // No address saved

        if (checkConnection(address)) {
            // Connection established
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            // Connection failed
            Snackbar.make(findViewById(android.R.id.content), R.string.server_timeout, Snackbar.LENGTH_SHORT).show()
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
        }.also {
            viewModel.loading.value = false
        }
    }
}