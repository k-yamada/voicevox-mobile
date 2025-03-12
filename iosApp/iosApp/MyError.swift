import Foundation
import ComposeApp

enum MyError: Error {
    case runtimeError(String)

    var voicevoxError: VoicevoxError {
        switch self {
        case .runtimeError(let message):
            return VoicevoxError.RuntimeError(message: message)
        }
    }
}
