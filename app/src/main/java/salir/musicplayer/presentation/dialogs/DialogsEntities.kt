package salir.musicplayer.presentation.dialogs

import androidx.compose.runtime.Composable

interface DialogData {
    @Composable
    fun _dialogComposable()
}

data class YesNoDialogData(
    val message: String,
    val leftButtonLabel: String,
    val rightButtonLabel: String,
    val onLeftClick: () -> Unit,
    val onRightClick: () -> Unit
): DialogData {
    @Composable
    override fun _dialogComposable() {
        YesNoDialog(this)
    }
}

data class MessageDialogData(
    val message: String
): DialogData {
    @Composable
    override fun _dialogComposable() {
        MessageDialog(this)
    }
}