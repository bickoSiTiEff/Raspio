package me.bickositieff.raspio

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.media.session.MediaButtonReceiver
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import me.bickositieff.raspio.generated.api.PlaybackApi
import me.bickositieff.raspio.generated.models.GETPlaybackResponse
import me.bickositieff.raspio.generated.models.GETPlaybackResponseState
import me.bickositieff.raspio.ui.models.Song
import me.bickositieff.raspio.ui.playback.PlaybackViewModel
import me.bickositieff.raspio.ui.playlist.PlaylistViewModel

class MainActivity : AppCompatActivity() {

    private val playback: PlaybackViewModel by viewModels()
    private val playlist: PlaylistViewModel by viewModels()

    private lateinit var mediaSession: MediaSessionCompat

    private val mediaSessionSupportedActions = PlaybackStateCompat.ACTION_PLAY or
            PlaybackStateCompat.ACTION_PAUSE or
            PlaybackStateCompat.ACTION_PLAY_PAUSE or
            PlaybackStateCompat.ACTION_STOP or
            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
            PlaybackStateCompat.ACTION_SEEK_TO or
            PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE or
            PlaybackStateCompat.ACTION_SET_REPEAT_MODE

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

        setupMediaSession()
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("notification_toggle", true))
            sendMediaNotification()
    }

    private fun setupMediaSession() {
        mediaSession = MediaSessionCompat(this, "RaspioMediaSession").apply {
            setMediaButtonReceiver(null)

            setPlaybackState(createPlaybackState(playback.playbackState.value))

            setCallback(MediaCallback())
        }

        playback.playbackState.observe(this) {
            mediaSession.setPlaybackState(createPlaybackState(it))
            mediaSession.setMetadata(
                createMetadataState(
                    playlist.playlist.value?.getOrNull(
                        it.currentlyPlayingIndex?.toInt() ?: return@observe
                    )
                ) ?: return@observe
            )
        }

        playlist.playlist.observe(this) {
            mediaSession.setMetadata(
                createMetadataState(
                    it.getOrNull(playback.playbackState.value?.currentlyPlayingIndex?.toInt() ?: return@observe)
                )
            )
        }

        mediaSession.isActive = true
    }

    private fun createPlaybackState(state: GETPlaybackResponse?): PlaybackStateCompat {
        val currentPlaybackState = when (state?.state) {
            GETPlaybackResponseState.PLAY -> PlaybackStateCompat.STATE_PLAYING
            GETPlaybackResponseState.STOP -> PlaybackStateCompat.STATE_STOPPED
            GETPlaybackResponseState.PAUSE -> PlaybackStateCompat.STATE_PAUSED
            else -> null
        }
        val builder = PlaybackStateCompat.Builder()
            .setActions(mediaSessionSupportedActions)

        if (currentPlaybackState != null && state != null)
            builder.setState(
                currentPlaybackState,
                state.elapsed?.toLong()?.times(1000) ?: PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                1.0f
            )

        return builder.build()
    }

    private fun createMetadataState(song: Song?): MediaMetadataCompat? = song?.run {
        MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, path)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, (duration * 1000).toLong())
            .build()
    }

    private fun sendMediaNotification() {
        val notificationManager = getSystemService<NotificationManager>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "raspio-media-notification",
                getString(R.string.media_notification_channel_description),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = getString(R.string.media_notification_channel_description)
            notificationManager?.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, "raspio-media-notification").apply {
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            setSmallIcon(R.drawable.outline_radio_24)

            addAction(
                NotificationCompat.Action(
                    R.drawable.outline_pause_24,
                    getString(R.string.play_pause_playback),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this@MainActivity,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                    )
                )
            )

            addAction(
                NotificationCompat.Action(
                    R.drawable.outline_skip_next_24,
                    getString(R.string.skip_song),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this@MainActivity,
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    )
                )
            )

            addAction(
                NotificationCompat.Action(
                    R.drawable.outline_skip_previous_24,
                    getString(R.string.skip_song),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this@MainActivity,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    )
                )
            )

            addAction(
                NotificationCompat.Action(
                    R.drawable.outline_stop_24,
                    getString(R.string.skip_song),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this@MainActivity,
                        PlaybackStateCompat.ACTION_STOP
                    )
                )
            )

            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0)
            )
        }.build()

        val playbackNotificationID = 18769
        notificationManager?.notify(playbackNotificationID, notification)
    }

    inner class MediaCallback : MediaSessionCompat.Callback() {
        override fun onPlay() {
            lifecycleScope.launch {
                PlaybackApi.postPlaybackPlay()
            }
        }

        override fun onPause() {
            lifecycleScope.launch {
                PlaybackApi.postPlaybackPause()
            }
        }

        override fun onStop() {
            lifecycleScope.launch {
                PlaybackApi.postPlaybackStop()
            }
        }

        override fun onSkipToPrevious() {
            lifecycleScope.launch {
                PlaybackApi.postPlaybackPrevious()
            }
        }

        override fun onSkipToNext() {
            lifecycleScope.launch {
                PlaybackApi.postPlaybackNext()
            }
        }

        override fun onSeekTo(pos: Long) {
            lifecycleScope.launch {
                PlaybackApi.postPlaybackSeek(pos / 1000.0f)
            }
        }

        override fun onSetShuffleMode(shuffleMode: Int) {
            lifecycleScope.launch {
                PlaybackApi.postPlaybackShuffle(shuffleMode != PlaybackStateCompat.SHUFFLE_MODE_NONE)
            }
        }

        override fun onSetRepeatMode(repeatMode: Int) {
            lifecycleScope.launch {
                PlaybackApi.postPlaybackRepeat(repeatMode != PlaybackStateCompat.REPEAT_MODE_NONE)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.isActive = false
        mediaSession.release()
    }

    override fun onSupportNavigateUp(): Boolean {
        return (Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp()
                || super.onSupportNavigateUp())
    }
}
