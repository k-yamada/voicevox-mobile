import ComposeApp
import voicevox_core
import AVFoundation
import UIKit

class VoicevoxNativeImpl: NSObject, VoicevoxNative {
    var libraryDir: String {
        get {
            FileManager.default.urls(for: .libraryDirectory, in: .userDomainMask).first!.path
        }
    }

    var cacheDir: String {
        get {
            FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask).first!.path
        }
    }
    private var isVoiceModelLoaded = false
    private var player: AVAudioPlayer?
    private var playWavFileContinuation: CheckedContinuation<Void, Never>?

    func speak(text: String, wavFilePath: OkioPath, openJtalkDicDirPath: OkioPath, vvmFilePath: OkioPath, styleId: Int32) async throws {
        do {
            let synthesizer = try makeSynthesizer(openJtalkDictDir: openJtalkDicDirPath.description())
            try loadVoiceModelIfNeeded(synthesizer: synthesizer, vvmPath: vvmFilePath.description())
            let voicevoxStyleId = VoicevoxStyleId(styleId)

            // 音声合成
            guard let audioQuery = synthesizer.audioQuery(text: text, styleId: voicevoxStyleId) else {
                throw MyError.runtimeError("Failed to create audioQuery: \(text)")
            }
            let wavData = try synthesizer.synthesis(audioQuery: audioQuery, styleId: voicevoxStyleId, options: voicevox_make_default_synthesis_options())
            let wavFileURL = URL(fileURLWithPath: wavFilePath.description())
            try wavData.write(to: wavFileURL)

            // 再生
            try await playWavFile(wavFilePath: wavFilePath)
        } catch {
            print(error)
        }
    }

    private func makeSynthesizer(openJtalkDictDir: String) throws -> Synthesizer {
        let onnxruntime = try Onnxruntime()
        let openJtalkDictDirURL = URL(fileURLWithPath: openJtalkDictDir)
        let openJtalk = try OpenJtalkRc(openJtalkDic: openJtalkDictDirURL)
        let initializeOptions: VoicevoxInitializeOptions = voicevox_make_default_initialize_options()
        return try Synthesizer(onnxruntime: onnxruntime, openJtalk: openJtalk, options: initializeOptions)
    }

    private func loadVoiceModelIfNeeded(synthesizer: Synthesizer, vvmPath: String) throws {
        if isVoiceModelLoaded { return }
        let voiceModelURL = URL(fileURLWithPath: vvmPath)
        let voiceModel = try VoiceModel(voiceModelURL: voiceModelURL)
        try synthesizer.loadVoiceModel(voiceModel: voiceModel)
    }

    private func playWavFile(wavFilePath: OkioPath) async throws -> Void {
        return await withCheckedContinuation { continuation in
            playWavFileContinuation = nil
            let fileURL = URL(fileURLWithPath: wavFilePath.description())
            do {
                let wavData = try Data(contentsOf: fileURL, options: [])
                // 再生
                if player?.isPlaying == true {
                    player?.stop()
                }
                player = try? AVAudioPlayer(data: wavData)
                player?.delegate = self
                player?.play()
                playWavFileContinuation = continuation
            } catch {
                print("Failed to read file")
                continuation.resume()
            }
        }
    }
}

extension VoicevoxNativeImpl: AVAudioPlayerDelegate {
    func audioPlayerDidFinishPlaying(_: AVAudioPlayer, successfully: Bool) {
        playWavFileContinuation?.resume()
        playWavFileContinuation = nil
    }
}
