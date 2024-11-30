package salir.musicplayer.presentation.models

import salir.musicplayer.R

enum class ThemeSettings(override val text: Int): SettingEnum {
    DARK(R.string.dark_theme),
    LIGHT(R.string.light_theme),
    SYSTEM(R.string.system_theme)
}