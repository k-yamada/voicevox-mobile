package app.example.voicevox

import android.media.MediaPlayer
import app.example.MainActivity
import app.example.voicevox.VoicevoxNative.Companion.STYLE_ID
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

    override suspend fun speak(text: String, openJtalkDicDirPath: Path, vvmFilePath: Path) {
        withContext(Dispatchers.IO) {
            val synthesizer = makeSynthesizer(openJtalkDicDirPath)
            loadVoiceModelIfNeeded(synthesizer, vvmFilePath)
            val wavFilePath = tempWavPath

            // wavファイル作成
            val wavFile = File(wavFilePath.toString())

            // 音声合成
            val audioQuery = try {
                synthesizer.createAudioQuery(text, STYLE_ID)
            } catch (e: Exception) {
                Logger.e("Failed to createAudioQuery", e)
                return@withContext
            }

            val wavData = synthesizer.synthesis(audioQuery, STYLE_ID).execute()
            val fos = FileOutputStream(wavFile)
            fos.write(wavData)
            fos.close()

            playWavFile(wavFilePath)

            wavFile.delete()
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

    private var isVoiceModelLoaded = false

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
