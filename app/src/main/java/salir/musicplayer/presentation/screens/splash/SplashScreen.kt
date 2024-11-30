package salir.musicplayer.presentation.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import salir.musicplayer.presentation.theme.SplashScreenColors
import java.util.Vector
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onAnimationEnd: () -> Unit = {}
) {
    val scale = remember { Animatable(1f) }
    val scale2 = remember { Animatable(1f) }
    val scale3 = remember { Animatable(1f) }
    val alpha = remember { Animatable(1f) }

    BoxWithConstraints(
        modifier = modifier
            .alpha(alpha.value)
            .fillMaxSize()
            .background(SplashScreenColors.background)
    ) {
        LaunchedEffect(Unit) {
            scale.animateTo(targetValue = 1.5f, animationSpec = tween(700))
            scale.animateTo(targetValue = 1f, animationSpec = tween(700))
            val j1 = launch {
                scale2.animateTo(
                    targetValue = sqrt(
                        maxWidth.value * maxWidth.value + maxHeight.value * maxHeight.value
                    ) / 256.dp.value,
                    animationSpec = tween(700)
                )
            }
            val j2 = launch {
                scale3.animateTo(targetValue = 0f, animationSpec = tween(700))
            }
            val j3 = launch {
                alpha.animateTo(targetValue = 0f, animationSpec = tween(700))
            }
            j1.join()
            j2.join()
            j3.join()
            onAnimationEnd()
        }

        Box(
            Modifier
                .align(Alignment.Center)
                .size(256.dp)
                .graphicsLayer {
                    scaleX = scale.value * scale2.value
                    scaleY = scale.value * scale2.value
                }
                .clip(CircleShape)
                .background(SplashScreenColors.onBackground2)
        )

        Box(
            Modifier
                .align(Alignment.Center)
                .size(48.dp)
                .graphicsLayer {
                    scaleX = 1.5f / scale.value * scale3.value
                    scaleY = 1.5f / scale.value * scale3.value
                }
                .clip(CircleShape)
                .background(SplashScreenColors.onBackground)
        )
    }
}

@Preview(showSystemUi = true, showBackground = false,
    device = "spec:width=1080px,height=2340px,dpi=440"
)
@Composable
private fun SplashScreenPreview() {
    SplashScreen()
}