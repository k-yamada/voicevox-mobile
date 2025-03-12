import Foundation
import voicevox_core

public class OpenJtalkRc {
    var ptr: OpaquePointer?

    public init(openJtalkDic: URL) throws {
        let resultCode = voicevox_open_jtalk_rc_new(openJtalkDic.path, &ptr)
        if resultCode != VOICEVOX_RESULT_OK.rawValue || ptr == nil {
            let message = (NSString(utf8String: voicevox_error_result_to_message(resultCode)) as? String) ?? "-"
            throw MyError.runtimeError("failed to voicevox_open_jtalk_rc_new: result=\(resultCode) message=\(message)")
        }
    }
}
