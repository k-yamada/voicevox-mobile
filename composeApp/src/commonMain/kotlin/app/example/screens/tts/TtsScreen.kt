package app.example.screens.tts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.core.component.KoinComponent

class TtsScreen : Screen, KoinComponent {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = navigator.rememberNavigatorScreenModel { TtsScreenModel() }
        val viewModelState by screenModel.state.collectAsState()
        val state = viewModelState as TtsScreenModel.State.Result

        var text by remember { mutableStateOf("ボイスボックス") }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                value = text,
                onValueChange = {
                    text = it
                },
                trailingIcon = {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier.clickable { text = "" }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                enabled = !state.isLoading,
                onClick = {
                    screenModel.onTapPlayButton(text)
                }) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "play",
                )
            }
        }
    }
}
