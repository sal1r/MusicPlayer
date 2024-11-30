package salir.musicplayer.data.repositories

import salir.musicplayer.data.database.Database
import salir.musicplayer.data.database.SongEntity
import salir.musicplayer.data.mappers.toDomain
import salir.musicplayer.data.mappers.toEntity
import salir.musicplayer.domain.models.Playlist
import salir.musicplayer.domain.repositories.PlaylistsRepository

class PlaylistsRepositoryImpl(
    private val db: Database
): PlaylistsRepository {

    private val playlistsDao = db.playlistsDao()
    private val songsDao = db.songsDao()

    private val cache: MutableMap<Long, List<Long>?> = mutableMapOf()

    override fun getPlaylists(): List<Playlist> =
        playlistsDao.getPlaylists().map {it.toDomain() }

    override fun getSongsIdsFromPlaylist(playlist: Playlist): List<Long> =
        cache[playlist.id] ?: songsDao.getSongsForPlaylist(playlist.id).map { it.songId }.let {
            cache[playlist.id] = it
            it
        }

    override fun insertPlaylist(playlist: Playlist): Long =
        playlistsDao.insertPlaylist(playlist.toEntity(withId = false))

    override fun updatePlaylist(playlist: Playlist) {
        playlistsDao.updatePlaylist(playlist.toEntity())
    }

    override fun deletePlaylist(playlist: Playlist) {
        playlistsDao.deletePlaylist(playlist.toEntity())
    }

    override fun addSongToPlaylist(playlist: Playlist, songId: Long) {
        if (songsDao.getSongOrNull(playlist.id, songId) == null) {
            val position = songsDao.getLastPosition(playlist.id)?.plus(1) ?: 0
            songsDao.addSong(
                SongEntity(
                    position = position,
                    songId = songId,
                    playlistId = playlist.id
                )
            )

            cache[playlist.id] = null
        }
    }

    override fun addSongsToPlaylist(playlist: Playlist, songsIds: List<Long>) {
        var lastPosition = songsDao.getLastPosition(playlist.id)?.plus(1) ?: 0

        songsDao.addSongs(songsIds.mapNotNull {
            if (songsDao.getSongOrNull(playlist.id, it) == null) {
                SongEntity(
                    position = lastPosition++,
                    songId = it,
                    playlistId = playlist.id
                )
            } else null
        })

        cache[playlist.id] = null
    }

    override fun deleteSongFromPlaylist(playlist: Playlist, songId: Long) {
        songsDao.deleteSongFromPlaylist(
            playlistId = playlist.id,
            songId = songId
        )
        cache[playlist.id] = null
    }

    override fun getPlaylistById(id: Long): Playlist =
        playlistsDao.getPlaylistById(id).toDomain()

    override fun moveSongsInPlaylist(playlist: Playlist, songId: Long, toPosition: Int) {
        songsDao.moveSong(playlist.id, songId, toPosition)
        cache[playlist.id] = null
    }
}