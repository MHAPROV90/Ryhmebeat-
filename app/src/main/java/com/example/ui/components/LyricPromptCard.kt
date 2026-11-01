package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.IntegrationMode
import com.example.ai.SongSection
import com.example.audio.Genre
import com.example.ui.LyricUiState
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun LyricPromptCard(
    targetPhrase: String,
    integrationMode: IntegrationMode,
    songSection: SongSection,
    genre: Genre,
    lyricState: LyricUiState,
    onTargetPhraseChanged: (String) -> Unit,
    onIntegrationModeChanged: (IntegrationMode) -> Unit,
    onSongSectionChanged: (SongSection) -> Unit,
    onGenerateClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("lyric_prompt_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StudioBorder))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = "Lyric Generator",
                        tint = ElectricMagenta,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WORD & RHYME INTEGRATION",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = ElectricMagenta
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Target Word/Phrase Input
            Text(
                text = "Target Word or Phrase",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = targetPhrase,
                onValueChange = onTargetPhraseChanged,
                placeholder = { Text("e.g. Neon Skyline, Gravel Road, Drop 16...") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("target_phrase_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = StudioBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = StudioCardElevated,
                    unfocusedContainerColor = StudioCardElevated
                )
            )

            // Quick Phrase Inspiration Chips
            Spacer(modifier = Modifier.height(6.dp))
            val quickSuggestions = when (genre) {
                Genre.HIP_HOP -> listOf("Drop 16", "Keep it 100", "Secure the Bag", "No Cap")
                Genre.TRAP -> listOf("Double Cup", "Rollie", "Spin the Block", "808")
                Genre.POP -> listOf("Midnight Drive", "Golden Hour", "Electric Night", "Glow Up")
                Genre.COUNTRY -> listOf("Tailgate", "Backroads", "Front Porch", "Whiskey River")
                Genre.RNB -> listOf("Slow Jam", "Midnight Call", "Soul Vibration", "Tender Touch")
                Genre.LO_FI -> listOf("Foggy Window", "Vinyl Crackle", "Daydreaming", "Warm Coffee")
                Genre.ROCK -> listOf("Overdrive", "Rebel Yell", "Power Chord", "Stage Dive")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Suggestions",
                    tint = TextTertiary,
                    modifier = Modifier.size(16.dp)
                )
                quickSuggestions.forEach { suggestion ->
                    SuggestionChip(
                        onClick = { onTargetPhraseChanged(suggestion) },
                        label = { Text(suggestion, style = MaterialTheme.typography.labelSmall) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = StudioCardElevated,
                            labelColor = TextSecondary
                        ),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            enabled = true,
                            borderColor = StudioBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Integration Mode Selector
            Text(
                text = "Rhyme Integration Mode",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                IntegrationMode.entries.forEach { mode ->
                    val isSelected = mode == integrationMode
                    Surface(
                        onClick = { onIntegrationModeChanged(mode) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) ElectricMagenta.copy(alpha = 0.15f) else StudioCardElevated,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) ElectricMagenta else StudioBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("integration_mode_${mode.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mode.displayName,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = mode.promptInstruction,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isSelected) Color.White.copy(alpha = 0.85f) else TextSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Song Section Selector Chips
            Text(
                text = "Structure & Length",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SongSection.entries.forEach { section ->
                    val isSelected = section == songSection
                    Surface(
                        onClick = { onSongSectionChanged(section) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else StudioCardElevated,
                        border = BorderStroke(1.dp, if (isSelected) NeonCyan else StudioBorder),
                        modifier = Modifier.testTag("section_${section.name.lowercase()}")
                    ) {
                        Text(
                            text = section.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) NeonCyan else TextPrimary
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Generate Button
            val isLoading = lyricState is LyricUiState.Loading
            Button(
                onClick = onGenerateClicked,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricMagenta,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("generate_lyrics_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CRAFTING LYRICS & RHYMES...",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Generate",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GENERATE ${genre.displayName.uppercase()} LYRICS",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
