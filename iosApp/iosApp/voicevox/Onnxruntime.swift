import Foundation
import voicevox_core

public class Onnxruntime {
    var ptr: OpaquePointer?

    public init() throws {
        let resultCode = voicevox_onnxruntime_init_once(&ptr)
        if resultCode != VOICEVOX_RESULT_OK.rawValue || ptr == nil {
            let message = (NSString(utf8String: voicevox_error_result_to_message(resultCode)) as? String) ?? "-"
            throw MyError.runtimeError("failed to voicevox_onnxruntime_init_once: result=\(resultCode) message=\(message)")
        }
    }

    func getSupportedDevicesJson() -> String {
        var ptrJson: UnsafeMutablePointer<CChar>? = nil
        voicevox_onnxruntime_create_supported_devices_json(ptr, &ptrJson)
        guard let ptrJson = ptrJson else { return "" }
        return NSString(utf8String: ptrJson) as? String ?? ""
    }
}
