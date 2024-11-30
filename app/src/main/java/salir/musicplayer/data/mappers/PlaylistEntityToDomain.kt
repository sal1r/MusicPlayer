package salir.musicplayer.data.mappers

import salir.musicplayer.data.database.PlaylistEntity
import salir.musicplayer.domain.models.Playlist
import salir.musicplayer.domain.models.Song

fun PlaylistEntity.toDomain() = Playlist(
    id = id,
    name = name,
    imageUri = imageUri
)
