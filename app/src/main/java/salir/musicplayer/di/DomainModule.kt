package salir.musicplayer.di

import org.koin.dsl.module
import salir.musicplayer.data.repositories.EqualizerSettingsRepositoryImpl
import salir.musicplayer.data.repositories.PlayerSettingsRepositoryImpl
import salir.musicplayer.data.repositories.PlaylistsRepositoryImpl
import salir.musicplayer.data.repositories.SettingsRepositoryImpl
import salir.musicplayer.data.repositories.SongsRepositoryImpl
import salir.musicplayer.domain.repositories.EqualizerSettingsRepository
import salir.musicplayer.domain.repositories.PlayerSettingsRepository
import salir.musicplayer.domain.repositories.PlaylistsRepository
import salir.musicplayer.domain.repositories.SettingsRepository
import salir.musicplayer.domain.repositories.SongsRepository
import salir.musicplayer.domain.usecases.SavePlayerSettingsUseCase

val domainModule = module {

    single<PlayerSettingsRepository> {
        PlayerSettingsRepositoryImpl(context = get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(context = get())
    }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(db = get())
    }

    single<SongsRepository> {
        SongsRepositoryImpl(context = get())
    }

    single<EqualizerSettingsRepository> {
        EqualizerSettingsRepositoryImpl(context = get())
    }

    factory<SavePlayerSettingsUseCase> {
        SavePlayerSettingsUseCase(repository = get())
    }
}