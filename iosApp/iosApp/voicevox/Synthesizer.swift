import Foundation
import voicevox_core

public class Synthesizer {
    var ptr: OpaquePointer?

    public init(onnxruntime: Onnxruntime, openJtalk: OpenJtalkRc, options: VoicevoxInitializeOptions) throws {
        let resultCode = voicevox_synthesizer_new(onnxruntime.ptr, openJtalk.ptr, options, &ptr)
        if resultCode != VOICEVOX_RESULT_OK.rawValue || ptr == nil {
            let message = (NSString(utf8String: voicevox_error_result_to_message(resultCode)) as? String) ?? "-"
            throw MyError.runtimeError("failed to voicevox_synthesizer_new: result=\(resultCode) message=\(message)")
        }
    }

    public func loadVoiceModel(voiceModel: VoiceModel) throws {
        let resultCode = voicevox_synthesizer_load_voice_model(ptr, voiceModel.ptr)
        if resultCode != VOICEVOX_RESULT_OK.rawValue {
            let message = (NSString(utf8String: voicevox_error_result_to_message(resultCode)) as? String) ?? "-"
            throw MyError.runtimeError("failed to voicevox_synthesizer_load_voice_model: result=\(resultCode) message=\(message)")
        }
    }

    public func audioQuery(text: String, styleId: VoicevoxStyleId) -> AudioQuery? {
        var aq: UnsafeMutablePointer<CChar>? = nil
        let _ = voicevox_synthesizer_create_audio_query(ptr,
                                                          (text as NSString).cString(using: NSUTF8StringEncoding),
                                                          styleId,
                                                          &aq)
        if aq == nil { return nil }
        guard let aq = aq else { return nil }
        let str = NSString(utf8String: aq) as? String ?? ""
        return AudioQuery(json: str)
    }

    public func synthesis(audioQuery: AudioQuery, styleId: VoicevoxStyleId, options: VoicevoxSynthesisOptions) throws -> Data {
        var len: UInt = 0
        var out: UnsafeMutablePointer<UInt8>? = nil
        let resultCode = voicevox_synthesizer_synthesis(ptr,
                                                 (audioQuery.json as NSString).cString(using: NSUTF8StringEncoding),
                                                 styleId,
                                                 options,
                                                 &len,
                                                 &out)
        if resultCode != VOICEVOX_RESULT_OK.rawValue {
            let message = (NSString(utf8String: voicevox_error_result_to_message(resultCode)) as? String) ?? "-"
            throw MyError.runtimeError("failed to voicevox_synthesizer_synthesis: result=\(resultCode) message=\(message)")
        }
        guard let out else { return Data() }
        return Data(bytes: out, count: Int(len))
    }
}
