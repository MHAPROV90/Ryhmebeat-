package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.LyricUiState
import com.example.ui.RhymeBeatViewModel
import com.example.ui.components.BeatControllerCard
import com.example.ui.components.BeatKeyboardCard
import com.example.ui.components.LyricEditorCard
import com.example.ui.components.LyricPromptCard
import com.example.ui.components.LyricsDisplayCard
import com.example.ui.components.RhymeSuggestionBottomSheet
import com.example.ui.components.RhymeSuggestionSidebar
import com.example.ui.components.SaveProjectDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen(
    viewModel: RhymeBeatViewModel,
    modifier: Modifier = Modifier
) {
    val currentGenre by viewModel.currentGenre.collectAsState()
    val bpm by viewModel.bpm.collectAsState()
    val beatPosition by viewModel.beatPosition.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()

    val targetPhrase by viewModel.targetPhrase.collectAsState()
    val integrationMode by viewModel.integrationMode.collectAsState()
    val songSection by viewModel.songSection.collectAsState()
    val lyricState by viewModel.lyricState.collectAsState()

    val editorText by viewModel.editorText.collectAsState()
    val lastWordTyped by viewModel.lastWordTyped.collectAsState()
    val isRhymeSheetOpen by viewModel.isRhymeSheetOpen.collectAsState()
    val editorRhymeTypeFilter by viewModel.editorRhymeTypeFilter.collectAsState()
    val realtimeRhymeSuggestions by viewModel.realtimeRhymeSuggestions.collectAsState()

    val activeDraftTitle by viewModel.activeDraftTitle.collectAsState()
    val isSaveProjectDialogOpen by viewModel.isSaveProjectDialogOpen.collectAsState()

    val synthInstrument by viewModel.synthInstrument.collectAsState()
    val synthOctave by viewModel.synthOctave.collectAsState()
    val lastPlayedNote by viewModel.lastPlayedNote.collectAsState()
    val lastTriggeredDrum by viewModel.lastTriggeredDrum.collectAsState()
    val isGeneratedLyricsSaved by viewModel.isGeneratedLyricsSaved.collectAsState()

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 720.dp

        if (isWideScreen) {
            // Adaptive Two-Column Layout with Permanent Sidebar on Wide Screens
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main Studio Controls, Beat Machine, Synth Keyboard, and Lyric Editor (Left 60-65%)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BeatControllerCard(
                        currentGenre = currentGenre,
                        bpm = bpm,
                        beatPosition = beatPosition,
                        isMuted = isMuted,
                        onGenreSelected = viewModel::selectGenre,
                        onBpmChanged = viewModel::setBpm,
                        onTapTempo = viewModel::tapTempo,
                        onTogglePlay = viewModel::toggleBeat,
                        onToggleMute = viewModel::toggleMute
                    )

                    BeatKeyboardCard(
                        currentInstrument = synthInstrument,
                        currentOctave = synthOctave,
                        lastPlayedNote = lastPlayedNote,
                        lastTriggeredDrum = lastTriggeredDrum,
                        onPlayNote = viewModel::playKeyboardNote,
                        onPlayDrum = viewModel::playDrumPad,
                        onSelectInstrument = viewModel::setSynthInstrument,
                        onOctaveUp = viewModel::octaveUp,
                        onOctaveDown = viewModel::octaveDown,
                        isBeatPlaying = beatPosition.isPlaying,
                        onToggleBeatPlay = viewModel::toggleBeat
                    )

                    LyricEditorCard(
                        editorText = editorText,
                        lastWord = lastWordTyped,
                        selectedGenre = currentGenre,
                        topRhymes = realtimeRhymeSuggestions,
                        hasAiLyrics = lyricState is LyricUiState.Success,
                        activeDraftTitle = activeDraftTitle,
                        onEditorTextChanged = viewModel::updateEditorText,
                        onOpenRhymeSheet = viewModel::openRhymeSheet,
                        onInsertRhyme = viewModel::insertRhymeIntoEditor,
                        onImportAiLyrics = viewModel::importAiLyricsToEditor,
                        onSaveDraft = viewModel::openSaveProjectDialog,
                        onNewProjectClick = viewModel::startNewProject
                    )

                    LyricPromptCard(
                        targetPhrase = targetPhrase,
                        integrationMode = integrationMode,
                        songSection = songSection,
                        genre = currentGenre,
                        lyricState = lyricState,
                        onTargetPhraseChanged = viewModel::setTargetPhrase,
                        onIntegrationModeChanged = viewModel::setIntegrationMode,
                        onSongSectionChanged = viewModel::setSongSection,
                        onGenerateClicked = viewModel::generateLyrics
                    )

                    LyricsDisplayCard(
                        lyricState = lyricState,
                        beatPosition = beatPosition,
                        isSavedToDevice = isGeneratedLyricsSaved,
                        onSaveToSongbook = { viewModel.saveCurrentSongDraft() },
                        onRetry = viewModel::generateLyrics,
                        onSendToEditor = viewModel::importAiLyricsToEditor
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Dedicated Rhyme Suggestion Sidebar (Right Column)
                RhymeSuggestionSidebar(
                    lastWord = lastWordTyped,
                    selectedGenre = currentGenre,
                    rhymes = realtimeRhymeSuggestions,
                    activeFilter = editorRhymeTypeFilter,
                    onFilterChanged = viewModel::setEditorRhymeTypeFilter,
                    onGenreChanged = viewModel::selectGenre,
                    onInsertRhyme = viewModel::insertRhymeIntoEditor,
                    modifier = Modifier.width(340.dp)
                )
            }
        } else {
            // Standard Single-Column Layout with Bottom Sheet on Compact Screens
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Procedural Beat Machine & Tempo Visualizer
                BeatControllerCard(
                    currentGenre = currentGenre,
                    bpm = bpm,
                    beatPosition = beatPosition,
                    isMuted = isMuted,
                    onGenreSelected = viewModel::selectGenre,
                    onBpmChanged = viewModel::setBpm,
                    onTapTempo = viewModel::tapTempo,
                    onTogglePlay = viewModel::toggleBeat,
                    onToggleMute = viewModel::toggleMute
                )

                // 2. Interactive Synthesizer Keyboard & MPC Drum Pads
                BeatKeyboardCard(
                    currentInstrument = synthInstrument,
                    currentOctave = synthOctave,
                    lastPlayedNote = lastPlayedNote,
                    lastTriggeredDrum = lastTriggeredDrum,
                    onPlayNote = viewModel::playKeyboardNote,
                    onPlayDrum = viewModel::playDrumPad,
                    onSelectInstrument = viewModel::setSynthInstrument,
                    onOctaveUp = viewModel::octaveUp,
                    onOctaveDown = viewModel::octaveDown,
                    isBeatPlaying = beatPosition.isPlaying,
                    onToggleBeatPlay = viewModel::toggleBeat
                )

                // 3. Real-Time Lyric Editor with Live Last-Word Rhyme Ticker & Save Actions
                LyricEditorCard(
                    editorText = editorText,
                    lastWord = lastWordTyped,
                    selectedGenre = currentGenre,
                    topRhymes = realtimeRhymeSuggestions,
                    hasAiLyrics = lyricState is LyricUiState.Success,
                    activeDraftTitle = activeDraftTitle,
                    onEditorTextChanged = viewModel::updateEditorText,
                    onOpenRhymeSheet = viewModel::openRhymeSheet,
                    onInsertRhyme = viewModel::insertRhymeIntoEditor,
                    onImportAiLyrics = viewModel::importAiLyricsToEditor,
                    onSaveDraft = viewModel::openSaveProjectDialog,
                    onNewProjectClick = viewModel::startNewProject
                )

                // 4. AI Prompt & Cadence Integration Configuration
                LyricPromptCard(
                    targetPhrase = targetPhrase,
                    integrationMode = integrationMode,
                    songSection = songSection,
                    genre = currentGenre,
                    lyricState = lyricState,
                    onTargetPhraseChanged = viewModel::setTargetPhrase,
                    onIntegrationModeChanged = viewModel::setIntegrationMode,
                    onSongSectionChanged = viewModel::setSongSection,
                    onGenerateClicked = viewModel::generateLyrics
                )

                // 5. Lyrics Output & Real-Time Beat Sync Display (Room DB Saved State)
                LyricsDisplayCard(
                    lyricState = lyricState,
                    beatPosition = beatPosition,
                    isSavedToDevice = isGeneratedLyricsSaved,
                    onSaveToSongbook = { viewModel.saveCurrentSongDraft() },
                    onRetry = viewModel::generateLyrics,
                    onSendToEditor = viewModel::importAiLyricsToEditor
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Real-Time Rhyme Suggestions Bottom Sheet
            RhymeSuggestionBottomSheet(
                isOpen = isRhymeSheetOpen,
                onDismissRequest = viewModel::closeRhymeSheet,
                lastWord = lastWordTyped,
                selectedGenre = currentGenre,
                rhymes = realtimeRhymeSuggestions,
                activeFilter = editorRhymeTypeFilter,
                onFilterChanged = viewModel::setEditorRhymeTypeFilter,
                onGenreChanged = viewModel::selectGenre,
                onInsertRhyme = { word ->
                    viewModel.insertRhymeIntoEditor(word)
                }
            )
        }

        // Save Project Dialog (Room persistence)
        SaveProjectDialog(
            isOpen = isSaveProjectDialogOpen,
            onDismissRequest = viewModel::closeSaveProjectDialog,
            existingTitle = activeDraftTitle,
            isExistingProject = activeDraftTitle != null,
            currentGenre = currentGenre,
            bpm = bpm,
            lyricsPreview = editorText,
            onSaveProject = { title, saveAsNew ->
                viewModel.saveUserWrittenLyrics(title, saveAsNew)
            }
        )
    }
}
