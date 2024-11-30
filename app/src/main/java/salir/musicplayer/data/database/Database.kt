package salir.musicplayer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PlaylistEntity::class, SongEntity::class],
    version = 5,
    exportSchema = false
)
abstract class Database : RoomDatabase() {

    abstract fun playlistsDao(): PlaylistsDao

    abstract fun songsDao(): SongsDao
}