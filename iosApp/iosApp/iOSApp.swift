import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        KoinHelperKt.doInitKoin(
            voicevoxNative: VoicevoxNativeImpl()
        )
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
