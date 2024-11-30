package salir.musicplayer.presentation.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.koin.androidx.compose.koinViewModel
import salir.musicplayer.presentation.dialogs.DialogsSystem
import salir.musicplayer.presentation.models.ThemeSettings
import salir.musicplayer.presentation.navigation.RootNavigation
import salir.musicplayer.presentation.screens.splash.SplashScreen
import salir.musicplayer.presentation.theme.MusicPlayerTheme
import salir.musicplayer.presentation.viewmodels.AllMusicViewModel
import salir.musicplayer.presentation.viewmodels.DialogsViewModel
import salir.musicplayer.presentation.viewmodels.LocalAllMusicViewModel
import salir.musicplayer.presentation.viewmodels.LocalDialogsViewModel
import salir.musicplayer.presentation.viewmodels.LocalPlayerViewModel
import salir.musicplayer.presentation.viewmodels.LocalSettingsViewModel
import salir.musicplayer.presentation.viewmodels.PlayerViewModel
import salir.musicplayer.presentation.viewmodels.SettingsViewModel
import java.util.Locale

val LocalActivityContext = compositionLocalOf<Context> { error("No ActivityContext provided") }
val LocalSnacknarHostState = compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }
val LocalPlayerBarHeight = compositionLocalOf<Dp> { error("No PlayerBarHeight provided") }

class MainActivity : ComponentActivity() {

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val settingsViewModel = koinViewModel<SettingsViewModel>()

            val settingsLoaded by settingsViewModel.settingsLoaded.collectAsState()

            var endOfAnim by rememberSaveable { mutableStateOf(false) }

            if (!settingsLoaded || !endOfAnim) {
                SplashScreen(
                    modifier = Modifier.zIndex(1f),
                    onAnimationEnd = { endOfAnim = true }
                )
            }

            val lang = settingsViewModel.lang.value.value ?: return@setContent

            CompositionLocalProvider(
                LocalActivityContext provides this,
                LocalContext provides LocalContext.current.let { context ->
                    context.createConfigurationContext(context.resources.configuration.apply {
                        setLocale(
                            lang.locale?.let { Locale(it) }
                                ?: Locale.getDefault()
                        )
                    })
                },
                LocalActivityResultRegistryOwner provides this,
                LocalAllMusicViewModel provides koinViewModel<AllMusicViewModel>(),
                LocalPlayerViewModel provides koinViewModel<PlayerViewModel>(),
                LocalDialogsViewModel provides koinViewModel<DialogsViewModel>(),
                LocalSettingsViewModel provides settingsViewModel,
                LocalSnacknarHostState provides remember { SnackbarHostState() }
            ) {
                MusicPlayerTheme(
                    darkTheme = (settingsViewModel.theme.value.value ?: return@CompositionLocalProvider).let {
                        when (it) {
                            ThemeSettings.SYSTEM -> isSystemInDarkTheme()
                            ThemeSettings.LIGHT -> false
                            ThemeSettings.DARK -> true
                        }
                    },
                    dynamicColor = settingsViewModel.dynamicColor.value.value ?: return@CompositionLocalProvider
                ) {
                    Box {
                        RootNavigation()
                        DialogsSystem()
                        SnackbarHost(
                            hostState = LocalSnacknarHostState.current,
                            modifier = Modifier
                                .zIndex(10f)
                                .offset(y = (-8).dp)
                                .navigationBarsPadding()
                                .align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }

//    override fun onActivityReenter(resultCode: Int, data: Intent?) {
//        super.onActivityReenter(resultCode, data)
//
//        Log.d("test", intent.extras.toString())
//    }
//
//    override fun setIntent(newIntent: Intent?) {
//        super.setIntent(newIntent)
//
//        Log.d("test", intent.extras.toString())
//    }
//
//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//
//        Log.d("test", intent.extras.toString())
//
//        Log.d("test", intent.extras?.getBoolean(OPEN_BIG_PLAYER)?.let { ViewModelProvider(this)[PlayerViewModel::class.java].showBigPlayer = true; it }.toString())
//    }
}

class LocaleContextWrapper(context: Context, locale: String? = null) : ContextWrapper(
    context.let {
        context.createConfigurationContext(context.resources.configuration.apply {
            setLocale(
                locale?.let { Locale(it) }
                    ?: Locale.getDefault()
            )
        })
    }
)