import Foundation
import voicevox_core

public class VoiceModel {
    var ptr: OpaquePointer?
    var path: String

    var id: VoicevoxVoiceModelId {
        get {
            VoicevoxVoiceModelId(ptr!)
        }
    }

    public init(voiceModelURL: URL) throws {
        let resultCode = voicevox_voice_model_file_open(voiceModelURL.path, &ptr)
        if resultCode != VOICEVOX_RESULT_OK.rawValue || ptr == nil {
            let message = (NSString(utf8String: voicevox_error_result_to_message(resultCode)) as? String) ?? "-"
            throw MyError.runtimeError("failed to voicevox_voice_model_new_from_path: result=\(resultCode) message=\(message)")
        }
        path = voiceModelURL.path
    }
}
