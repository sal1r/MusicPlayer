package salir.musicplayer.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import salir.musicplayer.presentation.viewmodels.AllMusicViewModel
import salir.musicplayer.presentation.viewmodels.DialogsViewModel
import salir.musicplayer.presentation.viewmodels.EditPlaylistViewModel
import salir.musicplayer.presentation.viewmodels.FastPlayerViewModel
import salir.musicplayer.presentation.viewmodels.PlayerViewModel
import salir.musicplayer.presentation.viewmodels.SettingsViewModel

val appModule = module {

    viewModel<SettingsViewModel> {
        SettingsViewModel(context = get(), settingsRepository = get())
    }

    viewModel<AllMusicViewModel> {
        AllMusicViewModel(songsRepository = get(), playlistsRepository = get())
    }

    viewModel<PlayerViewModel> {
        PlayerViewModel(context = get(), playerSettingsRepository = get(), songsRepository = get(), equalizerSettingsRepository = get())
    }

    viewModel<DialogsViewModel> {
        DialogsViewModel()
    }

    viewModel<EditPlaylistViewModel> {
        EditPlaylistViewModel(app = get(), savedStateHandle = get(), playlistsRepository = get())
    }

    viewModel<FastPlayerViewModel> {
        FastPlayerViewModel(app = get())
    }
}