package salir.musicplayer.presentation.dialogs

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import salir.musicplayer.presentation.theme.MusicPlayerTheme
import salir.musicplayer.presentation.viewmodels.DialogsViewModel
import salir.musicplayer.presentation.viewmodels.LocalDialogsViewModel


// TODO: разделить на Stateful и Stateless
@Composable
fun YesNoDialog(data: YesNoDialogData) {
    val dialogsViewModel = LocalDialogsViewModel.current

    Surface(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Text(
                text = data.message,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
            Row {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            data.onLeftClick()
                            dialogsViewModel.popDialogFromStack(data)
                        }
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = data.leftButtonLabel)
                }
                Box(
                    modifier = Modifier
                        .width(2.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            data.onRightClick()
                            dialogsViewModel.popDialogFromStack(data)
                        }
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = data.rightButtonLabel)
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun YesNoDialogPreview() {
    MusicPlayerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            YesNoDialog(
                data = YesNoDialogData(
                    message = "Are you sure you want to delete this playlist?",
                    leftButtonLabel = "Cancel",
                    rightButtonLabel = "Delete",
                    onLeftClick = {},
                    onRightClick = {}
                )
            )
        }
    }
}