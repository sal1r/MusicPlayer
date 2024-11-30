package salir.musicplayer.presentation.screens.home

import android.content.res.Configuration
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import salir.musicplayer.R
import salir.musicplayer.domain.models.Playlist
import salir.musicplayer.domain.models.Song
import salir.musicplayer.presentation.activities.LocalPlayerBarHeight
import salir.musicplayer.presentation.dialogs.YesNoDialogData
import salir.musicplayer.presentation.dnd.dragAndDrop
import salir.musicplayer.presentation.dnd.rememberLazyListDragAndDropState
import salir.musicplayer.presentation.navigation.LocalRootNavController
import salir.musicplayer.presentation.navigation.RootNavDestinations
import salir.musicplayer.presentation.theme.MusicPlayerTheme
import salir.musicplayer.presentation.ui.IconButton
import salir.musicplayer.presentation.ui.PlaylistCard
import salir.musicplayer.presentation.ui.SongCard
import salir.musicplayer.presentation.viewmodels.LocalAllMusicViewModel
import salir.musicplayer.presentation.viewmodels.LocalDialogsViewModel
import salir.musicplayer.presentation.viewmodels.LocalPlayerViewModel
import kotlin.math.roundToInt

@Composable
fun HomeScreen() {
    val allMusicViewModel = LocalAllMusicViewModel.current
    val playerViewModel = LocalPlayerViewModel.current
    val dialogsViewModel = LocalDialogsViewModel.current

    val navController = LocalRootNavController.current
    val context = LocalContext.current

    val playlists by allMusicViewModel.playlists.collectAsStateWithLifecycle()
    val currentPlaylist by allMusicViewModel.currentPlaylist.collectAsStateWithLifecycle()
    val currentPlaylistSongs = allMusicViewModel.currentPlaylistSongs.collectAsStateWithLifecycle()
    val currentSong by playerViewModel.currentSong.collectAsStateWithLifecycle()

    HomeScreenStateless(
        playlists = playlists,
        currentPlaylist = currentPlaylist,
        songsInCurrentPlaylist = currentPlaylistSongs,
        currentSong = currentSong,
        onAddPlaylistClick = {
            navController.navigate(RootNavDestinations.EDIT_PLAYLIST)
        },
        onEditClick = {
            navController.navigate("${RootNavDestinations.EDIT_PLAYLIST}?playlistId=${it.id}")
        },
        onPlaylistClick = {
            allMusicViewModel.setCurrentPlaylist(it)
//            allMusicViewModel.currentPlaylist.value = it
//            allMusicViewModel.loadSongsForCurrentPlaylist()
        },
        onDeleteClick = {
            dialogsViewModel.pushDialogToStack(
                YesNoDialogData(
                    message = context.getString(R.string.question_delete_playlist),
                    leftButtonLabel = context.getString(R.string.Yes),
                    rightButtonLabel = context.getString(R.string.No),
                    onLeftClick = { allMusicViewModel.deletePlaylist(it) },
                    onRightClick = {}
                )
            )
        },
        onSongInPlaylistClick = {
            playerViewModel.setPlaylist(
                playlist = allMusicViewModel.currentPlaylistSongs.value,
                song = it
            )
        },
        onDeleteSongFromPlaylistClick = { playlist, song, onCancel ->
            dialogsViewModel.pushDialogToStack(
                YesNoDialogData(
                    message = context.getString(R.string.question_delete_song),
                    leftButtonLabel = context.getString(R.string.Yes),
                    rightButtonLabel = context.getString(R.string.No),
                    onLeftClick = {
                        allMusicViewModel.deleteSongFromPlaylist(playlist, song)
                        onCancel()
                    },
                    onRightClick = {
                        onCancel()
                    }
                )
            )
        },
        onSongsOrderChanged = { lastIndex, newIndex ->
            allMusicViewModel.moveSongInCurrentPlaylist(lastIndex, newIndex)
        },
        onDragEnd = { _, endIndex ->
            allMusicViewModel.moveSongsInDataBaseForCurrentPlaylist(endIndex)
        },
        onAddToQueueClick = { playerViewModel.addSongToQueue(it) },
        onSetNextClick = { playerViewModel.setNextSong(it) },
        songsCardsShowQueueInteractionButtons = playerViewModel.queue.isNotEmpty(),
        playerBarHeight = LocalPlayerBarHeight.current,
        getFirstFourSongsImagesForPlaylist = {
            allMusicViewModel.getFirstFourSongsImagesForPlaylist(it)
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeScreenStateless(
    playlists: List<Playlist> = listOf(),
    currentPlaylist: Playlist? = null,
    songsInCurrentPlaylist: State<List<Song>> = mutableStateOf(listOf()),
    currentSong: Song? = null,
    onAddPlaylistClick: () -> Unit = {},
    onDeleteClick: (Playlist) -> Unit = {},
    onEditClick: (Playlist) -> Unit = {},
    onPlaylistClick: (Playlist) -> Unit = {},
    onSongInPlaylistClick: (Song) -> Unit = {},
    onDeleteSongFromPlaylistClick: (Playlist, Song, () -> Unit) -> Unit = { _, _, _ -> },
    onSongsOrderChanged: (lastIndex: Int, newIndex: Int) -> Unit = { _, _ -> },
    onDragEnd: (startIndex: Int, endIndex: Int) -> Unit = { _, _ -> },
    onAddToQueueClick: (song: Song) -> Unit = {},
    onSetNextClick: (song: Song) -> Unit = {},
    songsCardsShowQueueInteractionButtons: Boolean = true,
    playerBarHeight: Dp = 0.dp,
    getFirstFourSongsImagesForPlaylist: suspend (Playlist) -> List<ImageBitmap?> = { listOf() }
) {
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // TODO: выносить все состояния в параметр
        var albumsListHeight by rememberSaveable { mutableIntStateOf(120) }
        val draggableState = rememberDraggableState {
            albumsListHeight = (
                albumsListHeight - with(density) { it.toDp().value.toInt() }
            ).coerceIn(0, (maxHeight - 36.dp - playerBarHeight)
                    .value.roundToInt()
            )
        }
        val coroutineScope = rememberCoroutineScope()

        Column {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                columns = GridCells.Adaptive(120.dp)
            ) {
                items(
                    items = playlists,
                    key = { it.hashCode() }
                ) { playlist ->
                    PlaylistCard(
                        modifier = Modifier
                            .height(120.dp)
                            .padding(8.dp),
                        onClick = {  onPlaylistClick(playlist) },
                        imagePainter = playlist.imageUri?.let {
                            rememberAsyncImagePainter(
                                model = playlist.imageUri,
                                placeholder = painterResource(R.drawable.ic_launcher_background),
                                error = painterResource(R.drawable.ic_launcher_background),
                                fallback = painterResource(R.drawable.ic_launcher_background)
                            )
                        },
                        getFirstFourSongsImages = { getFirstFourSongsImagesForPlaylist(playlist) },
                        title = playlist.name,
                        isCurrentPlaylist = playlist.id == currentPlaylist?.id
                    )
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(120.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(shape = MaterialTheme.shapes.medium)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    shape = MaterialTheme.shapes.medium
                                )
                                .clickable(onClick = onAddPlaylistClick),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_add),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxSize(0.6f)
                            )
                        }
                    }
                }
            }
            if (currentPlaylist != null) {
                Column(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)))
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                        .padding(bottom = playerBarHeight),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            color = MaterialTheme.colorScheme.onSurface,
                            onClick = { onDeleteClick(currentPlaylist) },
                            painter = painterResource(R.drawable.ic_delete),
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .size(24.dp)
                                .align(Alignment.CenterStart)
                        )
                        Icon(
                            painter = painterResource(R.drawable.ic_drag_handle),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(108.dp, 36.dp)
                                .draggable(
                                    state = draggableState,
                                    orientation = Orientation.Vertical
                                )
                                .padding(horizontal = 36.dp)
                        )
                        IconButton(
                            color = MaterialTheme.colorScheme.onSurface,
                            onClick = { onEditClick(currentPlaylist) },
                            painter = painterResource(R.drawable.ic_edit),
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(24.dp)
                                .align(Alignment.CenterEnd)
                        )
                    }

                    val lazyListState = rememberLazyListState()
                    val dragAndDropState = rememberLazyListDragAndDropState()

                    BoxWithConstraints {
                        val dragWidth: Float = remember { with(density) { (maxWidth / 4f).toPx() } }

                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier
                                .height(albumsListHeight.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .dragAndDrop(
                                    lazyListState = lazyListState,
                                    dragAndDropState = dragAndDropState,
                                    onItemsOrderChanged = onSongsOrderChanged,
                                    onDragEnd = onDragEnd
                                ),
                        ) {
                            itemsIndexed(
                                items = songsInCurrentPlaylist.value,
                                key = { _, it -> it.id }
                            ) { index, song ->
                                val anchors = DraggableAnchors<Float> {
                                    0f.at(0f)
                                    1f.at(dragWidth)
                                }
                                val anchoredDraggableState = remember {
                                    AnchoredDraggableState(
                                        initialValue = 0f,
                                        anchors = anchors,
                                        positionalThreshold = { it },
                                        velocityThreshold = { 100f },
                                        snapAnimationSpec = tween(200),
                                        decayAnimationSpec = exponentialDecay(),
                                        confirmValueChange = { true }
                                    )
                                }
                                var dragEnabled by remember { mutableStateOf(true) }

                                LaunchedEffect(anchoredDraggableState.offset) {
                                    if (anchoredDraggableState.offset == dragWidth) {
                                        dragEnabled = false
                                        onDeleteSongFromPlaylistClick(currentPlaylist, song) {
                                            coroutineScope.launch {
                                                dragEnabled = true
                                                anchoredDraggableState.animateTo(0f)
                                            }
                                        }
                                    }
                                }

                                val offset =
                                    if (index == dragAndDropState.draggedItemIndex.value)
                                        dragAndDropState.draggedItemOffset.value
                                    else null

                                Box(
                                    modifier = offset?.let {
                                        Modifier
                                            .zIndex(1f)
                                            .graphicsLayer {
                                                translationY = it
                                                scaleX = 1.05f
                                                scaleY = 1.05f
                                            }
                                            .border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                shape = MaterialTheme.shapes.small
                                            )
                                    } ?: Modifier.animateItem()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(
                                                color = MaterialTheme.colorScheme.errorContainer,
                                                shape = MaterialTheme.shapes.small
                                            )
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_delete),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier
                                                .align(Alignment.CenterStart)
                                                .height(36.dp)
                                                .width(64.dp)
                                        )
                                    }

                                    SongCard(
                                        song = song,
                                        isCurrentSong = song.id == currentSong?.id,
                                        modifier = Modifier
                                            .zIndex(1f)
                                            .offset {
                                                IntOffset(
                                                    x = anchoredDraggableState.offset.toInt(),
                                                    y = 0
                                                )
                                            }
                                            .clip(MaterialTheme.shapes.small)
                                            .background(
                                                MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                    3.dp
                                                )
                                            )
                                            .anchoredDraggable(
                                                state = anchoredDraggableState,
                                                orientation = Orientation.Horizontal,
                                                enabled = dragEnabled
                                            ),
                                        onClick = { onSongInPlaylistClick(song) },
                                        onAddToQueueClick = { onAddToQueueClick(song) },
                                        onSetNextClick = { onSetNextClick(song) },
                                        showQueueInteractionButtons = songsCardsShowQueueInteractionButtons,
                                        inactiveBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showSystemUi = true, showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun HomeScreenPreview() {
    MusicPlayerTheme(true) {
        Surface {
            HomeScreenStateless(
                playlists = List(10) {
                    Playlist(
                        it.toLong(),
                        "Playlist $it",
                        null
                    )
                },
                currentPlaylist = Playlist(
                    3,
                    "Playlist 2",
                    null
                ),
                songsInCurrentPlaylist = remember {
                    mutableStateOf(List(30) {
                        Song(
                            id = it.toLong(),
                            uri = "",
                            title = "Song $it",
                            artist = "Artist $it",
                            duration = 89404,
                            image = null,
                            album = ""
                        )
                    })
                }
            )
        }
    }
}