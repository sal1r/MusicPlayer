package salir.musicplayer.presentation.viewmodels

import android.app.Application
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import salir.musicplayer.R
import salir.musicplayer.domain.models.Song

class FastPlayerViewModel(
    private val app: Application
): ViewModel() {

    var isPlaying by mutableStateOf(false)
        private set

    var progress: Float by mutableStateOf(0f)
        private set
    private var isProgressInChange: Boolean = false

    var songImage: ImageBitmap? by mutableStateOf(null)
        private set

    var currentSong: Song? by mutableStateOf(null)
        private set

    val player: ExoPlayer = ExoPlayer.Builder(app).build()

    val playerProgressWatcher: Job = playerProgressWatcher()

    @OptIn(UnstableApi::class)
    fun setAudioUri(uri: Uri) {
        if (uri.path == null) return

        viewModelScope.launch(Dispatchers.IO) {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(app, uri)
            val bytes = mmr.embeddedPicture

            currentSong = Song(
                id = 0,
                uri = uri.toString(),
                title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: app.getString(R.string.no_title),
                artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: app.getString(R.string.no_artist),
                duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toIntOrNull() ?: 0,
                image = null,
                album = ""
            )

            songImage = bytes?.let {
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
            }

            mmr.release()
        }

        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.play()

        isPlaying = true

    }

    fun changePlayingState() {
        isPlaying = !isPlaying
        player.playWhenReady = isPlaying
    }

    fun setProgressWithoutSeek(progress: Float) {
        isProgressInChange = true
        this.progress = progress
    }

    fun endProgressChange() {
        isProgressInChange = false
        player.seekTo((progress * player.duration).toLong())
    }

    private fun playerProgressWatcher(): Job {
        return viewModelScope.launch(Dispatchers.Main) {
            while (true) {
                if (!isProgressInChange) {
                    progress = player.currentPosition.toFloat() / player.duration
                }
                withContext(Dispatchers.Default) {
                    delay(1000)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        player.release()
    }
}