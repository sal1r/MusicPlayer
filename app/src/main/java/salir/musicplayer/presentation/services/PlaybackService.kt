package salir.musicplayer.presentation.services

import android.app.PendingIntent
import android.content.Intent
import android.media.audiofx.Equalizer
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.MediaSession.ConnectionResult.AcceptedResultBuilder
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionCommands
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import salir.musicplayer.data.repositories.PlayerSettingsRepositoryImpl
import salir.musicplayer.data.repositories.SongsRepositoryImpl
import salir.musicplayer.domain.models.PlayerSetting
import salir.musicplayer.domain.repositories.EqualizerSettingsRepository
import salir.musicplayer.domain.repositories.PlayerSettingsRepository
import salir.musicplayer.domain.repositories.SongsRepository
import salir.musicplayer.domain.usecases.GetSongsByIdUseCase
import salir.musicplayer.domain.usecases.SavePlayerSettingsUseCase

class PlaybackService : MediaSessionService() {

    private val savePlayerSettingsUseCase: SavePlayerSettingsUseCase by inject()
    private val equalizerSettingsRepository: EqualizerSettingsRepository by inject()

    private var session: MediaSession? = null
    private var player: ExoPlayer? = null
    private var equalizer: Equalizer? = null

    private val job = SupervisorJob()

    private val coroutineScope = CoroutineScope(job)

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this).build()

        equalizer = Equalizer(0, player!!.audioSessionId)

        coroutineScope.launch(Dispatchers.IO) {
            equalizer!!.enabled = equalizerSettingsRepository.getEnabled()

            for (band in 0..4) equalizer!!.setBandLevel(
                band.toShort(),
                equalizerSettingsRepository.getBandLevel(band.toShort())
            )
        }

        player!!.prepare()

        player!!.addListener(
            object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    mediaItem?.let {
                        savePlayerSettingsUseCase(PlayerSetting.CURRENT_SONG_ID, it.mediaId.toLong())
                    }
                }

                override fun onRepeatModeChanged(repeatMode: Int) {
                    super.onRepeatModeChanged(repeatMode)
                    savePlayerSettingsUseCase(PlayerSetting.REPEAT_MODE, repeatMode)
                }
            }
        )

        val intent = packageManager.getLaunchIntentForPackage(packageName)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent, PendingIntent.FLAG_IMMUTABLE
        )

        session = MediaSession.Builder(this, player!!)
            .setCallback(SessionCallback())
            .setSessionActivity(pendingIntent)
            .build()
    }

    inner class SessionCallback : MediaSession.Callback {

        @OptIn(UnstableApi::class)
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult = AcceptedResultBuilder(session)
            .setAvailableSessionCommands(
                ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                    .add(SessionCommand(SET_EQUALIZER_BAND_LEVEL_COMMAND, Bundle.EMPTY))
                    .add(SessionCommand(SET_EQUALIZER_ENABLED_COMMAND, Bundle.EMPTY))
                    .build()
            ).build()

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            return when (customCommand.customAction) {
                SET_EQUALIZER_BAND_LEVEL_COMMAND -> {
                    val band = args.getShort(EQUALIZER_BAND_PARAM)
                    val level = args.getShort(EQUALIZER_BAND_LEVEL_PARAM)
                    equalizer?.setBandLevel(band, level)

                    coroutineScope.launch(Dispatchers.IO) {
                        equalizerSettingsRepository.setBandLevel(band, level)
                    }

                    Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }

                SET_EQUALIZER_ENABLED_COMMAND -> {
                    val enabled = args.getBoolean(EQUALIZER_ENABLED_PARAM)
                    equalizer?.enabled = enabled

                    coroutineScope.launch(Dispatchers.IO) {
                        equalizerSettingsRepository.setEnabled(enabled)
                    }

                    Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
                }

                else -> super.onCustomCommand(session, controller, customCommand, args)
            }
        }
    }

    companion object {
        const val SET_EQUALIZER_BAND_LEVEL_COMMAND = "SET_EQUALIZER_BAND_LEVEL_COMMAND"
        const val EQUALIZER_BAND_PARAM = "EQUALIZER_BAND_PARAM"
        const val EQUALIZER_BAND_LEVEL_PARAM = "EQUALIZER_BAND_LEVEL_PARAM"

        const val SET_EQUALIZER_ENABLED_COMMAND = "SET_EQUALIZER_ENABLED_COMMAND"
        const val EQUALIZER_ENABLED_PARAM = "EQUALIZER_ENABLED_PARAM"
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = session?.player!!
        if (!player.playWhenReady
            || player.mediaItemCount == 0
            || player.playbackState == Player.STATE_ENDED) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        job.cancel()

        equalizer?.release()
        session?.run {
            player.release()
            release()
            session = null
        }

        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        session
}