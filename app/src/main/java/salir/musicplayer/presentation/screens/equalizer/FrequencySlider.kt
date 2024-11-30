package salir.musicplayer.presentation.screens.equalizer

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import salir.musicplayer.R
import salir.musicplayer.presentation.theme.MusicPlayerTheme

@Composable
fun FrequencySlider(
    modifier: Modifier = Modifier,
    value: Float = 0f,
    onValueChange: (Float) -> Unit = {},
    valueRange: ClosedFloatingPointRange<Float> = -1500f..1500f,
    onValueChangeFinished: () -> Unit = {}
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        onValueChangeFinished = onValueChangeFinished,
        colors = SliderDefaults.colors(
            activeTrackColor = SliderDefaults.colors().inactiveTrackColor
        ),
        modifier = modifier
            .graphicsLayer {
                transformOrigin = TransformOrigin(0f, 0f)
                rotationZ = -90f
            }
            .layout {
                    measurable, constraints ->
                val placeable = measurable.measure(
                    Constraints(
                        minWidth = constraints.minHeight,
                        maxWidth = constraints.maxHeight,
                        minHeight = constraints.minWidth,
                        maxHeight = constraints.maxWidth
                    )
                )
                layout(placeable.height, placeable.width) {
                    placeable.place(-placeable.width, 0)
                }
            }
    )
}

@Preview
@Composable
private fun FrequencySliderPreview() {
    MusicPlayerTheme(true) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            FrequencySlider()
        }
    }
}