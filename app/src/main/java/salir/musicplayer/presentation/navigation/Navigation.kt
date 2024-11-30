package salir.musicplayer.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import salir.musicplayer.presentation.activities.LocalPlayerBarHeight
import salir.musicplayer.presentation.screens.allmusic.AllMusicScreen
import salir.musicplayer.presentation.screens.bigplayer.BigPlayer
import salir.musicplayer.presentation.screens.collection.CollectionScreen
import salir.musicplayer.presentation.screens.home.HomeScreen
import salir.musicplayer.presentation.screens.permissiondenied.PermissionDeniedScreen
import salir.musicplayer.presentation.screens.editplaylist.EditPlaylistScreen
import salir.musicplayer.presentation.screens.equalizer.EqualizerScreen
import salir.musicplayer.presentation.screens.other.OtherScreen
import salir.musicplayer.presentation.screens.settings.SettingsScreen
import salir.musicplayer.presentation.ui.PlayerBar
import salir.musicplayer.presentation.utils.checkAudioStoragePermission

val LocalRootNavController = compositionLocalOf<NavHostController> {
    error("No NavController provided")
}

object RootNavDestinations {
    const val HOME = "home"

    const val PERMISSION_DENIED = "permissionDenied"
    const val EDIT_PLAYLIST = "addPlaylist"

    const val SETTINGS = "settings"
    const val ABOUT = "about"
    const val EQUALIZER = "equalizer"
    const val SYNC = "sync"
}

@Composable
fun RootNavigation() {
    val context = LocalContext.current

    val rootNavController = rememberNavController()

    val audioStoragePermissionGranted = remember { checkAudioStoragePermission(context) }

    CompositionLocalProvider(
        LocalRootNavController provides rootNavController
    ) {
        NavHost(
            navController = rootNavController,
            startDestination =
                if (audioStoragePermissionGranted) RootNavDestinations.HOME
                else RootNavDestinations.PERMISSION_DENIED,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            composable(RootNavDestinations.HOME) {
                val homeNavController = rememberNavController()

                HomeNavigation(homeNavController = homeNavController)
            }

            composable(RootNavDestinations.PERMISSION_DENIED) {
                Scaffold { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        PermissionDeniedScreen()
                    }
                }
            }

            composable(
                route = "${RootNavDestinations.EDIT_PLAYLIST}?playlistId={playlistId}",
                arguments = listOf(navArgument("playlistId") {
                    nullable = true
                    type = NavType.StringType
                }),
                enterTransition = {
                    slideInHorizontally(
                        animationSpec = tween(300),
                        initialOffsetX = { -it }
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        animationSpec = tween( 300),
                        targetOffsetX = { -it }
                    )
                }
            ) {
                Scaffold { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        EditPlaylistScreen()
                    }
                }
            }

            composable(
                route = RootNavDestinations.SETTINGS,
                enterTransition = {
                    slideInHorizontally(
                        animationSpec = tween(300),
                        initialOffsetX = { it }
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        animationSpec = tween(300),
                        targetOffsetX = { it }
                    )
                }
            ) {
                SettingsScreen()
            }

            composable(RootNavDestinations.ABOUT) {
                TODO("Not implemented yet")
            }

            composable(
                route = RootNavDestinations.EQUALIZER,
                enterTransition = {
                    slideInHorizontally(
                        animationSpec = tween(300),
                        initialOffsetX = { it }
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        animationSpec = tween( 300),
                        targetOffsetX = { it }
                    )
                }
                ) {
                EqualizerScreen()
            }

            composable(RootNavDestinations.SYNC) {
                TODO("Not implemented yet")
            }
        }
    }
}

object HomeNavDestinations {
    const val HOME = "home"
    const val ALL_MUSIC = "allMusic"
    const val COLLECTION = "collection"
    const val OTHER = "other"
}

@Composable
fun HomeNavigation(
    homeNavController: NavHostController
) {
    Box (
        modifier = Modifier.fillMaxSize()
    ) {
        val density = LocalDensity.current

        var playerBarHeight by remember { mutableStateOf(0.dp) }

        Scaffold(
            bottomBar = {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                        .navigationBarsPadding()
                ) {
                    Box(
                        modifier = Modifier
                            .height(0.dp)
                            .wrapContentHeight(
                                align = Alignment.Bottom,
                                unbounded = true
                            )
                            .onSizeChanged {
                                playerBarHeight = with(density) {
                                    it.height.toDp()
                                }
                            }
                    ) {
                        PlayerBar()
                    }
                    BottomNavBar(homeNavController)
                }
            }
        ) { innerPadding ->
            CompositionLocalProvider(
                LocalPlayerBarHeight provides playerBarHeight
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    NavHost(
                        navController = homeNavController,
                        startDestination = HomeNavDestinations.HOME,
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None },
                        popEnterTransition = { EnterTransition.None },
                        popExitTransition = { ExitTransition.None }
                    ) {
                        composable(HomeNavDestinations.HOME) {
                            HomeScreen()
                        }

                        composable(HomeNavDestinations.ALL_MUSIC) {
                            AllMusicScreen()
                        }

                        composable(HomeNavDestinations.COLLECTION) {
                            CollectionScreen()
                        }

                        composable(
                            route = HomeNavDestinations.OTHER,
                            enterTransition = {
                                slideInHorizontally(
                                    animationSpec = tween(300),
                                    initialOffsetX = { it }
                                )
                            },
                            popEnterTransition = { EnterTransition.None }
                        ) {
                            OtherScreen()
                        }
                    }
                }
            }
        }
        BigPlayer()
    }
}