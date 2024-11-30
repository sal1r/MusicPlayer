package salir.musicplayer.domain.repositories

import salir.musicplayer.domain.models.PlayerSetting

interface PlayerSettingsRepository {
    fun <T> getSetting(setting: PlayerSetting, defaultValue: T): T

    fun saveSetting(setting: PlayerSetting, value: Any)
}