package salir.musicplayer.presentation.utils

import androidx.navigation.NavHostController

fun NavHostController.navigateWithOneInstance(destination: String) {
    navigate(destination) {
        launchSingleTop = true
        popUpTo(currentBackStackEntry?.destination?.route ?: "") {
            saveState = true
            inclusive = true
        }
        restoreState = true
    }
}

fun NavHostController.navigateOnce(destination: String) {
    navigate(destination) {
        launchSingleTop = true
        popUpTo(currentBackStackEntry?.destination?.route ?: "") {
            inclusive = true
        }
        restoreState = true
    }
}