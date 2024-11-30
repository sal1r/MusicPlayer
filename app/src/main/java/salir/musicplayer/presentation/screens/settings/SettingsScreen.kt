package salir.musicplayer.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import salir.musicplayer.R
import salir.musicplayer.presentation.navigation.LocalRootNavController
import salir.musicplayer.presentation.ui.IconButton
import salir.musicplayer.presentation.viewmodels.LocalSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val settingsViewModel = LocalSettingsViewModel.current
    val navController = LocalRootNavController.current

    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.Settings),
                            style = MaterialTheme.typography.headlineMedium,
                            maxLines = 1
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            color = MaterialTheme.colorScheme.onSurface,
                            onClick = { navController.navigateUp() },
                            painter = painterResource(R.drawable.ic_back),
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    windowInsets = WindowInsets(0.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    SettingsBlock(name = stringResource(R.string.appearance_settings)) {
                        EnumSettingView(
                            setting = settingsViewModel.theme,
                            settingName = stringResource(R.string.theme_setting_name),
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        BooleanSettingView(
                            setting = settingsViewModel.dynamicColor,
                            settingName = stringResource(R.string.dynamic_color_setting_name),
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    SettingsBlock(name = stringResource(R.string.other_settings)) {
                        EnumSettingView(
                            setting = settingsViewModel.lang,
                            settingName = stringResource(R.string.lang_setting_name),
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsBlock(
    name: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            content()
        }
    }
}