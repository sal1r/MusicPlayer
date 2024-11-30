package salir.musicplayer.presentation.screens.bigplayer

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.FileProvider
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.palette.graphics.Palette
import salir.musicplayer.R
import salir.musicplayer.domain.models.Playlist
import salir.musicplayer.domain.models.Song
import salir.musicplayer.presentation.theme.MusicPlayerTheme
import salir.musicplayer.presentation.ui.IconButton
import salir.musicplayer.presentation.ui.PlayButton
import salir.musicplayer.presentation.viewmodels.LocalAllMusicViewModel
import salir.musicplayer.presentation.viewmodels.LocalPlayerViewModel
import salir.musicplayer.domain.utils.millsToMinutesAndSeconds
import salir.musicplayer.presentation.activities.LocalActivityContext
import salir.musicplayer.presentation.activities.LocalSnacknarHostState
import salir.musicplayer.presentation.models.ThemeSettings
import salir.musicplayer.presentation.viewmodels.LocalSettingsViewModel
import java.io.File

@Composable
fun BigPlayer() {
    val context = LocalContext.current
    val activityContext = LocalActivityContext.current

    val playerViewModel = LocalPlayerViewModel.current
    val allMusicViewModel = LocalAllMusicViewModel.current

    val settingsViewModel = LocalSettingsViewModel.current

    val snackbarHostState = LocalSnacknarHostState.current

    val currentSong = playerViewModel.currentSong.collectAsStateWithLifecycle().value ?: return

    val isPlaying by playerViewModel.isPlaying.collectAsStateWithLifecycle()

    val colorSwatch = remember(currentSong.id) {
        currentSong.image?.let { img ->
            Palette.from(img.asAndroidBitmap()).generate().let {
                if (settingsViewModel.theme.value.value == ThemeSettings.LIGHT)
                    it.lightMutedSwatch ?: it.mutedSwatch ?: it.darkMutedSwatch
                else it.darkMutedSwatch ?: it.mutedSwatch ?: it.lightMutedSwatch
            }
        }
    }

    val backgroundColor = colorSwatch?.let {
        Color(it.rgb)
    } ?: MaterialTheme.colorScheme.primaryContainer

    val contentColor = colorSwatch?.rgb?.let {
        if (it.red * 0.299f + it.green * 0.587f + it.blue * 0.114f > 186) Color.Black else Color.White
    } ?: MaterialTheme.colorScheme.onPrimaryContainer

    var showAddToPlaylistDialog by remember { mutableStateOf(false) }
    val addToPlaylistDialogAlpha by animateFloatAsState(
        targetValue = if (showAddToPlaylistDialog) 0.5f else 0f,
        animationSpec = tween(400)
    )

    val showBigPlayer by playerViewModel.showBigPlayer.collectAsStateWithLifecycle()
    val progress by playerViewModel.progress.collectAsStateWithLifecycle()
    val isShuffled by playerViewModel.isShuffled.collectAsStateWithLifecycle()
    val repeatMode by playerViewModel.repeatMode.collectAsStateWithLifecycle()

    AnimatedVisibility(
        visible = showBigPlayer,
        enter = slideInVertically(
            animationSpec = tween(300),
            initialOffsetY = { it }
        ),
        exit = slideOutVertically(
            animationSpec = tween(300),
            targetOffsetY = { it }
        ),
        modifier = Modifier.zIndex(1f)
    ) {
        BigPlayerStateless(
            song = currentSong,
            onBackgroundClick = { playerViewModel.showBigPlayer.value = false },
            progress = progress,
            onProgressChange = playerViewModel::setProgressWithoutSeek,
            onProgressChangeFinished = playerViewModel::endProgressChange,
            isPlaying = isPlaying,
            onPlayClick = { playerViewModel.setPlayingState(!isPlaying) },
            onPreviousClick = { playerViewModel.previousSong() },
            onNextClick = { playerViewModel.nextSong() },
            isShuffled = isShuffled,
            onShuffleCkick = { playerViewModel.setShuffledState(!isShuffled) },
            repeatState = repeatMode,
            onRepeatClick = { playerViewModel.setNextRepeatState() },
            onAddToPlaylistClick = { showAddToPlaylistDialog = true },
            onShareClick = {
                val file = File(currentSong.uri)
                val uri = FileProvider.getUriForFile(activityContext, "salir.musicplayer.fileprovider", file)

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "audio/*"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                activityContext.startActivity(Intent.createChooser(intent, context.getString(R.string.Share)))
            },
            backgroundColor = backgroundColor,
            contentColor = contentColor
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = addToPlaylistDialogAlpha))
                .then(
                    if (showAddToPlaylistDialog) Modifier.clickable(
                        onClick = { showAddToPlaylistDialog = false },
                        indication = null,
                        interactionSource = null
                    ) else Modifier
                )
        ) {
            AnimatedVisibility(
                visible = showAddToPlaylistDialog,
                enter = slideInVertically(
                    animationSpec = tween(300),
                    initialOffsetY = { it }
                ),
                exit = slideOutVertically(
                    animationSpec = tween(300),
                    targetOffsetY = { it }
                ),
                modifier = Modifier
                    .zIndex(1f)
                    .align(Alignment.BottomCenter)
            ) {
                AddToPlaylistDialog(
                    playlists = allMusicViewModel.playlists.collectAsStateWithLifecycle().value,
                    onPlaylistClick = {
                        allMusicViewModel.addSongToPlaylist(it, currentSong) {
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.song_added_to_playlist),
                                duration = SnackbarDuration.Short,
                                withDismissAction = true
                            )
                        }
                        showAddToPlaylistDialog = false
                    },
                    modifier = Modifier
                        .fillMaxHeight(0.5f),
                    getFirstFourSongsImagesForPlaylist = { allMusicViewModel.getFirstFourSongsImagesForPlaylist(it) }
                )
            }
        }
    }
}

