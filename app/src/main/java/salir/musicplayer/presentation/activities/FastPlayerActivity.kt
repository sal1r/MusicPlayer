package salir.musicplayer.presentation.activities

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel
import salir.musicplayer.presentation.screens.fastplayer.FastPlayerScreen
import salir.musicplayer.presentation.theme.MusicPlayerTheme
import salir.musicplayer.presentation.viewmodels.FastPlayerViewModel

class FastPlayerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = intent.data

        enableEdgeToEdge()

        setContent {
            MusicPlayerTheme(
                dynamicColor = true
            ) {
                val vm: FastPlayerViewModel = koinViewModel()

                LaunchedEffect(Unit) {
                    vm.setAudioUri(data!!)
                }

                FastPlayerScreen(vm)
            }
        }
    }
}