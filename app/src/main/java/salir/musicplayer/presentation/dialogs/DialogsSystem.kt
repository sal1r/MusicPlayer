package salir.musicplayer.presentation.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import salir.musicplayer.presentation.viewmodels.LocalDialogsViewModel

@Composable
fun DialogsSystem() {
    val stack = LocalDialogsViewModel.current.stack

    stack.lastOrNull()?.let {
        Box(
            modifier = Modifier
                .zIndex(100f)
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(interactionSource = null, indication = null, onClick = {}),
            contentAlignment = Alignment.Center
        ) {
            it._dialogComposable()
        }
    }
}