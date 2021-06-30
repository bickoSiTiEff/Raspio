package me.bickositieff.raspio

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import me.bickositieff.raspio.ui.playback.PlaybackViewModel

class MainActivity : AppCompatActivity() {

    private val playback: PlaybackViewModel by viewModels()

    private lateinit var mediaSession: MediaSessionCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_playlist, R.id.navigation_server_controls, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        mediaSession = MediaSessionCompat(this, "RaspioMediaSession").apply {
            setMediaButtonReceiver(null)

            setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setActions(
                        PlaybackStateCompat.ACTION_PLAY or
                                PlaybackStateCompat.ACTION_PAUSE or
                                PlaybackStateCompat.ACTION_STOP or
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                                PlaybackStateCompat.ACTION_SEEK_TO or
                                PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE or
                                PlaybackStateCompat.ACTION_SET_REPEAT_MODE
                    )
                    .build()
            )

            setCallback(MediaCallback())
        }

        mediaSession.isActive = true
    }

    inner class MediaCallback : MediaSessionCompat.Callback() {
        override fun onPlay() {
            TODO()
        }

        override fun onPause() {
            TODO()
        }

        override fun onStop() {
            TODO()
        }

        override fun onSkipToPrevious() {
            TODO()
        }

        override fun onSkipToNext() {
            TODO()
        }

        override fun onSeekTo(pos: Long) {
            TODO()
        }

        override fun onSetShuffleMode(shuffleMode: Int) {
            TODO()
        }

        override fun onSetRepeatMode(repeatMode: Int) {
            TODO()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
    }

    override fun onSupportNavigateUp(): Boolean {
        return (Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp()
                || super.onSupportNavigateUp())
    }
}
