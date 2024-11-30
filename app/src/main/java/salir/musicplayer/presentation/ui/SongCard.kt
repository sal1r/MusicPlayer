package salir.musicplayer.presentation.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import salir.musicplayer.R
import salir.musicplayer.domain.models.Song
import salir.musicplayer.presentation.theme.MusicPlayerTheme
import salir.musicplayer.domain.utils.millsToMinutesAndSeconds

@Composable
fun SongCard(
    song: Song,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onAddToQueueClick: () -> Unit = {},
    onSetNextClick: () -> Unit = {},
    isCurrentSong: Boolean = false,
    activeContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    inactiveContentColor: Color = MaterialTheme.colorScheme.onSurface,
    activeBackgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    inactiveBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    showQueueInteractionButtons: Boolean = true
) {
    val contentColor = if (isCurrentSong) activeContentColor
        else inactiveContentColor

    var dropdownExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isCurrentSong) activeBackgroundColor
                else inactiveBackgroundColor
            )
            .clickable(onClick = onClick)
            .padding(8.dp, 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = song.image?.let {
                BitmapPainter(it)
            } ?: painterResource(R.drawable.ic_launcher_background),
            contentDescription = song.title,
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.extraSmall)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = contentColor
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = contentColor,
                    modifier = Modifier
                        .weight(weight = 1f, fill = false)
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(contentColor)
                )

                Text(
                    text = millsToMinutesAndSeconds(song.duration),
                    style = MaterialTheme.typography.titleSmall,
                    color = contentColor
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))
        
        Box {
            IconButton(
                color = contentColor,
                onClick = { dropdownExpanded = true },
                painter = painterResource(R.drawable.ic_more_48),
                modifier = Modifier.size(28.dp)
            )

            if (showQueueInteractionButtons) {
                val context = LocalContext.current

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_play_next),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(24.dp)
                                )
                                Text(text = context.getString(R.string.play_next))
                            }
                        },
                        onClick = {
                            dropdownExpanded = false
                            onSetNextClick()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_add_to_queue),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(24.dp)
                                )
                                Text(text = context.getString(R.string.add_to_queue))
                            }
                        },
                        onClick = {
                            dropdownExpanded = false
                            onAddToQueueClick()
                        }
                    )
                }
            }
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun SongCardPreview() {
    MusicPlayerTheme(debug = true) {
        Surface {
            Column {
                SongCard(
                    song = Song(
                        id = 0,
                        uri = "",
                        title = "qedsqedfrqweqewqewedfqwqefsdfqweqwesvvweqweqwdfsfeqeqwweqweqweqeqedsfddfdqqds",
                        artist = "Artist",
                        duration = 405900,
                        image = null,
                        album = ""
                    ),
                    isCurrentSong = true
                )
                SongCard(
                    song = Song(
                        id = 0,
                        uri = "",
                        title = "qedsqedfrqweqewqewedfqwqefsdfqweqwesvvweqweqwdfsfeqeqwweqweqweqeqedsfddfdqqds",
                        artist = "Artist",
                        duration = 405900,
                        image = null,
                        album = ""
                    ),
                    isCurrentSong = false
                )
            }
        }
    }
}