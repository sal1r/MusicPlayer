package salir.musicplayer.presentation.screens.fastplayer

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.palette.graphics.Palette
import org.koin.androidx.compose.koinViewModel
import salir.musicplayer.R
import salir.musicplayer.domain.models.Song
import salir.musicplayer.presentation.models.ThemeSettings
import salir.musicplayer.presentation.ui.PlayButton
import salir.musicplayer.presentation.viewmodels.FastPlayerViewModel
import kotlin.system.exitProcess

@Composable
fun FastPlayerScreen(vm: FastPlayerViewModel = koinViewModel()) {
    val context = LocalContext.current

    BackHandler {
        (context as Activity).finishAndRemoveTask()
        exitProcess(0)
    }

    FastPlayerScreenContent(
        song = vm.currentSong,
        songImage = vm.songImage,
        androidSdkInt = android.os.Build.VERSION.SDK_INT,
        isPlaying = vm.isPlaying,
        progress = vm.progress,
        onProgressChange = vm::setProgressWithoutSeek,
        onProgressChangeFinished = vm::endProgressChange,
        onPlayClick = { vm.changePlayingState() },
        onBackgroundClick = {
            (context as Activity).finishAndRemoveTask()
            exitProcess(0)
        }
    )
}

@Composable
private fun FastPlayerScreenContent(
    song: Song? = null,
    songImage: ImageBitmap? = null,
    isPlaying: Boolean = false,
    androidSdkInt: Int = android.os.Build.VERSION_CODES.TIRAMISU,
    progress: Float = 0f,
    onPlayClick: () -> Unit = {},
    onBackgroundClick: () -> Unit = {},
    onProgressChange: (Float) -> Unit = {},
    onProgressChangeFinished: () -> Unit = {}
) {
    if (song == null) return

    val isSysytemInDarkTheme = isSystemInDarkTheme()

    val image: Painter? = remember(songImage) { songImage?.let { BitmapPainter(it) } }

    val contentColor = image?.let {
        Color.White
    } ?: MaterialTheme.colorScheme.onPrimaryContainer

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                onClick = onBackgroundClick,
                interactionSource = null,
                indication = null
            ),
        color = Color.Black.copy(alpha = 0.5f),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (
                image != null &&
                androidSdkInt >= android.os.Build.VERSION_CODES.TIRAMISU
            ) Image(
                painter = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(128.dp)
                    .clip(MaterialTheme.shapes.large)
                    .blur(8.dp)
                    .drawWithContent {
                        drawContent()
                        drawRect(Color.Black.copy(alpha = 0.3f))
                    }
            ) else Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(128.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
            )
            Column(
                modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(128.dp)
                .clip(MaterialTheme.shapes.large)
                .padding(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = image ?: painterResource(R.drawable.ic_image),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .clip(MaterialTheme.shapes.large),
                        colorFilter = if (image == null) ColorFilter.tint(contentColor) else null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(
                        modifier = Modifier
                    ) {
                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = contentColor
                        )
                        Text(
                            text = song.artist,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = contentColor,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PlayButton(
                        isPlaying = isPlaying,
                        color = contentColor,
                        onPlayClick = onPlayClick,
                        modifier = Modifier.size(32.dp),
                        ripple = false
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Slider(
                        value = progress,
                        onValueChange = onProgressChange,
                        onValueChangeFinished = onProgressChangeFinished,
                        modifier = Modifier
                            .weight(1f)
                            .height(24.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = contentColor,
                            activeTrackColor = contentColor,
                            activeTickColor = contentColor,
                            inactiveTrackColor = contentColor.copy(alpha = 0.5f),
                            inactiveTickColor = contentColor.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun FastPlayerScreenPreview() {
    FastPlayerScreenContent(
        progress = 0.3f,
        song = Song(
            id = 0,
            uri = "",
            title = "Song titile",
            artist = "Artist name",
            duration = 0,
            image = null,
            album = ""
        )
    )
}