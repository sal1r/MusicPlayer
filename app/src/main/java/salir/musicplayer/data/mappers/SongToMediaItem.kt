package salir.musicplayer.data.mappers

import androidx.media3.common.MediaItem
import salir.musicplayer.domain.models.Song

fun Song.toMediaItem(): MediaItem = MediaItem.Builder()
    .setUri(uri)
    .setMediaId(id.toString())
    .build()