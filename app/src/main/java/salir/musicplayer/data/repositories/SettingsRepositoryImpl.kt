package salir.musicplayer.data.repositories

import android.content.Context
import salir.musicplayer.R
import salir.musicplayer.domain.repositories.SettingsRepository

class SettingsRepositoryImpl(context: Context): SettingsRepository {
    private val sp = context.getSharedPreferences(
        context.getString(R.string.sp_settings), Context.MODE_PRIVATE
    )

//    override fun <T : Enum<T>> saveEnum(key: String, value: T) {
//        sp.edit().putString(key, value.name).apply()
//    }

    override fun saveBoolean(key: String, value: Boolean) {
        sp.edit().putBoolean(key, value).apply()
    }

    override fun saveInt(key: String, value: Int) {
        sp.edit().putInt(key, value).apply()
    }

    override fun saveString(key: String, value: String) {
        sp.edit().putString(key, value).apply()
    }

//    override fun <T : Enum<T>> getEnum(key: String, defaultValue: T): T =
//        sp.getString(key, defaultValue.name)!!.let { name ->
//            defaultValue::class.java.enumConstants!!.first { it.name == name }
//        }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        sp.getBoolean(key, defaultValue)

    override fun getInt(key: String, defaultValue: Int): Int =
        sp.getInt(key, defaultValue)

    override fun getString(key: String, defaultValue: String): String =
        sp.getString(key, defaultValue)!!
}