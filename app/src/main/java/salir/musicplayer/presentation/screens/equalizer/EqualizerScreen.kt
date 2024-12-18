package salir.musicplayer.presentation.screens.equalizer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import salir.musicplayer.R
import salir.musicplayer.presentation.navigation.LocalRootNavController
import salir.musicplayer.presentation.theme.MusicPlayerTheme
import salir.musicplayer.presentation.ui.IconButton
import salir.musicplayer.presentation.viewmodels.LocalPlayerViewModel
import salir.musicplayer.presentation.viewmodels.equalizerFrequencies

@Composable
fun EqualizerScreen() {
    val playerViewModel = LocalPlayerViewModel.current
    val navController = LocalRootNavController.current

    val values = playerViewModel.equalizerSetting
    val equalizerEnabled by playerViewModel.equalizerEnabled.collectAsStateWithLifecycle()

    EqualizerScreenContent(
        values = playerViewModel.equalizerSetting.map { it.collectAsStateWithLifecycle() },
        onValueChange = { i, value -> if (values.getOrNull(i) != null) values[i].value = value },
        onNavBack = { navController.navigateUp() },
        equalizerEnabled = equalizerEnabled,
        onEqualizerEnabledChange = { playerViewModel.setEqualizerEnabledState(it) },
        onValueChangeFinished = { playerViewModel.setEqualizerBandLevel(it.toShort(), values[it].value.toInt().toShort()) },
        onResetClick = { playerViewModel.resetEqualizerSetting() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EqualizerScreenContent(
    values: List<State<Float>> = List(5) { mutableFloatStateOf(0f) },
    onValueChange: (index: Int, value: Float) -> Unit = { _, _ -> },
    onNavBack: () -> Unit = {},
    equalizerEnabled: Boolean = true,
    onEqualizerEnabledChange: (Boolean) -> Unit = {},
    onValueChangeFinished: (band: Int) -> Unit = {},
    onResetClick: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.Equalizer),
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        color = MaterialTheme.colorScheme.onSurface,
                        onClick = onNavBack,
                        painter = painterResource(R.drawable.ic_back),
                        modifier = Modifier.size(32.dp)
                    )
                },
                actions = {
                    Switch(
                        checked = equalizerEnabled,
                        onCheckedChange = onEqualizerEnabledChange,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            )
        }
    ) { innerPadding ->
        // TODO: анимированное затемнение
        if (!equalizerEnabled) {
            Box(
                modifier = Modifier
                    .zIndex(1f)
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0x80000000))
                    .clickable(onClick = {}, interactionSource = null, indication = null)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                equalizerFrequencies.forEachIndexed { i, fr ->
                    Column {
                        if (i == 0) Row(
                            modifier = Modifier.weight(1f)
                        ) {
                            Column {
                                Text(text = "+15 " + stringResource(R.string.dB))
                                Spacer(modifier = Modifier.weight(1f))
                                Text(text = "0 " + stringResource(R.string.dB))
                                Spacer(modifier = Modifier.weight(1f))
                                Text(text = "-15 " + stringResource(R.string.dB))
                            }
                            FrequencySlider(
                                value = values.getOrElse(i) { mutableFloatStateOf(0f) }.value,
                                onValueChange = { onValueChange(i, it) },
                                onValueChangeFinished = { onValueChangeFinished(i) }
                            )
                        } else FrequencySlider(
                            modifier = Modifier.weight(1f),
                            value = values.getOrElse(i) { mutableFloatStateOf(0f) }.value,
                            onValueChange = { onValueChange(i, it) },
                            onValueChangeFinished = { onValueChangeFinished(i) }
                        )
                        Text(
                            text = if (fr / 1000000f > 1)
                                "${(if (fr % 1000000 == 0) "%.0f" else "%.1f").format((fr / 1000000f))} " + stringResource(R.string.kHz)
                            else "${fr / 1000} " + stringResource(R.string.Hz),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onResetClick) {
                Text(text = stringResource(R.string.Reset))
            }
        }
    }
}

@Preview
@Composable
private fun EqualizerScreenEnabledPreview() {
    MusicPlayerTheme(true) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            EqualizerScreenContent(
                equalizerEnabled = true
            )
        }
    }
}

@Preview
@Composable
private fun EqualizerScreenDisabledPreview() {
    MusicPlayerTheme(true) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            EqualizerScreenContent(
                equalizerEnabled = false
            )
        }
    }
}