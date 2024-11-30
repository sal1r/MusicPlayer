package salir.musicplayer

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import salir.musicplayer.di.appModule
import salir.musicplayer.di.dataModule
import salir.musicplayer.di.domainModule

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(appModule, dataModule, domainModule)
        }
    }
}