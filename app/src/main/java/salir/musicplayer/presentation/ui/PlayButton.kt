package salir.musicplayer.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import salir.musicplayer.R

@Composable
fun PlayButton(
    isPlaying: Boolean,
    color: Color,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier,
    ripple: Boolean = true
) {
    IconButton(
        color = color,
        onClick = onPlayClick,
        painter = painterResource(
            if (isPlaying) R.drawable.ic_stop
            else R.drawable.ic_play
        ),
        modifier = modifier,
        ripple = ripple
    )
}