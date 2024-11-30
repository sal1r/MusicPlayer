package salir.musicplayer.presentation.screens.allmusic

import android.content.res.Configuration
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import salir.musicplayer.R
import salir.musicplayer.domain.models.Song
import salir.musicplayer.presentation.activities.LocalPlayerBarHeight
import salir.musicplayer.presentation.theme.MusicPlayerTheme
import salir.musicplayer.presentation.ui.IconButton
import salir.musicplayer.presentation.ui.SongCard
import salir.musicplayer.presentation.viewmodels.LocalAllMusicViewModel
import salir.musicplayer.presentation.viewmodels.LocalPlayerViewModel

@Composable
fun AllMusicScreen() {
    val playerViewModel = LocalPlayerViewModel.current
    val songs by LocalAllMusicViewModel.current.songsList.collectAsStateWithLifecycle()
    val currentSong by playerViewModel.currentSong.collectAsStateWithLifecycle()

    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    AllMusicScreenStateless(
        songs = songs,
        currentSong = currentSong,
        onSongClick = { song, playlist ->
            playerViewModel.setPlaylist(playlist, song)
        },
        bottomSpace = LocalPlayerBarHeight.current,
        selectedTabIndex = selectedTabIndex,
        onTabClick = { selectedTabIndex = it },
        onAddToQueueClick = { playerViewModel.addSongToQueue(it) },
        onSetNextClick = { playerViewModel.setNextSong(it) },
        songsCardsShowQueueInteractionButtons = playerViewModel.queue.isNotEmpty()
    )
}

private object AllMusicNavDestinations {
    const val ALL_MUSIC = "all_music"
    const val GROUP = "group"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllMusicScreenStateless(
    songs: List<Song> = listOf(),
    currentSong: Song? = null,
    onSongClick: (song: Song, playlist: List<Song>) -> Unit = { _, _ -> },
    bottomSpace: Dp = 0.dp,
    selectedTabIndex: Int = 0,
    onTabClick: (index: Int) -> Unit = {},
    onAddToQueueClick: (song: Song) -> Unit = {},
    onSetNextClick: (song: Song) -> Unit = {},
    songsCardsShowQueueInteractionButtons: Boolean = true
) {
    val allMusicNavController = rememberNavController()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        NavHost(
            navController = allMusicNavController,
            startDestination = AllMusicNavDestinations.ALL_MUSIC,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            composable(AllMusicNavDestinations.ALL_MUSIC) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val tabsLazyListsStates = List(3) { rememberLazyListState() }

                    ScrollableTabRow(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                        selectedTabIndex = selectedTabIndex,
                        edgePadding = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                            .clip(MaterialTheme.shapes.medium),
                        indicator = { tabPositions ->
                            Box(
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[selectedTabIndex])
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .fillMaxSize()
                            )
                        },
                        divider = {}
                    ) {
                        (0..2).forEach { index ->
                            val selected = index == selectedTabIndex

                            Box(
                                modifier = Modifier
                                    .weight(1f, false)
                                    .zIndex(1f)
                                    .padding(horizontal = 4.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable(
                                        onClick = { onTabClick(index) },
                                        interactionSource = null,
                                        indication = null
                                    )
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                    text = when (index) {
                                        0 -> stringResource(R.string.All_music)
                                        1 -> stringResource(R.string.Albums)
                                        else -> stringResource(R.string.Artists)
                                    },
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    LazyColumn(
                        state = tabsLazyListsStates[selectedTabIndex],
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        when (selectedTabIndex) {
                            0 -> songs.forEach { song ->
                                item(key = song.id) {
                                    SongCard(
                                        song = song,
                                        onClick = { onSongClick(song, songs) },
                                        isCurrentSong = song.id == currentSong?.id,
                                        onAddToQueueClick = { onAddToQueueClick(song) },
                                        onSetNextClick = { onSetNextClick(song) },
                                        showQueueInteractionButtons = songsCardsShowQueueInteractionButtons
                                    )
                                }
                            }
                            1 -> songs.map { it.album }.distinct().forEach {
                                item(key = it) {
                                    Text(
                                        text = it,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                allMusicNavController.navigate(
                                                    "${AllMusicNavDestinations.GROUP}?groupName=${it}&isAlbum=true"
                                                )
                                            }
                                            .padding(8.dp)
                                    )
                                }
                            }
                            2 -> songs.map { it.artist }.distinct().forEach {
                                item(key = it) {
                                    Text(
                                        text = it,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                allMusicNavController.navigate(
                                                    "${AllMusicNavDestinations.GROUP}?groupName=${it}&isAlbum=false"
                                                )
                                            }
                                            .padding(8.dp)
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(bottomSpace))
                        }
                    }
                }
            }

            composable(
                route = "${AllMusicNavDestinations.GROUP}?groupName={groupName}&isAlbum={isAlbum}",
                arguments = listOf(
                    navArgument("groupName") {
                        nullable = false
                        type = NavType.StringType
                    },
                    navArgument("isAlbum") {
                        nullable = false
                        type = NavType.BoolType
                    }
                )
            ) { bse ->
                val groupName = bse.arguments?.getString("groupName") ?: ""
                val isAlbum = bse.arguments?.getBoolean("isAlbum") ?: false

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TopAppBar(
                        title = {
                            Text(
                                text = groupName,
                                style = MaterialTheme.typography.headlineMedium,
                                maxLines = 1,
                                modifier = Modifier
                                    .basicMarquee(
                                        iterations = Int.MAX_VALUE,
                                        velocity = 30.dp
                                    )
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                color = MaterialTheme.colorScheme.onSurface,
                                onClick = { allMusicNavController.navigateUp() },
                                painter = painterResource(R.drawable.ic_back),
                                modifier = Modifier.size(32.dp)
                            )
                        },
                        windowInsets = WindowInsets(0.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        songs.filter {
                            if (isAlbum) it.album == groupName
                            else it.artist == groupName
                        }.let {
                            it.forEach { song ->
                                item(key = song.id) {
                                    SongCard(
                                        song = song,
                                        onClick = { onSongClick(song, it) },
                                        isCurrentSong = song.id == currentSong?.id,
                                        onAddToQueueClick = { onAddToQueueClick(song) },
                                        onSetNextClick = { onSetNextClick(song) },
                                        showQueueInteractionButtons = songsCardsShowQueueInteractionButtons
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(bottomSpace))
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "All Music Screen", showSystemUi = true, showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun AllMusicScreenPreview() {
    MusicPlayerTheme(true) {
        Surface {
            AllMusicScreenStateless(
                List(20) {
                    Song(
                        id = it.toLong(),
                        uri = it.toString(),
                        title = "qedsqedfrqweqewqewedfqwqefsdfqweqwesvvweqweqwdfsfeqeqwweqweqweqeqedsfddfdqqds",
                        artist = "Artist",
                        duration = 405900,
                        image = null,
                        album = ""
                    )
                }
            )
        }
    }
}