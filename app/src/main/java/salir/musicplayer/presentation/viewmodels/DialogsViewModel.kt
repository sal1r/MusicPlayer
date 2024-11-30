package salir.musicplayer.presentation.viewmodels

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import salir.musicplayer.presentation.dialogs.DialogData

class DialogsViewModel: ViewModel() {
    var stack = mutableStateListOf<DialogData>()
        private set

    fun pushDialogToStack(dialog: DialogData) {
        stack.add(dialog)
    }

    fun popDialogFromStack(dialog: DialogData) {
        stack.removeIf { it === dialog }
    }
}