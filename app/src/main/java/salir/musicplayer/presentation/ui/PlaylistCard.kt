package salir.musicplayer.presentation.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import salir.musicplayer.R
import salir.musicplayer.domain.models.Playlist
import salir.musicplayer.presentation.theme.MusicPlayerTheme

@Composable
fun PlaylistCard(
    title: String,
    modifier: Modifier = Modifier,
    imagePainter: Painter? = null,
    getFirstFourSongsImages: suspend () -> List<ImageBitmap?> = { listOf() },
    onClick: () -> Unit = {},
    isCurrentPlaylist: Boolean = false,
    activeContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    inactiveContentColor: Color = MaterialTheme.colorScheme.onSurface,
    activeBackgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    inactiveBackgroundColor: Color = MaterialTheme.colorScheme.surface,
) {
    var firstFourSongsImages: List<ImageBitmap?> by remember { mutableStateOf(listOf()) }

    LaunchedEffect(Unit) {
        if (imagePainter == null) withContext(Dispatchers.IO) {
            firstFourSongsImages = getFirstFourSongsImages()
        }
    }

    PlaylistCardContent(
        modifier = modifier,
        onClick = onClick,
        imagePainters = imagePainter?.let {
            listOf(it)
        } ?: firstFourSongsImages.map { bitmap ->
            bitmap?.let {
                remember { BitmapPainter(it) }
            } ?: painterResource(R.drawable.ic_launcher_background)
        }.ifEmpty {
            listOf(painterResource(R.drawable.ic_launcher_background))
        },
        title = title,
        isCurrentPlaylist = isCurrentPlaylist,
        activeBackgroundColor = activeBackgroundColor,
        inactiveBackgroundColor = inactiveBackgroundColor,
        activeContentColor = activeContentColor,
        inactiveContentColor = inactiveContentColor
    )
}

@Composable
fun PlaylistCardContent(
    imagePainters: List<Painter>,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isCurrentPlaylist: Boolean = false,
    activeContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    inactiveContentColor: Color = MaterialTheme.colorScheme.onSurface,
    activeBackgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    inactiveBackgroundColor: Color = MaterialTheme.colorScheme.surface
) {
    if (imagePainters.isEmpty()) return

    val contentColor = if (isCurrentPlaylist) activeContentColor
        else inactiveContentColor

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(
                if (isCurrentPlaylist) activeBackgroundColor
                else inactiveBackgroundColor
            )
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        when(imagePainters.size) {
            0 -> {}

            1 -> Image(
                painter = imagePainters[0],
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.small)
            )

            2 -> Row(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.small)
            ) {
                Image(
                    painter = imagePainters[0],
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                Image(
                    painter = imagePainters[1],
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
            
            3 -> Row(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.small)
            ) {
                Image(
                    painter = imagePainters[0],
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Image(
                        painter = imagePainters[1],
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Image(
                        painter = imagePainters[2],
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(1f)
                    )
                }
            }

            else -> Row(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.small)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Image(
                        painter = imagePainters[0],
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Image(
                        painter = imagePainters[1],
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(1f)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Image(
                        painter = imagePainters[2],
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Image(
                        painter = imagePainters[3],
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(1f)
                    )
                }
            }
        }
        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = contentColor
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun PlaylistCardPreview() {
    MusicPlayerTheme(true) {
        Surface {
            Column {
                PlaylistCardContent(
                    imagePainters = listOf(
                        painterResource(R.drawable.ic_launcher_background)
                    ),
                    title = "Title",
                    modifier = Modifier.size(120.dp)
                )
                PlaylistCardContent(
                    imagePainters = listOf(
                        painterResource(R.drawable.ic_launcher_background),
                        painterResource(R.drawable.ic_launcher_background)
                    ),
                    title = "Title",
                    modifier = Modifier.size(120.dp)
                )
                PlaylistCardContent(
                    imagePainters = listOf(
                        painterResource(R.drawable.ic_launcher_background),
                        painterResource(R.drawable.ic_launcher_background),
                        painterResource(R.drawable.ic_launcher_background)
                    ),
                    title = "Title",
                    modifier = Modifier.size(120.dp)
                )
                PlaylistCardContent(
                    imagePainters = listOf(
                        painterResource(R.drawable.ic_launcher_background),
                        painterResource(R.drawable.ic_launcher_background),
                        painterResource(R.drawable.ic_launcher_background),
                        painterResource(R.drawable.ic_launcher_background)
                    ),
                    title = "Title",
                    modifier = Modifier.size(120.dp)
                )
            }
        }
    }
}