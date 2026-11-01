package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.GeneratedBar
import com.example.ai.GeneratedLyricResult
import com.example.audio.BeatPosition
import com.example.ui.LyricUiState
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun LyricsDisplayCard(
    lyricState: LyricUiState,
    beatPosition: BeatPosition,
    onSaveToSongbook: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("lyrics_display_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StudioBorder))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when (lyricState) {
                is LyricUiState.Idle -> {
                    // Empty / Prompting State
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = StudioCardElevated,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "READY FOR THE MIC",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Set your beat tempo above, enter a target phrase, and generate rhythm-locked lyrics.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                            modifier = Modifier.padding(horizontal = 24.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                is LyricUiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = StudioCardElevated,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = ElectricMagenta,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "CALCULATING CADENCE & RHYMES...",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Syncing syllables to tempo and weaving your target words into bars...",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
                is LyricUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            tint = ElectricMagenta,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Generation Error",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = lyricState.message,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = onRetry,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Try Again")
                        }
                    }
                }
                is LyricUiState.Success -> {
                    val result = lyricState.result
                    val totalBars = result.bars.size
                    // Active bar based on beat engine playing
                    val activeBarIndex = if (beatPosition.isPlaying && totalBars > 0) {
                        (beatPosition.bar - 1) % totalBars
                    } else -1

                    // Title & Actions Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = result.title,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                            )
                            if (result.tempoNotes.isNotBlank()) {
                                Text(
                                    text = result.tempoNotes,
                                    style = MaterialTheme.typography.labelSmall.copy(color = NeonCyan)
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Copy Action
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = StudioCardElevated,
                                modifier = Modifier
                                    .size(38.dp)
                                    .border(1.dp, StudioBorder, RoundedCornerShape(10.dp)),
                                onClick = {
                                    val fullLyrics = buildString {
                                        appendLine(result.title)
                                        for (b in result.bars) {
                                            appendLine("[Bar ${b.barNumber} | ${b.rhymeSchemeTag}] ${b.text}")
                                        }
                                    }
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("RhymeBeat Lyrics", fullLyrics))
                                    Toast.makeText(context, "Lyrics copied to clipboard!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Lyrics",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Save to Songbook Action
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = ElectricMagenta.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .size(38.dp)
                                    .border(1.dp, ElectricMagenta, RoundedCornerShape(10.dp)),
                                onClick = onSaveToSongbook
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkAdd,
                                        contentDescription = "Save to Songbook",
                                        tint = ElectricMagenta,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Integration Analysis Banner
                    if (result.integrationExplanation.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = StudioCardElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🎯 Integration: ${result.integrationExplanation}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.5.sp,
                                    color = TextSecondary
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bars List
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        result.bars.forEachIndexed { index, bar ->
                            BarLineRow(
                                bar = bar,
                                isActive = index == activeBarIndex
                            )
                        }
                    }

                    // Cultural Slang Used Chips
                    if (result.culturalTermsUsed.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Cultural Slang & Idioms Used:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            result.culturalTermsUsed.forEach { slang ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = ElectricMagenta.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, ElectricMagenta.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = slang,
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Suggested Rhyme Follow-ups
                    if (result.suggestedRhymeFollowUps.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Rhyme ideas for the next verse:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            result.suggestedRhymeFollowUps.forEach { word ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = NeonCyan.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = word,
                                        style = MaterialTheme.typography.labelSmall.copy(color = NeonCyan),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BarLineRow(
    bar: GeneratedBar,
    isActive: Boolean
) {
    val bgColor by animateColorAsState(
        targetValue = if (isActive) ElectricMagenta.copy(alpha = 0.22f) else StudioCardElevated,
        label = "bar_bg_${bar.barNumber}"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isActive) ElectricMagenta else StudioBorder,
        label = "bar_border_${bar.barNumber}"
    )

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(if (isActive) 1.5.dp else 1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bar Number + Rhyme Tag
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isActive) ElectricMagenta else StudioBorder
                    ) {
                        Text(
                            text = "BAR ${String.format("%02d", bar.barNumber)}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NeonCyan.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "[${bar.rhymeSchemeTag}]",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Syllable Count Badge
                Text(
                    text = "${bar.syllableCount} syl",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = TextTertiary
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Main Lyric Line
            Text(
                text = bar.text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                    color = if (isActive) Color.White else TextPrimary,
                    lineHeight = 22.sp
                )
            )

            // Delivery Tip
            if (bar.deliveryTip.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🎙️ ${bar.deliveryTip}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isActive) NeonCyan else TextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
