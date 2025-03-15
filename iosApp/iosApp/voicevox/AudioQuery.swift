import Foundation
import voicevox_core

public struct AudioQuery: Codable {
    public var accent_phrases: [AccentPhrase]
    public var speedScale: Float
    public var pitchScale: Float
    public var intonationScale: Float
    public var volumeScale: Float
    public var prePhonemeLength: Float
    public var postPhonemeLength: Float
    public var outputSamplingRate: Float
    public var outputStereo: Bool
    public var kana: String

    public init(json: String) {
        let decoder = JSONDecoder()
        self = try! decoder.decode(AudioQuery.self, from: json.data(using: .utf8)!)
    }

    public var json: String {
        let encoder = JSONEncoder()
        let aqData = try! encoder.encode(self)
        return String(data: aqData, encoding: .utf8)!
    }

    public init(accentPhrases: [AccentPhrase], speedScale: Float, pitchScale: Float, intonationScale: Float, volumeScale: Float, prePhonemeLength: Float, postPhonemeLength: Float, outputSamplingRate: Float, outputStereo: Bool, kana: String) {
        self.accent_phrases = accentPhrases
        self.speedScale = speedScale
        self.pitchScale = pitchScale
        self.intonationScale = intonationScale
        self.volumeScale = volumeScale
        self.prePhonemeLength = prePhonemeLength
        self.postPhonemeLength = postPhonemeLength
        self.outputSamplingRate = outputSamplingRate
        self.outputStereo = outputStereo
        self.kana = kana
    }
}

public struct AccentPhrase: Codable {
    public var moras: [Mora]
    public var accent: Int
    public var pause_mora: Mora?
    public var is_interrogative: Bool

    public init(moras: [Mora], accent: Int, pauseMora: Mora? = nil, isInterrogative: Bool) {
        self.moras = moras
        self.accent = accent
        self.pause_mora = pauseMora
        self.is_interrogative = isInterrogative
    }
}

public struct Mora: Codable {
    public var text: String
    public var consonant: String?
    public var consonant_length: Float?
    public var vowel: String
    public var vowel_length: Float
    public var pitch: Float

    public init(text: String, consonant: String? = nil, consonantLength: Float? = nil, vowel: String, vowelLength: Float, pitch: Float) {
        self.text = text
        self.consonant = consonant
        self.consonant_length = consonantLength
        self.vowel = vowel
        self.vowel_length = vowelLength
        self.pitch = pitch
    }
}
