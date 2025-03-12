package app.example.voicevox

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.readRemaining
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path
import okio.SYSTEM

class VoicevoxApi {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            })
        }
    }

    suspend fun downloadVvm(vvmFileName: String, filePath: Path) {
        httpClient.prepareGet("https://github.com/k-yamada/voicevox_fat_resource/raw/main/core/model/${vvmFileName}")
            .execute { httpResponse ->
                writeFile(httpResponse, filePath)
            }
    }

    suspend fun downloadOpenJtalkZip(filePath: Path) {
        httpClient.prepareGet("https://github.com/k-yamada/voicevox_fat_resource/releases/download/1.0.0/open_jtalk_dic_utf_8-1.11.zip")
            .execute { httpResponse ->
                writeFile(httpResponse, filePath)
            }
    }

    private suspend fun writeFile(httpResponse: HttpResponse, filePath: Path) {
        val channel: ByteReadChannel = httpResponse.body()
        FileSystem.SYSTEM.write(filePath) {
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                while (!packet.exhausted()) {
                    val bytes = packet.readBytes()
                    write(bytes)
                }
            }
        }
    }

    companion object {
        const val DEFAULT_BUFFER_SIZE = 4098
    }
}
