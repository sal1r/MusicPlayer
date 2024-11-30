package salir.musicplayer.presentation.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import salir.musicplayer.data.repositories.PlaylistsRepositoryImpl
import salir.musicplayer.data.repositories.SongsRepositoryImpl
import salir.musicplayer.domain.models.Playlist
import salir.musicplayer.domain.models.Song
import salir.musicplayer.domain.repositories.PlaylistsRepository
import salir.musicplayer.domain.repositories.SongsRepository

class AllMusicViewModel(
    private val songsRepository: SongsRepository,
    private val playlistsRepository: PlaylistsRepository
): ViewModel() {

    private val _songsList = MutableStateFlow<List<Song>>(listOf())
    val songsList = _songsList.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(listOf())
    val playlists = _playlists.asStateFlow()
    
    private val _currentPlaylist = MutableStateFlow<Playlist?>(null)
    val currentPlaylist = _currentPlaylist.asStateFlow()

    private val _currentPlaylistSongs = MutableStateFlow<List<Song>>(listOf())
    val currentPlaylistSongs = _currentPlaylistSongs.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _songsList.value = songsRepository.getAllSongs()
            _playlists.value = playlistsRepository.getPlaylists()
        }
    }

    fun setCurrentPlaylist(playlist: Playlist) {
        _currentPlaylist.value = playlist
        loadSongsForCurrentPlaylist()
    }

    fun loadSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            _songsList.value = songsRepository.getAllSongs()
        }
    }

    fun loadPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            _playlists.value = playlistsRepository.getPlaylists()
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.deletePlaylist(playlist)
            _currentPlaylist.value = null
            _playlists.value = playlistsRepository.getPlaylists()
        }
    }

    fun addSongToPlaylist(playlist: Playlist, song: Song, onAdded: suspend () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.addSongToPlaylist(playlist, song.id)
            viewModelScope.launch(Dispatchers.Main) {
                onAdded()
            }
            _playlists.value = playlistsRepository.getPlaylists()
            if (_currentPlaylist.value?.id == playlist.id) loadSongsForCurrentPlaylist()
        }
    }

    fun loadSongsForCurrentPlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            _currentPlaylist.value?.let {
                _currentPlaylistSongs.value = playlistsRepository.getSongsIdsFromPlaylist(it).map { songId ->
                    _songsList.value.first { song -> song.id == songId }
                }
            }
        }
    }

    suspend fun getFirstFourSongsImagesForPlaylist(playlist: Playlist): List<ImageBitmap?> {
        return playlistsRepository.getSongsIdsFromPlaylist(playlist).take(4).map {
            _songsList.value.firstOrNull { song -> song.id == it }?.image
        }
    }

    fun deleteSongFromPlaylist(playlist: Playlist, song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.deleteSongFromPlaylist(playlist, song.id)
            if (_currentPlaylist.value?.id == playlist.id) loadSongsForCurrentPlaylist()
        }
    }

    fun moveSongInCurrentPlaylist(lastIndex: Int, newIndex: Int) {
        _currentPlaylist.value?.let {
            _currentPlaylistSongs.value = _currentPlaylistSongs.value
                .toMutableList()
                .apply { add(newIndex, removeAt(lastIndex)) }
        }
    }

    fun moveSongsInDataBaseForCurrentPlaylist(newIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _currentPlaylist.value?.let {
                playlistsRepository.moveSongsInPlaylist(it, _currentPlaylistSongs.value[newIndex].id, newIndex)
                loadSongsForCurrentPlaylist()
            }
        }
    }
}