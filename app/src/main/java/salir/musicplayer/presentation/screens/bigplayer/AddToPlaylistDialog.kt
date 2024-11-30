package salir.musicplayer.presentation.screens.bigplayer

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import salir.musicplayer.R
import salir.musicplayer.domain.models.Playlist
import salir.musicplayer.presentation.theme.MusicPlayerTheme
import salir.musicplayer.presentation.ui.PlaylistCard
import salir.musicplayer.presentation.ui.PlaylistCardContent

@Composable
fun AddToPlaylistDialog(
    playlists: List<Playlist>,
    modifier: Modifier = Modifier,
    onPlaylistClick: (Playlist) -> Unit = { },
    getFirstFourSongsImagesForPlaylist: suspend (Playlist) -> List<ImageBitmap?> = { listOf() }
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)))
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(120.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    items = playlists,
                    key = { it.hashCode() }
                ) { playlist ->
                    PlaylistCard(
                        modifier = Modifier
                            .height(120.dp)
                            .padding(8.dp),
                        onClick = { onPlaylistClick(playlist) },
                        getFirstFourSongsImages = { getFirstFourSongsImagesForPlaylist(playlist) },
                        title = playlist.name,
                        isCurrentPlaylist = false,
                        inactiveBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    device = "spec:width=900px,height=2100px,dpi=440"
)
@Composable
private fun AddPlaylistDialogPreview() {
    MusicPlayerTheme(true) {
        Surface {
            AddToPlaylistDialog(
                playlists = List(10) {
                    Playlist(
                        it.toLong(),
                        "Playlist $it",
                        null
                    )
                }
            )
        }
    }
}