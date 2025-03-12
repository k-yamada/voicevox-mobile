package app.example.screens.tts

import app.example.screens.AppStateScreenModel
import app.example.voicevox.VoicevoxSdk
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TtsScreenModel : ScreenModel, KoinComponent, AppStateScreenModel<TtsScreenModel.State>(State.Result()) {
    sealed class State {
        data class Result(
            val isLoading: Boolean = false
        ) : State()
    }

    private val voicevoxSdk: VoicevoxSdk by inject()
    private val resultState: State.Result get() = mutableState.value as? State.Result ?: State.Result()

    private fun setResult(result: State.Result) {
        mutableState.value = result
    }

    fun onTapPlayButton(text: String) {
        screenModelScope.launch {
            setResult(resultState.copy(isLoading = true))
            voicevoxSdk.speak(text)
            setResult(resultState.copy(isLoading = false))
        }
    }
}
