package app.example.voicevox

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import okio.buffer
import okio.openZip
import okio.use

class VoicevoxSdk(
    private val native: VoicevoxNative,
) : CoroutineScope by CoroutineScope(Dispatchers.Main) {
    private val api = VoicevoxApi()
    private val openJtalkDicDirPath = "${native.libraryDir}/$OPEN_JTALK_DIC_DIR".toPath()
    private val modelDirPath = "${native.libraryDir}/$MODEL_DIR".toPath()
    private val vvmFilePath = "${modelDirPath}/${VVM_FILE_NAME}".toPath()

    suspend fun speak(text: String) {
        downloadOpenJtalkDicIfNeeded()
        downloadVvmFileIfNeeded()
        val wavFilePath = "${native.cacheDir}/temp.wav".toPath()
        native.speak(text = text, wavFilePath = wavFilePath, openJtalkDicDirPath = openJtalkDicDirPath, vvmFilePath = vvmFilePath, styleId = STYLE_ID)
        FileSystem.SYSTEM.delete(wavFilePath)
    }

    private suspend fun downloadOpenJtalkDicIfNeeded() {
        if (FileSystem.SYSTEM.exists(openJtalkDicDirPath)) {
            return
        }
        withContext(Dispatchers.Default) {
            val zipFilePath = "${native.cacheDir}/open_jtalk.zip".toPath()
            val jtalkDicDir = "${native.libraryDir}/$OPEN_JTALK_DIC_DIR".toPath()
            try {
                if (FileSystem.SYSTEM.exists(zipFilePath)) {
                    FileSystem.SYSTEM.delete(zipFilePath)
                }
                if (FileSystem.SYSTEM.exists(jtalkDicDir)) {
                    FileSystem.SYSTEM.delete(jtalkDicDir)
                }

                api.downloadOpenJtalkZip(zipFilePath)
                val zipFileSystem = FileSystem.SYSTEM.openZip(zipFilePath)
                FileSystem.SYSTEM.createDirectory(jtalkDicDir)
                val fileList = zipFileSystem.list("/open_jtalk_dic_utf_8-1.11".toPath())
                fileList.forEach {
                    zipFileSystem.source(it).buffer().use { source ->
                        FileSystem.SYSTEM.write("${jtalkDicDir}/${it.name}".toPath()) {
                            writeAll(source)
                        }
                    }
                }
                FileSystem.SYSTEM.delete(zipFilePath)
            } catch (e: Exception) {
                Logger.e("Failed to downloadOpenJtalkDic", e)
                FileSystem.SYSTEM.delete(jtalkDicDir)
            }
        }
    }

    private suspend fun downloadVvmFileIfNeeded() {
        if (FileSystem.SYSTEM.exists(vvmFilePath)) {
            return
        }
        FileSystem.SYSTEM.createDirectory(modelDirPath)
        api.downloadVvm(VVM_FILE_NAME, vvmFilePath)
    }

    companion object {
        const val OPEN_JTALK_DIC_DIR = "open_jtalk_dic"
        const val MODEL_DIR = "voicevox_model"
        const val VVM_FILE_NAME = "0.vvm"
        const val STYLE_ID = 3
    }
}
