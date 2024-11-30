package salir.musicplayer.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import salir.musicplayer.R
import salir.musicplayer.data.database.Database
import salir.musicplayer.data.repositories.PlayerSettingsRepositoryImpl
import salir.musicplayer.data.repositories.PlaylistsRepositoryImpl
import salir.musicplayer.data.repositories.SettingsRepositoryImpl
import salir.musicplayer.data.repositories.SongsRepositoryImpl
import salir.musicplayer.domain.repositories.PlayerSettingsRepository
import salir.musicplayer.domain.repositories.PlaylistsRepository
import salir.musicplayer.domain.repositories.SettingsRepository
import salir.musicplayer.domain.repositories.SongsRepository
import salir.musicplayer.domain.usecases.SavePlayerSettingsUseCase

val dataModule = module {

    single<Database> {
        Room.databaseBuilder(get(), Database::class.java, androidContext().getString(R.string.music_player_db))
            .fallbackToDestructiveMigration()
            .build()
    }
}