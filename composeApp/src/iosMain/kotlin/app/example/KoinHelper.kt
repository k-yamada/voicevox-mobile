package app.example

import app.example.voicevox.VoicevoxNative
import app.example.voicevox.VoicevoxSdk
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoin(
    voicevoxNative: VoicevoxNative,
) {
    startKoin {
        modules(module {
            single<VoicevoxSdk> { VoicevoxSdk(voicevoxNative) }
        })
    }
}
