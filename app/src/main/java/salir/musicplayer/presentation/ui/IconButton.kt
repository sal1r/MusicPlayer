package salir.musicplayer.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import salir.musicplayer.R

@Composable
fun IconButton(
    color: Color,
    onClick: () -> Unit,
    painter: Painter,
    modifier: Modifier = Modifier,
    ripple: Boolean = true
) {
    Icon(
        modifier = modifier
            .clip(CircleShape)
            .then(
                if (ripple) Modifier.clickable(onClick = onClick)
                else Modifier.clickable(onClick = onClick, indication = null, interactionSource = null)
            ),
        painter = painter,
        tint = color,
        contentDescription = null
    )
}

@Composable
fun IconButton(
    color: Color,
    onClick: () -> Unit,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    ripple: Boolean = true
) {
    Icon(
        modifier = modifier
            .clip(CircleShape)
            .then(
                if (ripple) Modifier.clickable(onClick = onClick)
                else Modifier.clickable(onClick = onClick, indication = null, interactionSource = null)
            ),
        imageVector = imageVector,
        tint = color,
        contentDescription = null
    )
}