@Composable
private fun BigPlayerStateless(
    song: Song,
    onBackgroundClick: () -> Unit = {},
    progress: Float = 0f,
    onProgressChange: (Float) -> Unit = {},
    onProgressChangeFinished: () -> Unit = {},
    isPlaying: Boolean = false,
    onPlayClick: () -> Unit = {},
    onPreviousClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onAddToPlaylistClick: () -> Unit = {},
    isShuffled: Boolean = false,
    onShuffleCkick: () -> Unit = {},
    repeatState: Int = Player.REPEAT_MODE_OFF,
    onRepeatClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .clickable(onClick = onBackgroundClick, indication = null, interactionSource = null)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(32.dp, 8.dp)
    ) {
        Column {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_down),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Image(
                painter = song.image?.let {
                    BitmapPainter(it)
                } ?: painterResource(R.drawable.ic_launcher_background),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = contentColor,
                        maxLines = 1,
                        modifier = Modifier
                            .basicMarquee(
                                iterations = Int.MAX_VALUE,
                                velocity = 30.dp
                            )
                    )

                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.titleMedium,
                        color = contentColor,
                        maxLines = 1,
                        modifier = Modifier
                            .basicMarquee(
                                iterations = Int.MAX_VALUE,
                                velocity = 30.dp
                            )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                var dropdownExpanded by remember { mutableStateOf(false) }

                Box {
                    IconButton(
                        onClick = { dropdownExpanded = !dropdownExpanded },
                        painter = painterResource(R.drawable.ic_more_48),
                        color = contentColor,
                        modifier = Modifier.size(32.dp)
                    )

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
                                        painter = painterResource(R.drawable.ic_add_circle),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .size(24.dp)
                                    )
                                    Text(text = context.getString(R.string.add_to_playlist))
                                }
                            },
                            onClick = {
                                dropdownExpanded = false
                                onAddToPlaylistClick()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_share),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .size(24.dp)
                                    )
                                    Text(text = context.getString(R.string.Share))
                                }
                            },
                            onClick = {
                                dropdownExpanded = false
                                onShareClick()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Slider(
                value = progress,
                onValueChange = onProgressChange,
                onValueChangeFinished = onProgressChangeFinished,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors().copy(
                    thumbColor = contentColor,
                    inactiveTrackColor = contentColor.copy(
                        alpha = 0.5f
                    ),
                    activeTrackColor = contentColor
                )
            )

            Row {
                Text(
                    text = millsToMinutesAndSeconds((song.duration * progress).toInt()),
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = millsToMinutesAndSeconds(song.duration),
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onRepeatClick,
                    painter = painterResource(
                        if (repeatState == Player.REPEAT_MODE_ONE) R.drawable.ic_repeat_one
                        else R.drawable.ic_repeat
                    ),
                    color = contentColor.let {
                        if (repeatState == Player.REPEAT_MODE_OFF) it.copy(alpha = 0.5f)
                        else it
                    },
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = onPreviousClick,
                    painter = painterResource(R.drawable.ic_arrow_previous),
                    color = contentColor,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                PlayButton(
                    isPlaying = isPlaying,
                    color = contentColor,
                    onPlayClick = onPlayClick,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onNextClick,
                    painter = painterResource(R.drawable.ic_arrow_next),
                    color = contentColor,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = onShuffleCkick,
                    painter = painterResource(R.drawable.ic_shuffle),
                    color = contentColor.let{
                        if (isShuffled) it
                        else it.copy(alpha = 0.5f)
                    },
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    device = "spec:width=900px,height=2100px,dpi=440"
)
@Composable
private fun BigPlayerPreview() {
    MusicPlayerTheme(true) {
        Surface {
            BigPlayerStateless(
                song = Song(
                    id = 0,
                    uri = "",
                    title = "qedsqedfrqweqewqewedfqwqefsdfqweqwesvvweqweqwdfsfeqeqwweqweqweqeqedsfddfdqqds",
                    artist = "Artist",
                    duration = 405900,
                    image = null,
                    album = ""
                ),
                progress = 0.4f
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    device = "spec:width=900px,height=2100px,dpi=440"
)
@Composable
private fun BigPlayerWithAddToPlaylistDialogPreview() {
    MusicPlayerTheme(true) {
        Surface {
            BigPlayerStateless(
                song = Song(
                    id = 0,
                    uri = "",
                    title = "qedsqedfrqweqewqewedfqwqefsdfqweqwesvvweqweqwdfsfeqeqwweqweqweqeqedsfddfdqqds",
                    artist = "Artist",
                    duration = 405900,
                    image = null,
                    album = ""
                ),
                progress = 0.4f
            )

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