package salir.musicplayer.domain.models

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Immutable
data class Playlist(
    val id: Long,
    val name: String,
    val imageUri: String? = null
)