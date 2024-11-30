package salir.musicplayer.domain.repositories

import salir.musicplayer.domain.models.Playlist

interface PlaylistsRepository {

    fun getPlaylists(): List<Playlist>

    fun getSongsIdsFromPlaylist(playlist: Playlist): List<Long>

    fun insertPlaylist(playlist: Playlist): Long

    fun updatePlaylist(playlist: Playlist)

    fun deletePlaylist(playlist: Playlist)

    fun addSongToPlaylist(playlist: Playlist, songId: Long)

    fun addSongsToPlaylist(playlist: Playlist, songsIds: List<Long>)

    fun deleteSongFromPlaylist(playlist: Playlist, songId: Long)

    fun getPlaylistById(id: Long): Playlist

    fun moveSongsInPlaylist(playlist: Playlist, songId: Long, toPosition: Int)
}