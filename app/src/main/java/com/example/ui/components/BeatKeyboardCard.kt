package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Piano
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.audio.DrumSound
import com.example.audio.KeyboardNote
import com.example.audio.SynthInstrument
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun BeatKeyboardCard(
    currentInstrument: SynthInstrument,
    currentOctave: Int,
    lastPlayedNote: String?,
    lastTriggeredDrum: DrumSound?,
    onPlayNote: (KeyboardNote) -> Unit,
    onPlayDrum: (DrumSound) -> Unit,
    onSelectInstrument: (SynthInstrument) -> Unit,
    onOctaveUp: () -> Unit,
    onOctaveDown: () -> Unit,
    isBeatPlaying: Boolean,
    onToggleBeatPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Piano Keyboard, 1 = Drum Pads

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("beat_keyboard_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StudioBorder))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Title, Mode Tabs, Play Beat Quick Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = NeonCyan.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Piano,
                                contentDescription = "Keyboard",
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "BEAT KEYBOARD & PADS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = if (selectedTab == 0) "Play melody hooks & 808 bass in key" else "Tap MPC drum pads over the rhythm",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                        )
                    }
                }

                // Jam Over Beat Button
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isBeatPlaying) NeonCyan.copy(alpha = 0.2f) else StudioCardElevated,
                    border = BorderStroke(1.dp, if (isBeatPlaying) NeonCyan else StudioBorder),
                    modifier = Modifier
                        .clickable { onToggleBeatPlay() }
                        .testTag("keyboard_quick_beat_toggle")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = if (isBeatPlaying) NeonCyan else TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBeatPlaying) "BEAT ON" else "START BEAT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isBeatPlaying) NeonCyan else TextSecondary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Selector: Piano Keys vs Drum Pads
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = StudioCardElevated,
                contentColor = TextPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .height(40.dp),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NeonCyan,
                        height = 3.dp
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Piano, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Melody Keys", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium)
                        }
                    },
                    modifier = Modifier.testTag("tab_melody_keys")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Drum Pads", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium)
                        }
                    },
                    modifier = Modifier.testTag("tab_drum_pads")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 0) {
                // Instrument & Octave Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Instrument Selector Chips
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SynthInstrument.entries.forEach { inst ->
                            val isSelected = inst == currentInstrument
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectInstrument(inst) },
                                label = { Text(inst.displayName, style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricMagenta.copy(alpha = 0.25f),
                                    selectedLabelColor = ElectricMagenta,
                                    containerColor = StudioCardElevated,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = StudioBorder,
                                    selectedBorderColor = ElectricMagenta
                                ),
                                modifier = Modifier.testTag("instrument_chip_${inst.name.lowercase()}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Octave Stepper
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(StudioCardElevated, RoundedCornerShape(10.dp))
                            .border(1.dp, StudioBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        IconButton(
                            onClick = onOctaveDown,
                            enabled = currentOctave > 2,
                            modifier = Modifier.size(28.dp).testTag("octave_down_button")
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Octave Down", tint = if (currentOctave > 2) TextPrimary else TextTertiary)
                        }

                        Text(
                            text = "OCT $currentOctave",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = NeonCyan
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        IconButton(
                            onClick = onOctaveUp,
                            enabled = currentOctave < 6,
                            modifier = Modifier.size(28.dp).testTag("octave_up_button")
                        ) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Octave Up", tint = if (currentOctave < 6) TextPrimary else TextTertiary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Realistic Piano Keyboard Layout
                PianoKeyboard(
                    currentOctave = currentOctave,
                    lastPlayedNote = lastPlayedNote,
                    onPlayNote = onPlayNote
                )

            } else {
                // MPC-Style Drum Pads Grid
                DrumPadGrid(
                    lastTriggeredDrum = lastTriggeredDrum,
                    onPlayDrum = onPlayDrum
                )
            }
        }
    }
}

@Composable
fun PianoKeyboard(
    currentOctave: Int,
    lastPlayedNote: String?,
    onPlayNote: (KeyboardNote) -> Unit
) {
    // 8 White Keys: C, D, E, F, G, A, B, C+
    val whiteKeys = remember { KeyboardNote.WHITE_KEYS }
    // Black Keys: C# (index 0.5), D# (1.5), F# (3.5), G# (4.5), A# (5.5)
    val blackKeyOffsets = remember {
        listOf(
            Pair(KeyboardNote("C#", 1, true), 0.68f),
            Pair(KeyboardNote("D#", 3, true), 1.70f),
            Pair(KeyboardNote("F#", 6, true), 3.68f),
            Pair(KeyboardNote("G#", 8, true), 4.70f),
            Pair(KeyboardNote("A#", 10, true), 5.72f)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .background(Color(0xFF10121A), RoundedCornerShape(12.dp))
            .border(1.5.dp, StudioBorder, RoundedCornerShape(12.dp))
            .padding(4.dp)
            .testTag("piano_keyboard_container")
    ) {
        // White Keys Row
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            whiteKeys.forEach { note ->
                val noteId = "${note.name}${if (note.name == "C+") currentOctave + 1 else currentOctave}"
                val isRecentlyPlayed = lastPlayedNote == noteId

                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()

                LaunchedEffect(isPressed) {
                    if (isPressed) onPlayNote(note)
                }

                val keyColor by animateColorAsState(
                    targetValue = when {
                        isPressed -> NeonCyan.copy(alpha = 0.7f)
                        isRecentlyPlayed -> NeonCyan.copy(alpha = 0.35f)
                        else -> Color(0xFFF0F4F8)
                    },
                    animationSpec = spring(),
                    label = "white_key_$noteId"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp, topStart = 4.dp, topEnd = 4.dp))
                        .background(keyColor)
                        .border(
                            1.dp,
                            if (isPressed) NeonCyan else Color(0xFFB0BEC5),
                            RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp, topStart = 4.dp, topEnd = 4.dp)
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            onPlayNote(note)
                        }
                        .testTag("piano_key_${note.name.lowercase().replace("+", "_high")}"),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        text = note.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = if (isPressed) Color.White else Color(0xFF263238)
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }

        // Black Keys Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(10f)
        ) {
            // Note: BoxWithConstraints allows proportional positioning
            androidx.compose.foundation.layout.BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val whiteKeyWidth = maxWidth / 8

                blackKeyOffsets.forEach { (note, relativeOffset) ->
                    val noteId = "${note.name}$currentOctave"
                    val isRecentlyPlayed = lastPlayedNote == noteId

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()

                    LaunchedEffect(isPressed) {
                        if (isPressed) onPlayNote(note)
                    }

                    val blackKeyColor by animateColorAsState(
                        targetValue = when {
                            isPressed -> ElectricMagenta
                            isRecentlyPlayed -> ElectricMagenta.copy(alpha = 0.6f)
                            else -> Color(0xFF1E2029)
                        },
                        animationSpec = spring(),
                        label = "black_key_$noteId"
                    )

                    Box(
                        modifier = Modifier
                            .offset(x = whiteKeyWidth * relativeOffset)
                            .width(whiteKeyWidth * 0.68f)
                            .height(105.dp)
                            .clip(RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
                            .background(blackKeyColor)
                            .border(1.dp, if (isPressed) ElectricMagenta else Color(0xFF37474F), RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                onPlayNote(note)
                            }
                            .testTag("piano_key_${note.name.lowercase().replace("#", "_sharp")}"),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            text = note.name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = if (isPressed) Color.White else NeonCyan
                            ),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DrumPadGrid(
    lastTriggeredDrum: DrumSound?,
    onPlayDrum: (DrumSound) -> Unit
) {
    val drumPads = remember {
        listOf(
            Pair(DrumSound.KICK, "KICK\nPUNCH"),
            Pair(DrumSound.SNARE, "SNARE\nCRISP"),
            Pair(DrumSound.CLAP, "CLAP\nSNAP"),
            Pair(DrumSound.HI_HAT_CLOSED, "CLOSED\nHAT"),
            Pair(DrumSound.HI_HAT_OPEN, "OPEN\nHAT"),
            Pair(DrumSound.SUB_808, "808\nBOOM"),
            Pair(DrumSound.TOM, "LOW\nTOM"),
            Pair(DrumSound.RIMSHOT, "RIM\nSHOT")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("drum_pad_grid"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 4x2 Grid (2 rows of 4 pads)
        for (rowIndex in 0..1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (colIndex in 0..3) {
                    val padIndex = rowIndex * 4 + colIndex
                    val (sound, label) = drumPads[padIndex]
                    val isTriggered = lastTriggeredDrum == sound

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()

                    LaunchedEffect(isPressed) {
                        if (isPressed) onPlayDrum(sound)
                    }

                    val padColor by animateColorAsState(
                        targetValue = when {
                            isPressed -> when (sound) {
                                DrumSound.KICK, DrumSound.SUB_808 -> NeonCyan
                                DrumSound.SNARE, DrumSound.CLAP -> ElectricMagenta
                                else -> Color(0xFFFFB703)
                            }
                            isTriggered -> StudioCardElevated.copy(alpha = 0.9f)
                            else -> StudioCardElevated
                        },
                        animationSpec = spring(),
                        label = "pad_color_${sound.name}"
                    )

                    val borderColor by animateColorAsState(
                        targetValue = when {
                            isPressed -> Color.White
                            sound == DrumSound.KICK || sound == DrumSound.SUB_808 -> NeonCyan.copy(alpha = 0.6f)
                            sound == DrumSound.SNARE || sound == DrumSound.CLAP -> ElectricMagenta.copy(alpha = 0.6f)
                            else -> Color(0xFFFFB703).copy(alpha = 0.6f)
                        },
                        label = "pad_border_${sound.name}"
                    )

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(68.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                onPlayDrum(sound)
                            }
                            .testTag("drum_pad_${sound.name.lowercase()}"),
                        shape = RoundedCornerShape(12.dp),
                        color = padColor
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize().padding(4.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center,
                                    color = if (isPressed) Color.Black else TextPrimary,
                                    lineHeight = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
