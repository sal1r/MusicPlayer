package salir.musicplayer.presentation.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import salir.musicplayer.R
import salir.musicplayer.presentation.utils.navigateWithOneInstance

data class BottomNavItem(
    val name: String,
    val icon_disabled: Painter,
    val icon_selected: Painter,
    val route: String
)

@Composable
fun BottomNavBar(navController: NavHostController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    BottomNavBarContent(
        items = listOf(
            BottomNavItem(
                name = stringResource(R.string.Home),
                icon_disabled = painterResource(R.drawable.ic_home_unfilled),
                icon_selected = painterResource(R.drawable.ic_home_filled),
                route = RootNavDestinations.HOME
            ),
            BottomNavItem(
                name = stringResource(R.string.Collection),
                icon_disabled = painterResource(R.drawable.ic_collection_unfilled),
                icon_selected = painterResource(R.drawable.ic_collection_filled),
                route = HomeNavDestinations.COLLECTION
            ),
            BottomNavItem(
                name = stringResource(R.string.All_music),
                icon_disabled = painterResource(R.drawable.ic_all_music_unfilled),
                icon_selected = painterResource(R.drawable.ic_all_music_filled),
                route = HomeNavDestinations.ALL_MUSIC
            ),
            BottomNavItem(
                name = stringResource(R.string.Other),
                icon_disabled = painterResource(R.drawable.ic_other_unfilled),
                icon_selected = painterResource(R.drawable.ic_other_filled),
                route = HomeNavDestinations.OTHER
            )
        ),
        selected = {
            it.route == currentBackStackEntry?.destination?.route
        },
        onClick = {
            navController.navigateWithOneInstance(it.route)
        }
    )
}

@Composable
private fun BottomNavBarContent(
    items: List<BottomNavItem>,
    selected: (BottomNavItem) -> Boolean,
    onClick: (BottomNavItem) -> Unit = {}
) {
    NavigationBar(
        modifier = Modifier.height(70.dp),
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
        windowInsets = WindowInsets(0)
    ) {
        items.forEach { item ->
            val selected = selected(item)

            NavigationBarItem(
                selected = selected,
                onClick = { onClick(item) },
                icon = {
                    AnimatedContent(
                        targetState = selected,
                        contentAlignment = Alignment.Center,
                        transitionSpec = {
                            fadeIn(tween(300, easing = EaseOut)) +
                            scaleIn(tween(300, easing = EaseOut), initialScale = 0.92f) togetherWith
                            fadeOut(tween(300, easing = EaseOut))
                        }
                    ) {
                        if (it) Icon(
                            painter = item.icon_selected,
                            contentDescription = item.name,
                            modifier = Modifier.size(24.dp)
                        ) else Icon(
                            painter = item.icon_disabled,
                            contentDescription = item.name,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(text = item.name)
                }
            )
        }
    }
}

