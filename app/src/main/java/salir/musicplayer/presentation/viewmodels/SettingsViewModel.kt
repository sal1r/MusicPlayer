package salir.musicplayer.presentation.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import salir.musicplayer.R
import salir.musicplayer.data.repositories.SettingsRepositoryImpl
import salir.musicplayer.domain.repositories.SettingsRepository
import salir.musicplayer.presentation.models.BooleanSetting
import salir.musicplayer.presentation.models.EnumSetting
import salir.musicplayer.presentation.models.LangSettings
import salir.musicplayer.presentation.models.ThemeSettings

class SettingsViewModel(
    context: Context,
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val _settingsLoaded = MutableStateFlow(false)
    val settingsLoaded = _settingsLoaded.asStateFlow()

    val theme: EnumSetting<ThemeSettings> = EnumSetting(
        name = context.getString(R.string.theme_setting),
        defaultValue = ThemeSettings.SYSTEM,
        repository = settingsRepository
    )

    val lang: EnumSetting<LangSettings> = EnumSetting(
        name = context.getString(R.string.lang_setting),
        defaultValue = LangSettings.SYSTEM,
        repository = settingsRepository
    )

    val dynamicColor: BooleanSetting = BooleanSetting(
        name = context.getString(R.string.dynamic_color_setting),
        defaultValue = false,
        repository = settingsRepository
    )

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            theme.load()
            dynamicColor.load()
            lang.load()
            _settingsLoaded.value = true
        }
    }
}