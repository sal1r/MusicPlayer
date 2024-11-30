package salir.musicplayer.data.repositories

import android.content.Context
import android.util.Log
import salir.musicplayer.R
import salir.musicplayer.domain.models.PlayerSetting
import salir.musicplayer.domain.repositories.PlayerSettingsRepository

class PlayerSettingsRepositoryImpl(context: Context): PlayerSettingsRepository {
    private val sp = context.getSharedPreferences(
        context.getString(R.string.sp_player_settings), Context.MODE_PRIVATE
    )

    override fun <T> getSetting(setting: PlayerSetting, defaultValue: T): T = when (defaultValue) {
        is Boolean -> sp.getBoolean(setting.name, defaultValue) as T
        is Int -> sp.getInt(setting.name, defaultValue) as T
        is Long -> sp.getLong(setting.name, defaultValue) as T
        is String -> sp.getString(setting.name, defaultValue) as T
        is Float -> sp.getFloat(setting.name, defaultValue) as T
        else -> error("Unsupported type: ${ defaultValue?.let { it::class } }")
    }

    override fun saveSetting(setting: PlayerSetting, value: Any) {
        with (sp.edit()) {
            when (value) {
                is Boolean -> putBoolean(setting.name, value)
                is Int -> putInt(setting.name, value)
                is Long -> putLong(setting.name, value)
                is String -> putString(setting.name, value)
                is Float -> putFloat(setting.name, value)
                else -> error("Unsupported type: ${ value?.let { it::class } }")
            }
            apply()
        }
    }
}