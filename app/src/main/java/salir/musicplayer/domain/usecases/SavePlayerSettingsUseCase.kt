package salir.musicplayer.domain.usecases

import salir.musicplayer.domain.models.PlayerSetting
import salir.musicplayer.domain.repositories.PlayerSettingsRepository

class SavePlayerSettingsUseCase(
    private val repository: PlayerSettingsRepository
) {
    operator fun invoke(setting: PlayerSetting, value: Any) {
        repository.saveSetting(setting, value)
    }

    operator fun invoke(vararg settings: Pair<PlayerSetting, Any>) {
        settings.forEach { repository.saveSetting(it.first, it.second) }
    }
}