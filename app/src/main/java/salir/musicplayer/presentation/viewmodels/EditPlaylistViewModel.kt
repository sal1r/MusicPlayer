package salir.musicplayer.presentation.viewmodels

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import salir.musicplayer.domain.models.Playlist
import salir.musicplayer.domain.repositories.PlaylistsRepository

class EditPlaylistViewModel(
    private val app: Application,
    savedStateHandle: SavedStateHandle,
    private val playlistsRepository: PlaylistsRepository
): ViewModel() {

    private var playlistId: Long? = savedStateHandle.get<String?>("playlistId")?.toLongOrNull()

    val playlistName = MutableStateFlow("")
    val imageUri = MutableStateFlow<Uri?>(null)
    val isErrorName = MutableStateFlow(false)

    init {
        loadData()
    }

    fun loadData() {
        playlistId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val playlist = playlistsRepository.getPlaylistById(it)
                playlistName.value = playlist.name
                imageUri.value = playlist.imageUri?.toUri()
            }
        }
    }

    fun save(
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistId?.let {
                playlistsRepository.updatePlaylist(Playlist(
                    id = it,
                    name = playlistName.value,
                    imageUri = imageUri.value?.toString()
                ))
            } ?: run {
                playlistId = playlistsRepository.insertPlaylist(
                    Playlist(
                        id = -1,
                        name = playlistName.value,
                        imageUri = imageUri.value?.toString()
                    )
                )
            }

            imageUri.value?.let {
                app.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            onSuccess()
        }
    }
}