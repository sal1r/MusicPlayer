package salir.musicplayer.presentation.screens.settings

import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.LocalUseFallbackRippleImplementation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import salir.musicplayer.R
import salir.musicplayer.presentation.models.BooleanSetting
import salir.musicplayer.presentation.theme.MusicPlayerTheme

@Composable
fun BooleanSettingView(
    setting: BooleanSetting,
    settingName: String,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    BooleanSettingViewContent(
        value = setting.value.value ?: false,
        onValueChange = {
            coroutineScope.launch(Dispatchers.IO) {
                setting.save(it)
            }
        },
        settingName = settingName,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooleanSettingViewContent(
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    settingName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(
            LocalRippleConfiguration provides null
        ) {
            Switch(
                checked = value,
                onCheckedChange = onValueChange
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = settingName,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
fun BooleanSettingViewPreview() {
    var checked by remember { mutableStateOf(false) }

    MusicPlayerTheme(true) {
        Surface {
            BooleanSettingViewContent(
                value = checked,
                onValueChange = { checked = it },
                settingName = "Test"
            )
        }
    }
}