package salir.musicplayer.data.repositories

import android.content.Context
import salir.musicplayer.R
import salir.musicplayer.domain.repositories.EqualizerSettingsRepository

class EqualizerSettingsRepositoryImpl(context: Context): EqualizerSettingsRepository {

    private val sp = context.getSharedPreferences(
        context.getString(R.string.sp_equalizer_settings), Context.MODE_PRIVATE
    )

    override suspend fun getEnabled(): Boolean = sp.getBoolean(ENABLED_SETTING, false)

    override suspend fun setEnabled(enabled: Boolean) {
        sp.edit().putBoolean(ENABLED_SETTING, enabled).apply()
    }

    override suspend fun getBandLevel(band: Short): Short =
        sp.getInt(BAND_LEVEL_SETTING(band), 0).toShort()

    override suspend fun setBandLevel(band: Short, level: Short) {
        sp.edit().putInt(BAND_LEVEL_SETTING(band), level.toInt()).apply()
    }

    companion object {
        private const val ENABLED_SETTING = "enabled"
        private val BAND_LEVEL_SETTING = { band: Short -> "band_$band" }
    }
}