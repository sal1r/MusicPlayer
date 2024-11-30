package salir.musicplayer.data.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Entity(
    tableName = "Songs",
    foreignKeys = [ForeignKey(
        entity = PlaylistEntity::class,
        parentColumns = ["id"],
        childColumns = ["playlistId"],
        onDelete = ForeignKey.CASCADE,
    )],
    primaryKeys = ["position", "playlistId", "songId"],
)
data class SongEntity(
    val position: Int = 0,
    val playlistId: Long,
    val songId: Long,
)

@Dao
abstract class SongsDao {
    @Query("SELECT * FROM Songs WHERE playlistId = :playlistId ORDER BY position ASC")
    abstract fun getSongsForPlaylist(playlistId: Long): List<SongEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun addSong(song: SongEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun addSongs(songs: List<SongEntity>)

    @Query("UPDATE Songs SET position = position - 1 WHERE playlistId = :playlistId AND position > (SELECT position FROM Songs WHERE playlistId = :playlistId AND songId = :songId)")
    abstract fun _decSongsPositions(playlistId: Long, songId: Long)

    @Query("DELETE FROM Songs WHERE playlistId = :playlistId AND songId = :songId")
    abstract fun _dltSongByIds(playlistId: Long, songId: Long)

    @Transaction
    open fun deleteSongFromPlaylist(playlistId: Long, songId: Long) {
        _decSongsPositions(playlistId, songId)
        _dltSongByIds(playlistId, songId)
    }

    @Query("SELECT * FROM Songs WHERE playlistId = :playlistId AND songId = :songId")
    abstract fun getSongOrNull(playlistId: Long, songId: Long): SongEntity?

    @Query("UPDATE Songs SET position = position + 1 WHERE playlistId = :playlistId AND position >= :toPosition")
    abstract fun _incSongsPositions(playlistId: Long, toPosition: Int)

    @Query("UPDATE Songs SET position = :toPosition WHERE playlistId = :playlistId AND songId = :songId")
    abstract fun _updSongPos(playlistId: Long, songId: Long, toPosition: Int)

    @Transaction
    open fun moveSong(playlistId: Long, songId: Long, toPosition: Int) {
        _decSongsPositions(playlistId, songId)
        _incSongsPositions(playlistId, toPosition)
        _updSongPos(playlistId, songId, toPosition)
    }

    @Query("SELECT position FROM Songs WHERE playlistId = :playlistId ORDER BY position DESC LIMIT 1")
    abstract fun getLastPosition(playlistId: Long): Int?
}