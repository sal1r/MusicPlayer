package salir.musicplayer.data.mappers

import salir.musicplayer.data.database.PlaylistEntity
import salir.musicplayer.domain.models.Playlist

fun Playlist.toEntity(withId: Boolean = true): PlaylistEntity =
    if (withId) PlaylistEntity(
        id = id,
        name = name,
        imageUri = imageUri
    ) else PlaylistEntity(
        name = name,
        imageUri = imageUri
    )