package salir.musicplayer.presentation.models

import salir.musicplayer.R

enum class LangSettings(override val text: Int, val locale: String?): SettingEnum {
    RU(R.string.ru_lang, "ru"),
    EN(R.string.en_lang, "en"),
    SYSTEM(R.string.system_lang, null)
}