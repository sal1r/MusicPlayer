package salir.musicplayer.domain.repositories

interface EqualizerSettingsRepository {

    suspend fun getEnabled(): Boolean

    suspend fun setEnabled(enabled: Boolean)

    suspend fun getBandLevel(band: Short): Short

    suspend fun setBandLevel(band: Short, level: Short)
}