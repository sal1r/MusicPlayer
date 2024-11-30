package salir.musicplayer.presentation.screens.editplaylist

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import org.koin.androidx.compose.koinViewModel
import salir.musicplayer.R
import salir.musicplayer.presentation.dialogs.YesNoDialogData
import salir.musicplayer.presentation.navigation.LocalRootNavController
import salir.musicplayer.presentation.theme.MusicPlayerTheme
import salir.musicplayer.presentation.viewmodels.EditPlaylistViewModel
import salir.musicplayer.presentation.viewmodels.LocalAllMusicViewModel
import salir.musicplayer.presentation.viewmodels.LocalDialogsViewModel

@Composable
fun EditPlaylistScreen() {
    val vm: EditPlaylistViewModel = koinViewModel()
    val allMusicViewModel = LocalAllMusicViewModel.current
    val navController = LocalRootNavController.current
    val context = LocalContext.current

    val dialogsViewModel = LocalDialogsViewModel.current

    val imageUri by vm.imageUri.collectAsStateWithLifecycle()
    val playlistName by vm.playlistName.collectAsStateWithLifecycle()
    val isErrorName by vm.isErrorName.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        it.data?.data?.let { uri ->
            vm.imageUri.value = uri
        }
    }

    EditPlaylistScreenStateless(
        playlistName = playlistName,
        onPlaylistNameChange = {
            vm.playlistName.value = it
            vm.isErrorName.value = false
        },
        imageUri = imageUri,
        isErrorName = isErrorName,
        onImageClick = {
            launcher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        },
        onSaveClick = {
            if (playlistName.isEmpty()) {
                vm.isErrorName.value = true
                return@EditPlaylistScreenStateless
            }

            vm.save(
                onSuccess = {
                    allMusicViewModel.loadPlaylists()
                }
            )
            navController.popBackStack()
        },
        onCancelClick = {
            if (playlistName.isEmpty() && imageUri == null) {
                navController.popBackStack()
                return@EditPlaylistScreenStateless
            }

            dialogsViewModel.pushDialogToStack(
                YesNoDialogData(
                    message = context.getString(R.string.question_exit_with_unsaved_data),
                    leftButtonLabel = context.getString(R.string.Yes),
                    rightButtonLabel = context.getString(R.string.No),
                    onLeftClick = {
                        navController.popBackStack()
                    },
                    onRightClick = {}
                )
            )
        }
    )
}

@Composable
private fun EditPlaylistScreenStateless(
    playlistName: String = "",
    onPlaylistNameChange: (String) -> Unit = {},
    imageUri: Uri? = null,
    isErrorName: Boolean = false,
    onImageClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    focusManager.clearFocus()
                }
            )
            .padding(32.dp)
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter(
                    model = imageUri.toString(),
                    placeholder = painterResource(R.drawable.ic_landscape),
                    error = painterResource(R.drawable.ic_landscape),
                    fallback = painterResource(R.drawable.ic_landscape)
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.large)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(color = Color(0x80000000))
                        }
                    }
                    .clickable(onClick = onImageClick)
            )

            Icon(
                painter = painterResource(R.drawable.ic_add_photo),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 8.dp, bottom = 8.dp)
                    .size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        var shouldShowPen by remember { mutableStateOf(true) }

        TextField(
            isError = isErrorName,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { shouldShowPen = !it.isFocused },
            value = playlistName,
            onValueChange = onPlaylistNameChange,
            singleLine = true,
            shape = MaterialTheme.shapes.small,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            ),
            trailingIcon = {
                if (isErrorName) {
                    Icon(
                        painter = painterResource(R.drawable.ic_error),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                    )

                    return@TextField
                }

                if (shouldShowPen) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                focusManager.clearFocus()
                            }
                    )
                }
            },
            supportingText = {
                if (isErrorName) {
                    Text(text = stringResource(R.string.playlist_name_should_not_be_empty))
                }
            },
            keyboardActions = KeyboardActions(
                onAny = {
                    focusManager.clearFocus()
                }
            ),
            placeholder = {
                Text(text = stringResource(R.string.name))
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = {
                    focusManager.clearFocus()
                    onCancelClick()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.cancel))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onSaveClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.save))
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun EditPlaylistScreenPreview() {
    MusicPlayerTheme {
        Surface {
            var playlistName by remember { mutableStateOf("") }

            EditPlaylistScreenStateless(
                playlistName = playlistName,
                onPlaylistNameChange = { playlistName = it },
                isErrorName = true
            )
        }
    }
}