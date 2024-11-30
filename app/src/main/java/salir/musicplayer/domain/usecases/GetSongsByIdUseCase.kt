package salir.musicplayer.domain.usecases

import android.util.Log
import salir.musicplayer.domain.models.Song
import salir.musicplayer.domain.repositories.SongsRepository

class GetSongsByIdUseCase(
    private val repository: SongsRepository
) {
    operator fun invoke(songId: Long): Song? = repository.getSongById(songId)

    operator fun invoke(vararg songsIds: Long): List<Song> =
        songsIds.map { repository.getSongById(it) }.filterNotNull()

    operator fun invoke(songsIds: List<Long>): List<Song> =
        songsIds.mapNotNull { repository.getSongById(it) }
}