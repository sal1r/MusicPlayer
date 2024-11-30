package salir.musicplayer.presentation.viewmodels

import androidx.compose.runtime.compositionLocalOf

val LocalAllMusicViewModel = compositionLocalOf<AllMusicViewModel> {
    error("No AllMusicViewModel provided")
}

val LocalPlayerViewModel = compositionLocalOf<PlayerViewModel> {
    error("No PlayerViewModel provided")
}

val LocalDialogsViewModel = compositionLocalOf<DialogsViewModel> {
    error("No DialogsViewModel provided")
}

val LocalSettingsViewModel = compositionLocalOf<SettingsViewModel> {
    error("No SettingsViewModel provided")
}