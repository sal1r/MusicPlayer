package salir.musicplayer.presentation.screens.permissiondenied

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import salir.musicplayer.R
import salir.musicplayer.presentation.navigation.LocalRootNavController
import salir.musicplayer.presentation.navigation.RootNavDestinations
import salir.musicplayer.presentation.utils.navigateOnce
import salir.musicplayer.presentation.viewmodels.LocalAllMusicViewModel

@Composable
fun PermissionDeniedScreen() {
    val navController = LocalRootNavController.current
    val allMusicViewModel = LocalAllMusicViewModel.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        results.forEach {
            if (!it.value) return@rememberLauncherForActivityResult
        }

        allMusicViewModel.loadSongs()
        navController.navigateOnce(RootNavDestinations.HOME)
    }

    PermissionDeniedScreenStateless(
        onClick = {
            permissionLauncher.launch(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_AUDIO, READ_MEDIA_VISUAL_USER_SELECTED)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_AUDIO)
                } else {
                    arrayOf(READ_EXTERNAL_STORAGE)
                }
            )
        }
    )
}

@Composable
fun PermissionDeniedScreenStateless(
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Text(stringResource(R.string.permission_denied_text))
        Button(onClick = onClick) {
            Text(stringResource(R.string.permission_denied_button_text))
        }
    }
}