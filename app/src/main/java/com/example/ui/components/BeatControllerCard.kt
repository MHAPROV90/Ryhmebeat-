package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.BeatPosition
import com.example.audio.Genre
import com.example.ui.theme.BeatDownbeat
import com.example.ui.theme.BeatLedActive
import com.example.ui.theme.BeatLedInactive
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun BeatControllerCard(
    currentGenre: Genre,
    bpm: Int,
    beatPosition: BeatPosition,
    isMuted: Boolean,
    onGenreSelected: (Genre) -> Unit,
    onBpmChanged: (Int) -> Unit,
    onTapTempo: () -> Unit,
    onTogglePlay: () -> Unit,
    onToggleMute: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("beat_controller_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StudioBorder))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title & Mute toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Beat Engine",
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RHYTHM ENGINE",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = NeonCyan
                        )
                    )
                }

                IconButton(
                    onClick = onToggleMute,
                    modifier = Modifier.testTag("toggle_mute_button")
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = if (isMuted) "Unmute Audio" else "Mute Audio",
                        tint = if (isMuted) TextTertiary else NeonCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Genre selector chips
            Text(
                text = "Musical Genre",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Genre.entries.forEach { genre ->
                    val isSelected = genre == currentGenre
                    FilterChip(
                        selected = isSelected,
                        onClick = { onGenreSelected(genre) },
                        label = {
                            Text(
                                text = genre.displayName,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricMagenta,
                            selectedLabelColor = Color.White,
                            containerColor = StudioCardElevated,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) ElectricMagenta else StudioBorder
                        ),
                        modifier = Modifier.testTag("genre_chip_${genre.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Real-Time Beat LEDs & Bar Meter Display
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = StudioCardElevated
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Bar & Beat Text
                    Column {
                        Text(
                            text = if (beatPosition.isPlaying) {
                                "BAR ${String.format("%02d", beatPosition.bar)}"
                            } else {
                                "STANDBY"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = if (beatPosition.isPlaying) "Beat ${beatPosition.beat} of 4" else "${currentGenre.defaultBpm} BPM suggested",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }

                    // 4-Beat Meter LEDs
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (b in 1..4) {
                            val isBeatActive = beatPosition.isPlaying && beatPosition.beat == b
                            val ledColor by animateColorAsState(
                                targetValue = when {
                                    !isBeatActive -> BeatLedInactive
                                    b == 1 -> BeatDownbeat // Pink/Red on Beat 1 downbeat
                                    else -> BeatLedActive // Cyan on Beats 2, 3, 4
                                },
                                animationSpec = spring(),
                                label = "beat_led_$b"
                            )

                            Box(
                                modifier = Modifier
                                    .size(if (b == 1) 18.dp else 14.dp)
                                    .clip(CircleShape)
                                    .background(ledColor)
                                    .border(
                                        width = 1.dp,
                                        color = if (isBeatActive) Color.White.copy(alpha = 0.8f) else StudioBorder,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tempo Controls: BPM Display, Slider, Tap Tempo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$bpm",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = NeonCyan
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "BPM",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Tap Tempo Button
                    Button(
                        onClick = onTapTempo,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StudioCardElevated,
                            contentColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("tap_tempo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = "Tap Tempo",
                            modifier = Modifier.size(16.dp),
                            tint = NeonCyan
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "TAP", style = MaterialTheme.typography.labelSmall)
                    }

                    // Minus 2 BPM
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = StudioCardElevated,
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { onBpmChanged(bpm - 2) },
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("-2", color = TextPrimary, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    // Plus 2 BPM
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = StudioCardElevated,
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { onBpmChanged(bpm + 2) },
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("+2", color = TextPrimary, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            Slider(
                value = bpm.toFloat(),
                onValueChange = { onBpmChanged(it.toInt()) },
                valueRange = 50f..190f,
                colors = SliderDefaults.colors(
                    thumbColor = NeonCyan,
                    activeTrackColor = NeonCyan,
                    inactiveTrackColor = StudioCardElevated
                ),
                modifier = Modifier.testTag("tempo_slider")
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Play / Pause Audio Beat Button
            Button(
                onClick = onTogglePlay,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (beatPosition.isPlaying) ElectricMagenta else NeonCyan,
                    contentColor = if (beatPosition.isPlaying) Color.White else Color(0xFF00382E)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("toggle_beat_button")
            ) {
                Icon(
                    imageVector = if (beatPosition.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (beatPosition.isPlaying) "Pause Beat" else "Play Beat"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (beatPosition.isPlaying) "PAUSE BEAT" else "PLAY ${currentGenre.displayName.uppercase()} BEAT",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
