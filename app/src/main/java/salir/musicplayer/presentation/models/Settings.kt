package salir.musicplayer.presentation.models

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import salir.musicplayer.domain.repositories.SettingsRepository

@Stable
interface Setting<T> {
    val name: String
    val defaultValue: T
    val value: State<T?>
    suspend fun save(value: T)
    suspend fun load()
}

@Stable
class EnumSetting<T : Enum<T>>(
    override val name : String,
    override val defaultValue: T,
    private val repository: SettingsRepository
) : Setting<T> {
    private val _value: MutableState<T?> = mutableStateOf(null)
    override val value: State<T?> = _value

    override suspend fun save(value: T) {
        repository.saveString(name, value.name)
        _value.value = value
    }

    override suspend fun load() {
        _value.value = repository.getString(name, defaultValue.name).let { data ->
            defaultValue::class.java.enumConstants.firstOrNull { it.name == data }
        }
    }
}

@Stable
class BooleanSetting(
    override val name : String,
    override val defaultValue: Boolean,
    private val repository: SettingsRepository
) : Setting<Boolean> {
    private val _value: MutableState<Boolean?> = mutableStateOf(null)
    override val value: State<Boolean?> = _value

    override suspend fun save(value: Boolean) {
        repository.saveBoolean(name, value)
        _value.value = value
    }

    override suspend fun load() {
        _value.value = repository.getBoolean(name, defaultValue)
    }
}

@Stable
class IntSetting(
    override val name : String,
    override val defaultValue: Int,
    private val repository: SettingsRepository
) : Setting<Int> {
    private val _value: MutableState<Int?> = mutableStateOf(null)
    override val value: State<Int?> = _value

    override suspend fun save(value: Int) {
        repository.saveInt(name, value)
        _value.value = value
    }

    override suspend fun load() {
        _value.value = repository.getInt(name, defaultValue)
    }
}

@Stable
class StringSetting(
    override val name : String,
    override val defaultValue: String,
    private val repository: SettingsRepository
) : Setting<String> {
    private val _value: MutableState<String?> = mutableStateOf(null)
    override val value: State<String?> = _value

    override suspend fun save(value: String) {
        repository.saveString(name, value)
        _value.value = value
    }

    override suspend fun load() {
        _value.value = repository.getString(name, defaultValue)
    }
}

interface SettingEnum {
    val text: Int
}