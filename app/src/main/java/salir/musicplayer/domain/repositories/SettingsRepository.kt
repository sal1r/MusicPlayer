package salir.musicplayer.domain.repositories

interface SettingsRepository {

    fun saveBoolean(key: String, value: Boolean)

    fun saveInt(key: String, value: Int)

    fun saveString(key: String, value: String)


    fun getBoolean(key: String, defaultValue: Boolean): Boolean

    fun getInt(key: String, defaultValue: Int): Int

    fun getString(key: String, defaultValue: String): String
}