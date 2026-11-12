package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.FirebaseAiLyricService
import com.example.ai.GeneratedLyricResult
import com.example.ai.IntegrationMode
import com.example.ai.SongSection
import com.example.audio.AudioBeatEngine
import com.example.audio.BeatPosition
import com.example.audio.DrumSound
import com.example.audio.Genre
import com.example.audio.KeyboardNote
import com.example.audio.SynthInstrument
import com.example.audio.SynthKeyboardEngine
import com.example.data.GenreRhymeDictionary
import com.example.data.RhymeEntry
import com.example.data.RhymeType
import com.example.data.SongDatabase
import com.example.data.SongDraft
import com.example.data.SongRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "RhymeBeatVM"

enum class SongbookSortOrder(val label: String) {
    NEWEST("Newest"),
    OLDEST("Oldest"),
    TITLE("Title (A-Z)"),
    BPM("Tempo (BPM)")
}

sealed interface LyricUiState {
    data object Idle : LyricUiState
    data object Loading : LyricUiState
    data class Success(val result: GeneratedLyricResult) : LyricUiState
    data class Error(val message: String) : LyricUiState
}

class RhymeBeatViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: SongRepository = SongRepository(SongDatabase.getInstance(application).songDao())
) : AndroidViewModel(application) {

    private val aiService = FirebaseAiLyricService()

    val beatEngine = AudioBeatEngine(viewModelScope)
    val synthKeyboard = SynthKeyboardEngine(viewModelScope)

    // Current Beat and Genre state
    val beatPosition: StateFlow<BeatPosition> = beatEngine.beatPosition
    val currentGenre: StateFlow<Genre> = beatEngine.currentGenre
    val bpm: StateFlow<Int> = beatEngine.bpm
    val isMuted: StateFlow<Boolean> = beatEngine.isMuted

    // Synth Keyboard & Drum Pads state
    val synthInstrument: StateFlow<SynthInstrument> = synthKeyboard.currentInstrument
    val synthOctave: StateFlow<Int> = synthKeyboard.currentOctave
    val lastPlayedNote: StateFlow<String?> = synthKeyboard.lastPlayedNote
    val lastTriggeredDrum: StateFlow<DrumSound?> = synthKeyboard.lastTriggeredDrum

    // Generated Lyrics Saved to Room state
    private val _isGeneratedLyricsSaved = MutableStateFlow(false)
    val isGeneratedLyricsSaved: StateFlow<Boolean> = _isGeneratedLyricsSaved.asStateFlow()

    // Prompt & Integration inputs
    private val _targetPhrase = MutableStateFlow("Midnight Drive")
    val targetPhrase: StateFlow<String> = _targetPhrase.asStateFlow()

    private val _integrationMode = MutableStateFlow(IntegrationMode.END_RHYME)
    val integrationMode: StateFlow<IntegrationMode> = _integrationMode.asStateFlow()

    private val _songSection = MutableStateFlow(SongSection.VERSE_8)
    val songSection: StateFlow<SongSection> = _songSection.asStateFlow()

    // Lyric Generation UI State
    private val _lyricState = MutableStateFlow<LyricUiState>(LyricUiState.Idle)
    val lyricState: StateFlow<LyricUiState> = _lyricState.asStateFlow()

    // Live Lyric Editor State
    private val _editorText = MutableStateFlow("Cruising through the city under neon light\nLooking for the rhythm of the night")
    val editorText: StateFlow<String> = _editorText.asStateFlow()

    private val _lastWordTyped = MutableStateFlow("night")
    val lastWordTyped: StateFlow<String> = _lastWordTyped.asStateFlow()

    private val _isRhymeSheetOpen = MutableStateFlow(false)
    val isRhymeSheetOpen: StateFlow<Boolean> = _isRhymeSheetOpen.asStateFlow()

    private val _editorRhymeTypeFilter = MutableStateFlow(RhymeType.ALL)
    val editorRhymeTypeFilter: StateFlow<RhymeType> = _editorRhymeTypeFilter.asStateFlow()

    private val _realtimeRhymeSuggestions = MutableStateFlow<List<RhymeEntry>>(emptyList())
    val realtimeRhymeSuggestions: StateFlow<List<RhymeEntry>> = _realtimeRhymeSuggestions.asStateFlow()

    // Active project being edited (if loaded from past drafts)
    private val _activeDraftId = MutableStateFlow<Long?>(null)
    val activeDraftId: StateFlow<Long?> = _activeDraftId.asStateFlow()

    private val _activeDraftTitle = MutableStateFlow<String?>(null)
    val activeDraftTitle: StateFlow<String?> = _activeDraftTitle.asStateFlow()

    // Save project dialog trigger
    private val _isSaveProjectDialogOpen = MutableStateFlow(false)
    val isSaveProjectDialogOpen: StateFlow<Boolean> = _isSaveProjectDialogOpen.asStateFlow()

    // Songbook music audio playback tracking
    private val _songbookPlayingDraftId = MutableStateFlow<Long?>(null)
    val songbookPlayingDraftId: StateFlow<Long?> = _songbookPlayingDraftId.asStateFlow()

    // Rhyme Dictionary Screen State
    private val _dictionaryQuery = MutableStateFlow("")
    val dictionaryQuery: StateFlow<String> = _dictionaryQuery.asStateFlow()

    private val _selectedRhymeType = MutableStateFlow(RhymeType.ALL)
    val selectedRhymeType: StateFlow<RhymeType> = _selectedRhymeType.asStateFlow()

    private val _rhymeResults = MutableStateFlow<List<RhymeEntry>>(emptyList())
    val rhymeResults: StateFlow<List<RhymeEntry>> = _rhymeResults.asStateFlow()

    // Room Saved Drafts & Project History
    val savedDrafts: StateFlow<List<SongDraft>> = repository.allDrafts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Project History Filters & Search
    private val _songbookSearchQuery = MutableStateFlow("")
    val songbookSearchQuery: StateFlow<String> = _songbookSearchQuery.asStateFlow()

    private val _songbookGenreFilter = MutableStateFlow<Genre?>(null)
    val songbookGenreFilter: StateFlow<Genre?> = _songbookGenreFilter.asStateFlow()

    private val _songbookOnlyFavorites = MutableStateFlow(false)
    val songbookOnlyFavorites: StateFlow<Boolean> = _songbookOnlyFavorites.asStateFlow()

    private val _songbookSortOrder = MutableStateFlow(SongbookSortOrder.NEWEST)
    val songbookSortOrder: StateFlow<SongbookSortOrder> = _songbookSortOrder.asStateFlow()

    // Filtered and Sorted past project history for the Songbook
    val filteredDrafts: StateFlow<List<SongDraft>> = combine(
        savedDrafts,
        _songbookSearchQuery,
        _songbookGenreFilter,
        _songbookOnlyFavorites,
        _songbookSortOrder
    ) { drafts, query, genreFilter, onlyFavorites, sortOrder ->
        var list = drafts

        if (onlyFavorites) {
            list = list.filter { it.isFavorite }
        }

        if (genreFilter != null) {
            list = list.filter { it.genreName.equals(genreFilter.name, ignoreCase = true) }
        }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                        it.lyricsContent.lowercase().contains(q) ||
                        it.targetPhrase.lowercase().contains(q)
            }
        }

        when (sortOrder) {
            SongbookSortOrder.NEWEST -> list.sortedByDescending { it.updatedAt }
            SongbookSortOrder.OLDEST -> list.sortedBy { it.updatedAt }
            SongbookSortOrder.TITLE -> list.sortedBy { it.title.lowercase() }
            SongbookSortOrder.BPM -> list.sortedByDescending { it.tempoBpm }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Snack bar / User notice
    private val _userNotice = MutableStateFlow<String?>(null)
    val userNotice: StateFlow<String?> = _userNotice.asStateFlow()

    init {
        refreshRhymeDictionary()
        refreshRealtimeEditorRhymes()
    }

    fun setTargetPhrase(phrase: String) {
        _targetPhrase.value = phrase
    }

    fun setIntegrationMode(mode: IntegrationMode) {
        _integrationMode.value = mode
    }

    fun setSongSection(section: SongSection) {
        _songSection.value = section
    }

    fun selectGenre(genre: Genre) {
        beatEngine.setGenre(genre)
        refreshRhymeDictionary()
        refreshRealtimeEditorRhymes()
    }

    fun setBpm(newBpm: Int) {
        beatEngine.setBpm(newBpm)
    }

    fun tapTempo() {
        beatEngine.registerTap()
    }

    fun toggleBeat() {
        beatEngine.togglePlay()
    }

    fun toggleMute() {
        beatEngine.toggleMute()
    }

    // --- Lyric Editor & Real-time Rhyme Suggestions ---

    fun updateEditorText(newText: String) {
        _editorText.value = newText
        val last = GenreRhymeDictionary.extractLastWord(newText)
        _lastWordTyped.value = last
        refreshRealtimeEditorRhymes()
    }

    fun openRhymeSheet() {
        _isRhymeSheetOpen.value = true
    }

    fun closeRhymeSheet() {
        _isRhymeSheetOpen.value = false
    }

    fun toggleRhymeSheet() {
        _isRhymeSheetOpen.value = !_isRhymeSheetOpen.value
    }

    fun setEditorRhymeTypeFilter(filter: RhymeType) {
        _editorRhymeTypeFilter.value = filter
        refreshRealtimeEditorRhymes()
    }

    fun insertRhymeIntoEditor(rhymeWord: String) {
        val current = _editorText.value
        val updated = if (current.isEmpty()) {
            rhymeWord
        } else if (current.endsWith(" ") || current.endsWith("\n")) {
            current + rhymeWord
        } else {
            current + " " + rhymeWord
        }
        updateEditorText(updated)
        _userNotice.value = "Inserted \"$rhymeWord\""
    }

    fun importAiLyricsToEditor() {
        val currentLyricState = _lyricState.value
        if (currentLyricState is LyricUiState.Success) {
            val bars = currentLyricState.result.bars
            val formatted = buildString {
                for (bar in bars) {
                    appendLine(bar.text)
                }
            }.trimEnd()
            updateEditorText(formatted)
            _userNotice.value = "Imported AI lyrics into editor!"
        }
    }

    // --- Local Room Database Operations (User Drafts & Past Projects) ---

    fun openSaveProjectDialog() {
        if (_editorText.value.trim().isBlank()) {
            _userNotice.value = "Editor is empty. Write some lyrics first!"
            return
        }
        _isSaveProjectDialogOpen.value = true
    }

    fun closeSaveProjectDialog() {
        _isSaveProjectDialogOpen.value = false
    }

    fun saveUserWrittenLyrics(customTitle: String? = null, saveAsNew: Boolean = false): Job {
        val text = _editorText.value.trim()
        if (text.isBlank()) {
            _userNotice.value = "Editor is empty. Write some lyrics first!"
            return Job().apply { complete() }
        }

        return viewModelScope.launch {
            val defaultTitle = text.lines().firstOrNull { it.isNotBlank() } ?: "Untitled Project"
            val sanitizedDefault = if (defaultTitle.length > 32) defaultTitle.take(32) + "..." else defaultTitle
            val finalTitle = if (!customTitle.isNullOrBlank()) customTitle.trim() else sanitizedDefault

            val currentLoadedId = _activeDraftId.value

            if (currentLoadedId != null && !saveAsNew) {
                // Update existing project draft in Room
                val updatedDraft = SongDraft(
                    id = currentLoadedId,
                    title = finalTitle,
                    genreName = beatEngine.currentGenre.value.name,
                    tempoBpm = beatEngine.bpm.value,
                    targetPhrase = _lastWordTyped.value,
                    integrationMode = _integrationMode.value.name,
                    lyricsContent = text,
                    rhymeNotes = "Saved with rhyme anchor '${_lastWordTyped.value}'",
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateDraft(updatedDraft)
                _activeDraftTitle.value = finalTitle
                _userNotice.value = "Updated project '$finalTitle' in history!"
            } else {
                // Insert new project draft in Room
                val newDraft = SongDraft(
                    title = finalTitle,
                    genreName = beatEngine.currentGenre.value.name,
                    tempoBpm = beatEngine.bpm.value,
                    targetPhrase = _lastWordTyped.value,
                    integrationMode = _integrationMode.value.name,
                    lyricsContent = text,
                    rhymeNotes = "Saved with rhyme anchor '${_lastWordTyped.value}'",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                val newId = repository.insertDraft(newDraft)
                _activeDraftId.value = newId
                _activeDraftTitle.value = finalTitle
                _userNotice.value = "Saved project '$finalTitle' to history!"
            }
            _isSaveProjectDialogOpen.value = false
        }
    }

    fun saveCurrentSongDraft(customTitle: String? = null): Job {
        val currentState = _lyricState.value
        if (currentState is LyricUiState.Success) {
            return viewModelScope.launch {
                val result = currentState.result
                val finalTitle = if (!customTitle.isNullOrBlank()) customTitle.trim() else result.title
                val fullLyrics = buildString {
                    appendLine("## $finalTitle")
                    appendLine("Genre: ${beatEngine.currentGenre.value.displayName} | Tempo: ${beatEngine.bpm.value} BPM")
                    appendLine("Target Phrase: ${_targetPhrase.value} (${_integrationMode.value.displayName})")
                    appendLine("Section: ${_songSection.value.displayName}")
                    if (result.tempoNotes.isNotBlank()) {
                        appendLine("Cadence: ${result.tempoNotes}")
                    }
                    if (result.integrationExplanation.isNotBlank()) {
                        appendLine("Integration: ${result.integrationExplanation}")
                    }
                    appendLine()
                    for (b in result.bars) {
                        val tip = if (b.deliveryTip.isNotBlank()) " — [🎙️ ${b.deliveryTip}]" else ""
                        appendLine("[Bar ${String.format("%02d", b.barNumber)} | ${b.rhymeSchemeTag}] ${b.text}$tip")
                    }
                    if (result.culturalTermsUsed.isNotEmpty()) {
                        appendLine("\nCultural Slang Used: ${result.culturalTermsUsed.joinToString(", ")}")
                    }
                    if (result.suggestedRhymeFollowUps.isNotEmpty()) {
                        appendLine("Rhyme Ideas: ${result.suggestedRhymeFollowUps.joinToString(", ")}")
                    }
                }

                val draft = SongDraft(
                    title = finalTitle,
                    genreName = beatEngine.currentGenre.value.name,
                    tempoBpm = beatEngine.bpm.value,
                    targetPhrase = _targetPhrase.value,
                    integrationMode = _integrationMode.value.name,
                    lyricsContent = fullLyrics,
                    rhymeNotes = result.integrationExplanation.ifBlank { "AI Generated ${_songSection.value.displayName}" },
                    isAiGenerated = true,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                val newId = repository.insertDraft(draft)
                _activeDraftId.value = newId
                _activeDraftTitle.value = finalTitle
                _isGeneratedLyricsSaved.value = true
                _userNotice.value = "Saved '$finalTitle' to local device database!"
            }
        } else {
            _userNotice.value = "Generate lyrics first before saving to local database."
            return Job().apply { complete() }
        }
    }

    // --- Synth Keyboard & Drum Pads Actions ---

    fun playKeyboardNote(note: KeyboardNote) {
        synthKeyboard.playNote(note)
    }

    fun playDrumPad(sound: DrumSound) {
        synthKeyboard.playDrum(sound)
    }

    fun setSynthInstrument(instrument: SynthInstrument) {
        synthKeyboard.setInstrument(instrument)
    }

    fun octaveUp() {
        synthKeyboard.octaveUp()
    }

    fun octaveDown() {
        synthKeyboard.octaveDown()
    }

    fun startNewProject() {
        _activeDraftId.value = null
        _activeDraftTitle.value = null
        _editorText.value = ""
        _lastWordTyped.value = ""
        _userNotice.value = "Started a new lyric project!"
    }

    fun deleteDraft(draft: SongDraft) {
        viewModelScope.launch {
            repository.deleteDraft(draft)
            if (_activeDraftId.value == draft.id) {
                _activeDraftId.value = null
                _activeDraftTitle.value = null
            }
            _userNotice.value = "Deleted '${draft.title}'"
        }
    }

    fun duplicateDraft(draft: SongDraft) {
        viewModelScope.launch {
            val newId = repository.duplicateDraft(draft)
            _userNotice.value = "Created copy of '${draft.title}'"
        }
    }

    fun toggleFavoriteDraft(draft: SongDraft) {
        viewModelScope.launch {
            repository.toggleFavorite(draft)
        }
    }

    fun updateDraftTitle(draft: SongDraft, newTitle: String) {
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            val updated = draft.copy(
                title = newTitle.trim(),
                updatedAt = System.currentTimeMillis()
            )
            repository.updateDraft(updated)
            if (_activeDraftId.value == draft.id) {
                _activeDraftTitle.value = newTitle.trim()
            }
            _userNotice.value = "Renamed to '$newTitle'"
        }
    }

    fun togglePlaySongbookDraft(draft: SongDraft) {
        if (_songbookPlayingDraftId.value == draft.id && beatPosition.value.isPlaying) {
            beatEngine.stop()
            _songbookPlayingDraftId.value = null
            _userNotice.value = "Paused music"
        } else {
            try {
                val genre = Genre.valueOf(draft.genreName)
                beatEngine.setGenre(genre)
            } catch (_: Exception) {}
            beatEngine.setBpm(draft.tempoBpm)
            beatEngine.play()
            _songbookPlayingDraftId.value = draft.id
            _userNotice.value = "Playing ${draft.genreName} beat (${draft.tempoBpm} BPM)"
        }
    }

    fun stopSongbookMusic() {
        if (beatPosition.value.isPlaying) {
            beatEngine.stop()
        }
        _songbookPlayingDraftId.value = null
    }

    fun loadDraftIntoStudio(draft: SongDraft) {
        try {
            val genre = Genre.valueOf(draft.genreName)
            selectGenre(genre)
        } catch (_: Exception) {}
        setBpm(draft.tempoBpm)
        if (draft.targetPhrase.isNotBlank()) {
            setTargetPhrase(draft.targetPhrase)
        }
        try {
            val mode = IntegrationMode.valueOf(draft.integrationMode)
            setIntegrationMode(mode)
        } catch (_: Exception) {}

        _activeDraftId.value = draft.id
        _activeDraftTitle.value = draft.title
        updateEditorText(draft.lyricsContent)
        _userNotice.value = "Loaded project '${draft.title}' into Studio"
    }

    // --- Songbook Filtering & Searching Controls ---

    fun setSongbookSearchQuery(query: String) {
        _songbookSearchQuery.value = query
    }

    fun setSongbookGenreFilter(genre: Genre?) {
        _songbookGenreFilter.value = genre
    }

    fun toggleSongbookOnlyFavorites() {
        _songbookOnlyFavorites.value = !_songbookOnlyFavorites.value
    }

    fun setSongbookSortOrder(sortOrder: SongbookSortOrder) {
        _songbookSortOrder.value = sortOrder
    }

    private fun refreshRealtimeEditorRhymes() {
        val last = _lastWordTyped.value
        val genre = beatEngine.currentGenre.value
        val filter = _editorRhymeTypeFilter.value

        val results = GenreRhymeDictionary.searchRhymes(
            query = last,
            genre = genre,
            typeFilter = filter
        )
        _realtimeRhymeSuggestions.value = results
    }

    // --- Rhyme Dictionary Screen Logic ---

    fun setDictionaryQuery(query: String) {
        _dictionaryQuery.value = query
        refreshRhymeDictionary()
    }

    fun setRhymeTypeFilter(type: RhymeType) {
        _selectedRhymeType.value = type
        refreshRhymeDictionary()
    }

    fun refreshRhymeDictionary() {
        val results = GenreRhymeDictionary.searchRhymes(
            query = _dictionaryQuery.value,
            genre = beatEngine.currentGenre.value,
            typeFilter = _selectedRhymeType.value
        )
        _rhymeResults.value = results
    }

    // --- AI Lyric Generation ---

    fun generateLyrics() {
        val phrase = _targetPhrase.value.trim()
        if (phrase.isBlank()) {
            _userNotice.value = "Please enter a target word or phrase first."
            return
        }

        viewModelScope.launch {
            _lyricState.value = LyricUiState.Loading
            _isGeneratedLyricsSaved.value = false
            try {
                val genre = beatEngine.currentGenre.value
                val tempo = beatEngine.bpm.value
                val mode = _integrationMode.value
                val section = _songSection.value

                // Collect top slang context for the prompt
                val slangList = GenreRhymeDictionary.GENRE_LEXICONS[genre]?.culturalTerms
                    ?.map { it.wordOrPhrase }?.take(6) ?: emptyList()

                val result = aiService.generateLyrics(
                    genre = genre,
                    bpm = tempo,
                    targetPhrase = phrase,
                    integrationMode = mode,
                    section = section,
                    genreSlangContext = slangList
                )
                _lyricState.value = LyricUiState.Success(result)
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "AI Generation timed out", e)
                _lyricState.value = LyricUiState.Error("Generation timed out after 60 seconds. Please try again.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Lyric generation failed", e)
                _lyricState.value = LyricUiState.Error(
                    e.localizedMessage ?: "Failed to generate lyrics. Please check your connection."
                )
            }
        }
    }

    fun clearUserNotice() {
        _userNotice.value = null
    }

    override fun onCleared() {
        super.onCleared()
        beatEngine.stop()
        synthKeyboard.release()
    }
}
