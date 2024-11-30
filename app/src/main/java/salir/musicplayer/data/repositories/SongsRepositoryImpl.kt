package salir.musicplayer.data.repositories

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.AudioMixer
import androidx.media3.transformer.Transformer
import salir.musicplayer.domain.models.Song
import salir.musicplayer.domain.repositories.SongsRepository

class SongsRepositoryImpl(
    private val context: Context
): SongsRepository {

    override fun getAllSongs(): List<Song> {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            MediaStore.Audio.Media.TITLE + " ASC"
        )

        val songs = mutableListOf<Song>()

        if (cursor != null && cursor.moveToFirst()) {
            do {
                getSongFromCursor(cursor)?.let {  songs.add(it) }
            } while (cursor.moveToNext())
        }

        cursor?.close()
        return songs
    }

    override fun getSongById(songId: Long): Song? {
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)

        val cursor = context.contentResolver.query(uri, projection, null, null, null)

        val song = 
            if (cursor != null && cursor.moveToFirst()) getSongFromCursor(cursor)
            else null

        cursor?.close()
        return song
    }

    @OptIn(UnstableApi::class)
    private fun getSongFromCursor(cursor: Cursor): Song? {
        val id = cursor.getLong(0)
        val path = cursor.getString(1)
        val title = cursor.getString(2)
        val artist = cursor.getString(3)
        val duration = cursor.getInt(4)
        val isMusic = cursor.getInt(5) != 0
        val album = cursor.getString(6)

        if (!isMusic) return null

        val image = getSongImage(path)

        return Song(id, path, title, artist, duration, image, album)
    }

    private fun getSongImage(path: String): ImageBitmap? {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(path)
        val bytes = mmr.embeddedPicture
        mmr.release()

        return bytes?.let {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
        }
    }

    companion object {
        private val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.ALBUM
        )
    }
}