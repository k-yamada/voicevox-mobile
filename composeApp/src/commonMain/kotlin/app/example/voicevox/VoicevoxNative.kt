package app.example.voicevox

import okio.Path

interface VoicevoxNative {
    /** VOICEVOXの音声モデル等を保存するディレクトリ */
    val libraryDir: String

    /**
     * 一時的なファイルを保存するディレクトリ
     * 使い終わったファイルは自分で削除すること
     */
    val cacheDir: String

    suspend fun speak(text: String, wavFilePath: Path, openJtalkDicDirPath: Path, vvmFilePath: Path, styleId: Int)
}
