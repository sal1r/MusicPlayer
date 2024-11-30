package salir.musicplayer.presentation.viewmodels

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import salir.musicplayer.data.mappers.toMediaItem
import salir.musicplayer.domain.models.PlayerSetting
import salir.musicplayer.domain.models.Song
import salir.musicplayer.domain.repositories.EqualizerSettingsRepository
import salir.musicplayer.domain.repositories.PlayerSettingsRepository
import salir.musicplayer.domain.repositories.SongsRepository
import salir.musicplayer.domain.usecases.GetSongsByIdUseCase
import salir.musicplayer.domain.usecases.SavePlayerSettingsUseCase
import salir.musicplayer.presentation.services.PlaybackService

val equalizerFrequencies: List<Int> = listOf(60000, 230000, 910000, 3600000, 14000000)

// TODO: вынести стейт плеера в отдельный класс
class PlayerViewModel(
    private val context: Context,
    private val playerSettingsRepository: PlayerSettingsRepository,
    private val songsRepository: SongsRepository,
    private val equalizerSettingsRepository: EqualizerSettingsRepository
): ViewModel() {

    private val savePlayerSettingsUseCase = SavePlayerSettingsUseCase(playerSettingsRepository)

    private val getSongsByIdUseCase = GetSongsByIdUseCase(songsRepository)

    private val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
    private val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

    private var player: Player? = null

    private var controller: MediaController? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying

    var queue by mutableStateOf<List<Song>>(listOf())
        private set

    private var queueInMediaItems: List<MediaItem> = listOf()

    private var originalQueue: List<Song> = listOf()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong = _currentSong.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

    private var isProgressInChange: Boolean = false

    var playerProgressWatcher: Job = playerProgressWatcher()

    val showBigPlayer = MutableStateFlow(false)

    private val _isShuffled = MutableStateFlow(false)
    val isShuffled = _isShuffled.asStateFlow()

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode = _repeatMode.asStateFlow()

    val equalizerSetting = List(5) {
        MutableStateFlow(0f)
    }

    private val _equalizerEnabled = MutableStateFlow(false)
    val equalizerEnabled = _equalizerEnabled.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            restorePlayerSettings()

            controllerFuture.addListener({
                controller = controllerFuture.get()
                player = controller

                player!!.let { player ->
                    val mi = player.currentMediaItem
                    if (mi != null) {
                        _isPlaying.value = player.isPlaying
                        _progress.value = player.currentPosition.toFloat() / player.duration
                        _currentSong.value = getSongsByIdUseCase(mi.mediaId.toLong())
                        _repeatMode.value = player.repeatMode
                    } else {
                        player.setMediaItems(queueInMediaItems)
                        playerSettingsRepository.getSetting(PlayerSetting.CURRENT_SONG_ID, 0L).let { id ->
                            setSong(queue.find { it.id == id })
                        }
                        playerSettingsRepository.getSetting(PlayerSetting.REPEAT_MODE, Player.REPEAT_MODE_OFF).let {
                            player.repeatMode = it
                            _repeatMode.value = it
                        }
                    }
                    player.prepare()
                }

                player!!.addListener(
                    object : Player.Listener {
                        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                            super.onPlayWhenReadyChanged(playWhenReady, reason)
                            this@PlayerViewModel._isPlaying.value = playWhenReady
                        }

                        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                            super.onMediaItemTransition(mediaItem, reason)
                            mediaItem?.let { mi ->
                                _currentSong.value = queue.find { it.id == mi.mediaId.toLong() }
                            }
                        }
                    }
                )
            }, ContextCompat.getMainExecutor(context))
        }

        loadEqualizerSettings()
    }

    override fun onCleared() {
        saveQueueSettings()

        MediaController.releaseFuture(controllerFuture)

        super.onCleared()
    }

    fun loadEqualizerSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            _equalizerEnabled.value = equalizerSettingsRepository.getEnabled()

            val bandLevels = (0..4).map {
                equalizerSettingsRepository.getBandLevel(it.toShort()).toFloat()
            }

            withContext(Dispatchers.Main) {
                bandLevels.forEachIndexed { band, level ->
                    equalizerSetting[band].value = level
                }
            }
        }
    }

    fun setEqualizerBandLevel(band: Short, level: Short) {
        controller?.sendCustomCommand(
            SessionCommand(PlaybackService.SET_EQUALIZER_BAND_LEVEL_COMMAND, Bundle.EMPTY),
            Bundle().apply {
                putShort(PlaybackService.EQUALIZER_BAND_PARAM, band)
                putShort(PlaybackService.EQUALIZER_BAND_LEVEL_PARAM, level)
            }
        )
    }

    fun applyEqualizerSetting() {
        equalizerSetting.forEachIndexed { band, level ->
            setEqualizerBandLevel(band.toShort(), level.value.toInt().toShort())
        }
    }

    fun resetEqualizerSetting() {
        equalizerSetting.forEachIndexed { band, _ ->
            equalizerSetting[band].value = 0f
            setEqualizerBandLevel(band.toShort(), 0)
        }
    }

    fun setEqualizerEnabledState(enabled: Boolean) {
        _equalizerEnabled.value = enabled

        controller?.sendCustomCommand(
            SessionCommand(PlaybackService.SET_EQUALIZER_ENABLED_COMMAND, Bundle.EMPTY),
            Bundle().apply { putBoolean(PlaybackService.EQUALIZER_ENABLED_PARAM, enabled) }
        )
    }

    private fun saveQueueSettings() {
        savePlayerSettingsUseCase(
            PlayerSetting.IS_SHUFFLED to _isShuffled.value,
            PlayerSetting.ORIGINAL_QUEUE to originalQueue.joinToString(" ") { it.id.toString() },
            PlayerSetting.QUEUE to queue.joinToString(" ") { it.id.toString() }
        )
    }

    private fun restorePlayerSettings() {
        _isShuffled.value = playerSettingsRepository.getSetting(PlayerSetting.IS_SHUFFLED, false)
        originalQueue = getSongsByIdUseCase(
            playerSettingsRepository.getSetting(PlayerSetting.ORIGINAL_QUEUE, "")
                .split(" ")
                .mapNotNull {
                    try { it.toLong() }
                    catch (e: NumberFormatException) { null }
                }
        )
        queue = getSongsByIdUseCase(
            playerSettingsRepository.getSetting(PlayerSetting.QUEUE, "")
                .split(" ")
                .mapNotNull {
                    try { it.toLong() }
                    catch (e: NumberFormatException) { null }
                }
        )
        queueInMediaItems = queue.map { it.toMediaItem() }
    }

    fun setNextRepeatState() {
        player?.let { player ->
            when (player.repeatMode) {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                else -> Player.REPEAT_MODE_OFF
            }.let {
                _repeatMode.value = it
                player.repeatMode = it
            }
        }
    }

    fun setShuffledState(isShuffled: Boolean) {
        this._isShuffled.value = isShuffled

         when (isShuffled) {
            true -> originalQueue.shuffled()
            false -> originalQueue
        }.let { q ->
             queue = if (!isShuffled) {
                 q
             } else {
                 _currentSong.value?.let { currentSong ->
                     q.toMutableList().apply {
                         removeIf { it.id == currentSong.id }
                         add(0, currentSong)
                     }
                 } ?: q
             }

             queueInMediaItems = queue.map { it.toMediaItem() }

             player?.let { player ->
                 val cs = _currentSong.value
                 val cp = player.currentPosition
                 player.setMediaItems(queueInMediaItems)
                 setSong(cs)
                 player.seekTo(cp)
             }
        }

        saveQueueSettings()
    }

    fun setPlaylist(playlist: List<Song>, song: Song?) {
        player?.let { player ->
            val mediaItems = playlist.map { it.toMediaItem() }

            originalQueue = playlist
            queue = originalQueue
            queueInMediaItems = mediaItems
            _isShuffled.value = false

            saveQueueSettings()

            player.setMediaItems(mediaItems)
            setSong(song)
        }
    }

    private fun setSong(song: Song?) {
        player?.let { player ->
            _currentSong.value = song
            song?.let { song ->
                val songIndex = queueInMediaItems.indexOfFirst {
                    it.mediaId.toLong() == song.id
                }.let {
                    if (it == -1) error("can't find song in playlist")
                    it
                }

                player.seekTo(songIndex, 0)
                player.prepare()
            }
        }
    }

    fun setNextSong(song: Song) {
        if (queue.isEmpty()) return
        if (_currentSong.value?.id == song.id) return

        queue = queue.toMutableList().apply {
            removeIf { it.id == song.id }
            add(indexOf(_currentSong.value) + 1, song)
        }
        queueInMediaItems = queue.map { it.toMediaItem() }

        player?.let { player ->
            val cs = _currentSong.value
            val cp = player.currentPosition
            player.setMediaItems(queueInMediaItems)
            setSong(cs)
            player.seekTo(cp)
        }

        saveQueueSettings()
    }

    fun addSongToQueue(song: Song) {
        if (queue.isEmpty()) return
        if (_currentSong.value?.id == song.id) return

        queue = queue.toMutableList().apply {
            removeIf { it.id == song.id }
            add(song)
        }
        queueInMediaItems = queue.map { it.toMediaItem() }

        player?.setMediaItems(queueInMediaItems)

        saveQueueSettings()
    }

    fun nextSong() {
        player?.seekToNext()
        _progress.value = 0f
    }

    fun previousSong() {
        player?.seekToPrevious()
        _progress.value = 0f
    }

    fun setPlayingState(isPlaying: Boolean) {
        player?.let { player ->
            player.playWhenReady = isPlaying
            this._isPlaying.value = isPlaying
        }
    }

    private fun playerProgressWatcher(): Job {
        return viewModelScope.launch(Dispatchers.Main) {
            while (true) {
                player?.let { player ->
                    if (!isProgressInChange) {
                        _progress.value = player.currentPosition.toFloat() / player.duration
                    }
                }
                withContext(Dispatchers.Default) {
                    delay(1000)
                }
            }
        }
    }

    fun setProgressWithoutSeek(progress: Float) {
        player?.let {
            isProgressInChange = true
            this._progress.value = progress
        }
    }

    fun endProgressChange() {
        player?.let { player ->
            isProgressInChange = false
            player.seekTo((_progress.value * player.duration).toLong())
        }
    }
}