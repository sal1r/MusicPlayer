package salir.musicplayer.presentation.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import salir.musicplayer.R
import salir.musicplayer.domain.models.Song
import salir.musicplayer.presentation.theme.MusicPlayerTheme
import salir.musicplayer.presentation.viewmodels.LocalPlayerViewModel

@Composable
fun PlayerBar() {
    val playerViewModel = LocalPlayerViewModel.current

    val currentSong by playerViewModel.currentSong.collectAsStateWithLifecycle()
    var lastNotNullSong = remember { Song(0, "", "", "", 0, null, "") }

    currentSong?.let { song -> lastNotNullSong = song }

    val isPlaying by playerViewModel.isPlaying.collectAsStateWithLifecycle()
    val progress by playerViewModel.progress.collectAsStateWithLifecycle()

    AnimatedVisibility(
        visible = currentSong != null,
        enter = slideInVertically(
            animationSpec = tween(300),
            initialOffsetY = { it }
        ),
        exit = slideOutVertically(
            animationSpec = tween(300),
            targetOffsetY = { it }
        )
    ) {
        PlayerBarStateless(
            song = lastNotNullSong,
            progress = { progress },
            onPlayClick = { playerViewModel.setPlayingState(!isPlaying) },
            onBarClick = { playerViewModel.showBigPlayer.value = true },
            isPlaying = isPlaying
        )

    }
}

@Composable
private fun PlayerBarStateless(
    song: Song,
    isPlaying: Boolean,
    progress: () -> Float = { 0f },
    onPlayClick: () -> Unit = {},
    onBarClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onBarClick)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = song.image?.let {
                        BitmapPainter(it)
                    } ?: painterResource(R.drawable.ic_launcher_background),
                    contentDescription = song.title,
                    modifier = Modifier.size(56.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = song.title,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        modifier = Modifier
                            .basicMarquee(
                                iterations = Int.MAX_VALUE,
                                velocity = 30.dp
                            )
                    )

                    Text(
                        text = song.artist,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        modifier = Modifier
                            .basicMarquee(
                                iterations = Int.MAX_VALUE,
                                velocity = 30.dp
                            )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                PlayButton(
                    isPlaying = isPlaying,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    onPlayClick = onPlayClick,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))
            }

            LinearProgressIndicator(
                progress = progress,
                gapSize = 0.dp,
                strokeCap = StrokeCap.Square,
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                drawStopIndicator = {}
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    name = "Player Bar"
)
@Composable
private fun PlayerBarPreview() {
    MusicPlayerTheme(true) {
        Surface {
            PlayerBarStateless(
                song = Song(
                    id = 0,
                    uri = "qedsqedfrqweqewqewedfqwqefsdfqweqwesvvweqweqwdfsfeqeqwweqweqweqeqedsfddfdqqds",
                    title = "qedsqedfrqweqewqewedfqwqefsdfqweqwesvvweqweqwdfsfeqeqwweqweqweqeqedsfddfdqqds",
                    artist = "Artist",
                    duration = 405900,
                    image = null,
                    album = ""
                ),
                progress = { 0.4f },
                isPlaying = true
            )
        }
    }
}