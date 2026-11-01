# RhymeBeat - Beat-Synced Lyric Generator & Rhyme Engine

An Android music studio application that generates rhythm-matched lyrics over real audio beats and delivers genre-specialized rhyme dictionaries with cultural slang, idioms, and targeted phrase integration.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The app features a live procedural beat engine with real Android `AudioTrack` synthesis (Kick, Snare, Hi-hat, 808 bass) across Hip-Hop, Country, Pop, R&B, Rock, and Lo-Fi, ensuring you can hear and feel the beat in real time as lyrics are written and delivered.

- **Audio Engine**: Real-time procedural PCM audio drum machine with dynamic tempo (60–180 BPM), tap tempo, and rhythmic visualizer pulses.
- **Genre Dictionaries**: Built-in curated rhyme dictionaries for 6+ genres with authentic slang, cultural idioms, slant/near rhymes, and multi-syllable phrases.
- **Phrase Integration Options**: End Rhyme (bar landing), Internal Rhyme (mid-line cadence), and Thematic Hook (conceptual anchor & refrain).
- **AI Backend**: Firebase AI Logic via Gemini 3.8 Flash with structured JSON output and real-time streaming, verified through App Check.
- **Local Persistence**: Room database for storing saved songs, recorded verse drafts, and rhyme scratchpads offline.

---

## 1. Overview & Core Concept

- **What It Does**: RhymeBeat empowers lyricists, rappers, and songwriters to compose in rhythm. Users select a musical genre and tempo, start a matching real audio beat, explore authentic genre slang and rhyme suggestions, and prompt Gemini 3.8 Flash to generate structured verses and hooks that integrate custom words or phrases via chosen rhyme mechanisms.
- **Target Audience**: Songwriters, freestyle rappers, battle rap enthusiasts, pop vocalists, country balladeers, and bedroom music producers.
- **Key Value**: Bridges the gap between passive lyric generation and actual musical composition by tying lyrics directly to real drum rhythms, bar divisions, syllable flow, and culturally genuine genre vocabularies.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Studio Beat & Flow Screen**:
   - User picks a genre pill (Hip-Hop, Trap, Pop, Country, R&B, Lo-Fi, Rock).
   - Tempo automatically adjusts to the genre standard (e.g. 90 BPM for Hip-Hop, 140 BPM for Trap, 105 BPM for Country, 120 BPM for Pop) with an interactive slider, + / - buttons, and a "Tap Tempo" pad.
   - Tapping **Play Beat** starts procedural audio drums. A 4-beat bar meter and waveform pulse in real-time.
   - User inputs a target word/phrase (e.g., *"Neon City"*, *"Gravel Road"*, *"Hustle & Flow"*), selects the integration mode (**End Rhyme**, **Internal Rhyme**, or **Thematic Hook**), chooses song section (Verse, Chorus, Freestyle 16), and taps **Generate Lyrics**.
   - Lyrics stream in with bar count markers `[Bar 1]`, syllable counts, highlighted rhyme words, and delivery notes.
   - User can play the beat while scrolling lyrics, edit lines on the fly, and save the draft to their Songbook.

2. **Genre Rhyme Dictionary & Slang Lexicon**:
   - Dedicated search tab with instant lookup for any word.
   - Displays categorized suggestions:
     - **Exact Rhymes** (perfect phonetic matches).
     - **Slant & Near Rhymes** (flow-friendly cadence rhymes).
     - **Genre Slang & Idiomatic Phrases** (e.g., Hip-Hop: *bag, drop 16, cold game, drip, cypher*; Country: *backroads, tailgate, two-step, honeysuckle*; Pop: *glow-up, midnight drive, neon haze*).
     - **Multi-syllable Rhymes** (compound rhyme schemes).
   - Tapping any word copies it, inserts it into the active scratchpad, or sets it as the prompt input.

3. **Songbook & Scratchpad**:
   - Local Room database list of saved songs and lyric drafts.
   - Quick copy, share, line-by-line rhyme tagging, and beat replay with saved tempo settings.

### Visual Identity & Theme

