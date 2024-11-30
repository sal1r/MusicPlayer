package salir.musicplayer.presentation.screens.other

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import salir.musicplayer.R
import salir.musicplayer.presentation.activities.LocalPlayerBarHeight
import salir.musicplayer.presentation.dialogs.MessageDialogData
import salir.musicplayer.presentation.navigation.LocalRootNavController
import salir.musicplayer.presentation.navigation.RootNavDestinations
import salir.musicplayer.presentation.theme.MusicPlayerTheme
import salir.musicplayer.presentation.viewmodels.LocalDialogsViewModel

@Composable
fun OtherScreen() {
    val context = LocalContext.current
    val navController = LocalRootNavController.current

    val dialogsViewModel = LocalDialogsViewModel.current

    OtherScreenContent(
        listItems = listOf(
            OtherListItem(
                icon = painterResource(R.drawable.ic_equalizer),
                text = stringResource(R.string.Equalizer),
                onClick = {
                    navController.navigate(RootNavDestinations.EQUALIZER)
                }
            ),
            OtherListItem(
                icon = painterResource(R.drawable.ic_cloud_sync),
                text = stringResource(R.string.Sync),
                onClick = {
                    dialogsViewModel.pushDialogToStack(
                        MessageDialogData(message = context.getString(R.string.in_dev))
                    )
                }
            ),
            OtherListItem(
                icon = painterResource(R.drawable.ic_settings),
                text = stringResource(R.string.Settings),
                onClick = {
                    navController.navigate(RootNavDestinations.SETTINGS)
                }
            ),
            OtherListItem(
                icon = painterResource(R.drawable.ic_info_i),
                text = stringResource(R.string.About),
                onClick = {
                    dialogsViewModel.pushDialogToStack(
                        MessageDialogData(message = context.getString(R.string.in_dev))
                    )
                }
            )
        ),
        playerBarHeight = LocalPlayerBarHeight.current
    )
}

@Composable
private fun OtherScreenContent(
    listItems: List<OtherListItem> = listOf(),
    playerBarHeight: Dp = 0.dp
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        listItems.forEach {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, 4.dp)
                    .clip(MaterialTheme.shapes.small)
                    .clickable(onClick = it.onClick)
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
                    .padding(8.dp, 12.dp)
            ) {
                Icon(
                    painter = it.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(36.dp)
                )
                Text(
                    text = it.text,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_right),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(playerBarHeight))
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true, showSystemUi = true
)
@Composable
private fun OtherScreenPreview() {
    MusicPlayerTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            OtherScreenContent(
                listItems = listOf(
                    OtherListItem(
                        icon = painterResource(R.drawable.ic_equalizer),
                        text = stringResource(R.string.Equalizer),
                        onClick = {}
                    ),
                    OtherListItem(
                        icon = painterResource(R.drawable.ic_cloud_sync),
                        text = stringResource(R.string.Sync),
                        onClick = {}
                    ),
                    OtherListItem(
                        icon = painterResource(R.drawable.ic_settings),
                        text = stringResource(R.string.Settings),
                        onClick = {}
                    ),
                    OtherListItem(
                        icon = painterResource(R.drawable.ic_info_i),
                        text = stringResource(R.string.About),
                        onClick = {}
                    )
                )
            )
        }
    }
}