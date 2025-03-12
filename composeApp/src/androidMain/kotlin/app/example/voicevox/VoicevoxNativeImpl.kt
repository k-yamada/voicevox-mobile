package app.example.voicevox

import android.media.MediaPlayer
import app.example.MainActivity
import co.touchlab.kermit.Logger
import jp.hiroshiba.voicevoxcore.OpenJtalk
import jp.hiroshiba.voicevoxcore.Synthesizer
import jp.hiroshiba.voicevoxcore.VoiceModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Path
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VoicevoxNativeImpl : VoicevoxNative {
    override val libraryDir: String
        get() = applicationContext.filesDir.toString()
    override val cacheDir: String
        get() = applicationContext.cacheDir.toString()
    private val applicationContext get() = MainActivity.INSTANCE.applicationContext
    private var mediaPlayer: MediaPlayer? = null
    private var isVoiceModelLoaded = false

    override suspend fun speak(text: String, wavFilePath: Path, openJtalkDicDirPath: Path, vvmFilePath: Path, styleId: Int) {
        withContext(Dispatchers.IO) {
            val synthesizer = makeSynthesizer(openJtalkDicDirPath)
            loadVoiceModelIfNeeded(synthesizer, vvmFilePath)

            // wavファイル作成
            val wavFile = File(wavFilePath.toString())

            // 音声合成
            val audioQuery = try {
                synthesizer.createAudioQuery(text, styleId)
            } catch (e: Exception) {
                Logger.e("Failed to createAudioQuery", e)
                return@withContext
            }
            val wavData = synthesizer.synthesis(audioQuery, styleId).execute()
            val fos = FileOutputStream(wavFile)
            fos.write(wavData)
            fos.close()

            // 再生
            playWavFile(wavFilePath)
        }
    }

    private fun makeSynthesizer(openJtalkDictDirPath: Path): Synthesizer {
        val openJtalk = OpenJtalk(openJtalkDictDirPath.toString())
        val synthesizer = Synthesizer.builder(openJtalk)
            .accelerationMode(
                Synthesizer.AccelerationMode.AUTO
            )
            .build()
        return synthesizer
    }

    private fun loadVoiceModelIfNeeded(synthesizer: Synthesizer, vvmPath: Path) {
        if (isVoiceModelLoaded) return
        try {
            val voiceModel = VoiceModel(vvmPath.toString())
            synthesizer.loadVoiceModel(voiceModel)
        } catch (e: Exception) {
            Logger.e("Failed to loadVoiceModel", e)
        }
    }

    private suspend fun playWavFile(wavFilePath: Path) = suspendCoroutine { continuation ->
        mediaPlayer?.stop()
        mediaPlayer?.release()

        val mediaPlayer = MediaPlayer()
        this.mediaPlayer = mediaPlayer
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.stop()
            mediaPlayer.release()
            this.mediaPlayer = null
            continuation.resume(Unit)
        }
        mediaPlayer.setDataSource(wavFilePath.toString())
        mediaPlayer.prepare()
        mediaPlayer.start()
    }
}
