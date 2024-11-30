package salir.musicplayer.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity(tableName = "Playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val imageUri: String? = null
)

@Dao
interface PlaylistsDao {

    @Query("SELECT * FROM Playlists")
    fun getPlaylists(): List<PlaylistEntity>

    @Insert
    fun insertPlaylist(playlist: PlaylistEntity): Long

    @Update
    fun updatePlaylist(playlist: PlaylistEntity)

    @Delete
    fun deletePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM Playlists WHERE id = :id")
    fun getPlaylistById(id: Long): PlaylistEntity
}