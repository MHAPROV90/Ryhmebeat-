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
import com.example.audio.Genre
import com.example.data.GenreRhymeDictionary
import com.example.data.RhymeEntry
import com.example.data.RhymeType
import com.example.data.SongDatabase
import com.example.data.SongDraft
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "RhymeBeatVM"

sealed interface LyricUiState {
    data object Idle : LyricUiState
    data object Loading : LyricUiState
    data class Success(val result: GeneratedLyricResult) : LyricUiState
    data class Error(val message: String) : LyricUiState
}

class RhymeBeatViewModel(application: Application) : AndroidViewModel(application) {

    private val db = SongDatabase.getInstance(application)
    private val songDao = db.songDao()
    private val aiService = FirebaseAiLyricService()

    val beatEngine = AudioBeatEngine(viewModelScope)

    // Current Beat and Genre state
    val beatPosition: StateFlow<BeatPosition> = beatEngine.beatPosition
    val currentGenre: StateFlow<Genre> = beatEngine.currentGenre
    val bpm: StateFlow<Int> = beatEngine.bpm
    val isMuted: StateFlow<Boolean> = beatEngine.isMuted

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

    // Rhyme Dictionary Lookup State
    private val _dictionaryQuery = MutableStateFlow("")
    val dictionaryQuery: StateFlow<String> = _dictionaryQuery.asStateFlow()

    private val _selectedRhymeType = MutableStateFlow(RhymeType.ALL)
    val selectedRhymeType: StateFlow<RhymeType> = _selectedRhymeType.asStateFlow()

    private val _rhymeResults = MutableStateFlow<List<RhymeEntry>>(emptyList())
    val rhymeResults: StateFlow<List<RhymeEntry>> = _rhymeResults.asStateFlow()

    // Room Saved Drafts
    val savedDrafts: StateFlow<List<SongDraft>> = songDao.getAllDrafts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Snack bar / User notice
    private val _userNotice = MutableStateFlow<String?>(null)
    val userNotice: StateFlow<String?> = _userNotice.asStateFlow()

    init {
        refreshRhymeDictionary()
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

    fun generateLyrics() {
        val phrase = _targetPhrase.value.trim()
        if (phrase.isBlank()) {
            _userNotice.value = "Please enter a target word or phrase first."
            return
        }

        viewModelScope.launch {
            _lyricState.value = LyricUiState.Loading
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

    fun saveCurrentSongDraft() {
        val currentState = _lyricState.value
        if (currentState is LyricUiState.Success) {
            viewModelScope.launch {
                val result = currentState.result
                val fullLyrics = buildString {
                    appendLine("## ${result.title}")
                    appendLine("Genre: ${beatEngine.currentGenre.value.displayName} | Tempo: ${beatEngine.bpm.value} BPM")
                    appendLine("Word/Phrase: ${_targetPhrase.value} (${_integrationMode.value.displayName})")
                    appendLine()
                    for (b in result.bars) {
                        appendLine("[Bar ${b.barNumber} | ${b.rhymeSchemeTag}] ${b.text}")
                    }
                    if (result.culturalTermsUsed.isNotEmpty()) {
                        appendLine("\nSlang Used: ${result.culturalTermsUsed.joinToString(", ")}")
                    }
                }

                val draft = SongDraft(
                    title = result.title,
                    genreName = beatEngine.currentGenre.value.name,
                    tempoBpm = beatEngine.bpm.value,
                    targetPhrase = _targetPhrase.value,
                    integrationMode = _integrationMode.value.name,
                    lyricsContent = fullLyrics,
                    rhymeNotes = result.integrationExplanation
                )

                songDao.insertDraft(draft)
                _userNotice.value = "Saved '${result.title}' to Songbook!"
            }
        } else {
            _userNotice.value = "Generate lyrics first before saving to Songbook."
        }
    }

    fun deleteDraft(draft: SongDraft) {
        viewModelScope.launch {
            songDao.deleteDraft(draft)
            _userNotice.value = "Deleted '${draft.title}'"
        }
    }

    fun loadDraftIntoStudio(draft: SongDraft) {
        try {
            val genre = Genre.valueOf(draft.genreName)
            selectGenre(genre)
        } catch (_: Exception) {}
        setBpm(draft.tempoBpm)
        setTargetPhrase(draft.targetPhrase)
        try {
            val mode = IntegrationMode.valueOf(draft.integrationMode)
            setIntegrationMode(mode)
        } catch (_: Exception) {}
        _userNotice.value = "Loaded settings for '${draft.title}'"
    }

    fun clearUserNotice() {
        _userNotice.value = null
    }

    override fun onCleared() {
        super.onCleared()
        beatEngine.stop()
    }
}
