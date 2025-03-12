package app.example.voicevox

sealed class VoicevoxError {
    data class RuntimeError(val message: String) : VoicevoxError()
    object OpenJtalkNotFound : VoicevoxError()
    data class VvmNotFound(val fileName: String) : VoicevoxError()
}
