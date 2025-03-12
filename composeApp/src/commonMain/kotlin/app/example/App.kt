package app.example

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import app.example.screens.tts.TtsScreen
import cafe.adriel.voyager.navigator.Navigator
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Navigator(TtsScreen())
    }
}
