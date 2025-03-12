package app.example

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import app.example.voicevox.VoicevoxNativeImpl
import app.example.voicevox.VoicevoxSdk
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module


class MainApplication : Application(),
    DefaultLifecycleObserver {

    override fun onCreate() {
        super<Application>.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(makeAppModule())
        }
    }

    private fun makeAppModule(): Module {
        return module {
            single<VoicevoxSdk> { VoicevoxSdk(VoicevoxNativeImpl()) }
        }
    }
}
