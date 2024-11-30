package salir.musicplayer.presentation.screens.settings

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import salir.musicplayer.R
import salir.musicplayer.domain.repositories.SettingsRepository
import salir.musicplayer.presentation.models.EnumSetting
import salir.musicplayer.presentation.models.SettingEnum
import salir.musicplayer.presentation.models.ThemeSettings
import salir.musicplayer.presentation.theme.MusicPlayerTheme

@Composable
fun <T> EnumSettingView(
    setting: EnumSetting<T>,
    settingName: String,
    modifier: Modifier = Modifier
) where T : Enum<T>, T: SettingEnum {
    val coroutineScope = rememberCoroutineScope()

    val selected by setting.value

    EnumSettingViewContent(
        settingName = settingName,
        selected = selected,
        items = setting.defaultValue::class.java.enumConstants,
        onSelect = {
            coroutineScope.launch(Dispatchers.IO) {
                setting.save(it)
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> EnumSettingViewContent(
    selected: T?,
    items: Array<out T>,
    onSelect: (T) -> Unit,
    settingName: String,
    modifier: Modifier = Modifier
) where T : Enum<T>, T: SettingEnum {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        Text(
            text = settingName,
            style = MaterialTheme.typography.titleMedium,
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = stringResource(selected?.text ?: R.string.loading),
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.extraSmall)
                    .menuAnchor(MenuAnchorType.PrimaryEditable),
                colors = ExposedDropdownMenuDefaults.textFieldColors().copy(
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                )
            )

            // Контекст с правильным языком
            val context = LocalContext.current

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // Контекст с неправильным языком
//                val context = LocalContext.current

                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = context.getString(item.text)) },
                        onClick = {
                            onSelect(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun EnumSettingViewPreview() {
    MusicPlayerTheme(true) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            ) {
                (1..5).forEach {
                    EnumSettingViewContent(
                        settingName = "Theme",
                        selected = ThemeSettings.SYSTEM,
                        items = ThemeSettings.entries.toTypedArray(),
                        onSelect = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}