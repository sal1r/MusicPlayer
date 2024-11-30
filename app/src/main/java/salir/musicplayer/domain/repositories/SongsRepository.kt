package salir.musicplayer.domain.repositories

import salir.musicplayer.domain.models.Song

interface SongsRepository {

    fun getAllSongs(): List<Song>

    fun getSongById(songId: Long): Song?
}