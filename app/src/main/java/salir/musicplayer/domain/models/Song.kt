package salir.musicplayer.domain.models

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap

@Immutable
data class Song(
    val id: Long,
    val uri: String,
    val title: String,
    val artist: String,
    val duration: Int,
    val image: ImageBitmap?,
    val album: String
)