- **Aesthetic Direction**: Dark Neon Music Studio / Cyber DAW (Digital Audio Workstation) aesthetic. Sleek obsidian surfaces, glowing cyan (#00F5D4) and electric magenta (#F72585) accents, warm amber (#FFB703) for country/acoustic modes.
- **Color Palette**:
  - Background: Deep Charcoal / Studio Night (`#0E0B16` / `#161224`)
  - Primary Accent: Neon Violet / Magenta (`#BD00FF` / `#F72585`)
  - Secondary Accent: Electric Cyan (`#00F5D4`)
  - Surface Containers: Glassmorphic elevated dark tiles (`#1E1A2E`, `#28233C`)
  - Text: High-contrast pure white (`#FFFFFF`) and muted lavender gray (`#B0A8C0`)
- **Typography & Rhythm**:
  - Punchy bold headlines with monospaced bar counters (`01`, `02`, `03`, `04`).
  - Syllable badges and rhyme scheme indicators (`[A]`, `[B]`).
- **Touch & Accessibility**:
  - 48dp+ tap targets for play/stop, tempo increment, tap tempo, and rhyme chips.
  - Edge-to-edge support with safe window insets and responsive tablet layout (`widthIn(max = 680.dp)`).

---

## 3. Key Product Decisions & Trade-Offs

- **Real Audio Synthesizer vs. Pre-rendered Audio Files**:
  - *Chosen Approach*: Lightweight procedural PCM drum synthesis via Android `AudioTrack` (808 sub kick, tight snare, noise hi-hat, clap, rimshot) scheduled per beat.
  - *Why*: Zero external audio file download overhead, instant tempo alteration without pitch distortion or audio stretching artifacts, tiny APK size, and rock-solid timing accuracy.
- **Firebase AI Logic (Gemini 3.8 Flash) with App Check**:
  - *Chosen Approach*: Official Firebase AI Logic integration with structured schema and streaming.
  - *Why*: State-of-the-art lyric generation with deep understanding of cadence, meter, slang, and rhyme structures without client API key exposure.
- **Hybrid Local Dictionaries + AI Expansion**:
  - *Chosen Approach*: Curated in-memory fast genre lexicons for instant offline rhyme browsing, paired with Gemini for dynamic contextual rhyming and phrase integration.
  - *Why*: Instantaneous user feedback when typing, with infinite depth via AI when requested.
- **Room Persistence**:
  - *Chosen Approach*: Local SQLite database via Room with Kotlin Coroutines Flow for draft storage.
  - *Why*: Keeps user drafts safe across sessions without requiring mandatory Google account sign-in for writing lyrics.

---

## 4. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────┐
│                       UI Layer (Compose)                    │
│  ┌─────────────────┐  ┌──────────────────┐  ┌─────────────┐ │
│  │ BeatStudioView  │  │ RhymeDictionary  │  │ SongbookView│ │
│  └────────┬────────┘  └────────┬─────────┘  └──────┬──────┘ │
└───────────┼────────────────────┼───────────────────┼────────┘
            ▼                    ▼                   ▼
┌─────────────────────────────────────────────────────────────┐
│                 RhymeBeatViewModel / UI State               │
│  - Active Genre & BPM      - Lyric Generation UI State      │
│  - Target Phrase & Mode    - Beat Playing & Bar Position    │
│  - Rhyme Query & Results   - Saved Song Drafts List         │
└───────┬──────────────────────┬───────────────────────┬──────┘
        ▼                      ▼                       ▼
┌───────────────┐     ┌────────────────┐      ┌────────────────┐
│ AudioBeatSynth│     │ LyricGenerator │      │ RhymeDataStore │
│ (AudioTrack   │     │ (Firebase AI   │      │ & Room DB      │
│  PCM drums,   │     │  Gemini 3.8    │      │ (Local SQLite  │
│  beat clock)  │     │  Flash + JSON) │      │  Song Drafts)  │
└───────────────┘     └────────────────┘      └────────────────┘
```

### Data Entities & Schema

- **SongDraft**:
  - `id: Long` (Primary Key, autoincrement)
  - `title: String`
  - `genre: String` (Hip-Hop, Trap, Pop, Country, R&B, Lo-Fi, Rock)
  - `tempoBpm: Int`
  - `targetPhrase: String`
  - `integrationMode: String` (EndRhyme, InternalRhyme, Thematic)
  - `lyricsContent: String`
  - `rhymeNotes: String`
  - `updatedAt: Long`

- **GenreLexiconEntry**:
  - `genre: Genre`
  - `word: String`
  - `category: String` (Slang, Idiom, CulturalTerm)
  - `meaningOrUsage: String`
  - `rhymeKeywords: List<String>`

- **GeneratedBarLine**:
  - `barNumber: Int`
  - `lineText: String`
  - `rhymeSchemeTag: String` (A, B, C, D)
  - `syllableCount: Int`
  - `rhymeWords: List<String>`
  - `deliveryTip: String`

---

## 5. Implementation Steps

1. **Audio Beat Synthesizer (`AudioBeatEngine.kt`)**: Procedural drum rhythm generation (Kick, Snare, Hi-hat, 808 Bass) with variable BPM and tempo-synced beat callbacks.
2. **Genre Lexicon & Rhyme Database (`GenreRhymeDictionary.kt`)**: Curated slang, idioms, slant rhymes, and vocabulary for Hip-Hop, Country, Pop, R&B, Rock, and Lo-Fi.
3. **App Check & AI Logic Service (`FirebaseAiLyricService.kt` + `AppCheckInit.kt`)**: Gemini 3.8 Flash model setup with structured schema for phrase integration and lyrics generation.
4. **Room Database (`SongDao.kt`, `SongDatabase.kt`, `SongDraft.kt`)**: Data access layer for lyric notebook and drafts.
5. **ViewModel (`RhymeBeatViewModel.kt`)**: State management combining audio engine state, rhyme dictionary lookup, AI generation with progress and error handling, and draft saving.
6. **Compose UI Components & Screens**:
   - Studio Beat & Lyric Generator Screen with Beat Bar visualizer and phrase integration selector.
   - Rhyme Finder & Genre Slang Dictionary Screen with category filtering and instant copy.
   - Songbook Screen for review, editing, and audio replay.
   - Polished M3 Theme with modern dark DAW styling.
7. **Verification**: Compile applet and verify full build.
