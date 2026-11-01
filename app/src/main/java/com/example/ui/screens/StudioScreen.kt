package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.RhymeBeatViewModel
import com.example.ui.components.BeatControllerCard
import com.example.ui.components.LyricPromptCard
import com.example.ui.components.LyricsDisplayCard

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

    Column(
        modifier = modifier
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

        // 2. Prompt & Integration Configuration
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

        // 3. Lyrics Output & Real-Time Beat Sync Display
        LyricsDisplayCard(
            lyricState = lyricState,
            beatPosition = beatPosition,
            onSaveToSongbook = viewModel::saveCurrentSongDraft,
            onRetry = viewModel::generateLyrics
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
