package app.example.voicevox

import okio.Path
import okio.Path.Companion.toPath

interface VoicevoxNative {
    /** VOICEVOXの音声モデル等を保存するディレクトリ */
    val libraryDir: String

    /**
     * 一時的なファイルを保存するディレクトリ
     * 使い終わったファイルは自分で削除すること
     */
    val cacheDir: String

    suspend fun speak(text: String, openJtalkDicDirPath: Path, vvmFilePath: Path)

    val tempWavPath: Path get() = "${cacheDir}/temp.wav".toPath()

    companion object {
        const val STYLE_ID = 3
    }
}
