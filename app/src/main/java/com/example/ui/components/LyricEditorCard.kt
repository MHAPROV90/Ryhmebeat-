package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.Genre
import com.example.data.GenreRhymeDictionary
import com.example.data.RhymeEntry
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun LyricEditorCard(
    editorText: String,
    lastWord: String,
    selectedGenre: Genre,
    topRhymes: List<RhymeEntry>,
    hasAiLyrics: Boolean,
    activeDraftTitle: String? = null,
    onEditorTextChanged: (String) -> Unit,
    onOpenRhymeSheet: () -> Unit,
    onInsertRhyme: (String) -> Unit,
    onImportAiLyrics: () -> Unit,
    onSaveDraft: () -> Unit,
    onNewProjectClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val currentLine = editorText.lines().lastOrNull { it.isNotBlank() } ?: ""
    val lineSyllables = if (currentLine.isNotBlank()) GenreRhymeDictionary.estimateSyllables(currentLine) else 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("lyric_editor_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StudioBorder))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Lyric Editor",
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LIVE LYRIC WRITER",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp,
                            color = NeonCyan
                        )
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Copy button
                    IconButton(
                        onClick = {
                            if (editorText.isNotBlank()) {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Lyrics", editorText))
                                Toast.makeText(context, "Lyrics copied!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy lyrics",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Save Project Draft Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ElectricMagenta.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, ElectricMagenta),
                        onClick = onSaveDraft,
                        modifier = Modifier.testTag("save_draft_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Save draft",
                                tint = ElectricMagenta,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (activeDraftTitle != null) "UPDATE" else "SAVE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = ElectricMagenta,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Clear button
                    if (editorText.isNotEmpty()) {
                        IconButton(
                            onClick = { onEditorTextChanged("") },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear text",
                                tint = TextTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Loaded Project Banner (if editing a past project from history)
            if (activeDraftTitle != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = StudioDarkBg,
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth().testTag("active_project_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LibraryMusic,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Project: $activeDraftTitle",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (onNewProjectClick != null) {
                            Text(
                                text = "New Project +",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .clickable { onNewProjectClick() }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Real-Time Rhyme Ticker & Sheet Opener Pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StudioCardElevated,
                border = BorderStroke(1.dp, if (lastWord.isNotBlank()) NeonCyan.copy(alpha = 0.5f) else StudioBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("realtime_rhyme_ticker")
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(if (lastWord.isNotBlank()) NeonCyan else TextTertiary, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (lastWord.isNotBlank()) {
                                    "Last Word: \"$lastWord\""
                                } else {
                                    "Start typing to see live rhymes"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (lastWord.isNotBlank()) Color.White else TextSecondary
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ElectricMagenta.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = selectedGenre.displayName.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricMagenta
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }

                        // Open Bottom Sheet / Sidebar Button
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NeonCyan.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, NeonCyan),
                            onClick = onOpenRhymeSheet,
                            modifier = Modifier.testTag("open_rhyme_sheet_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoFixHigh,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "RHYME SHEET (${topRhymes.size})",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = NeonCyan,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    // Horizontal Quick-Tap Rhyme Strip
                    if (topRhymes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            topRhymes.take(8).forEach { rhyme ->
                                SuggestionChip(
                                    onClick = { onInsertRhyme(rhyme.wordOrPhrase) },
                                    label = {
                                        Text(
                                            text = rhyme.wordOrPhrase,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
                                        )
                                    },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        labelColor = if (rhyme.genre == selectedGenre) NeonCyan else Color.White
                                    ),
                                    border = SuggestionChipDefaults.suggestionChipBorder(
                                        enabled = true,
                                        borderColor = StudioBorder
                                    ),
                                    modifier = Modifier.testTag("quick_rhyme_${rhyme.wordOrPhrase.lowercase().replace(" ", "_")}")
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Editor Text Area
            OutlinedTextField(
                value = editorText,
                onValueChange = onEditorTextChanged,
                placeholder = {
                    Text(
                        text = "Write your bars here...\nReal-time rhyme suggestions will adapt as you type each word.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextTertiary)
                    )
                },
                minLines = 5,
                maxLines = 10,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lyric_editor_text_field"),
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextPrimary
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = StudioBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = StudioCardElevated,
                    unfocusedContainerColor = StudioCardElevated
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Footer info: Syllables of active line & import AI lyrics button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (lineSyllables > 0) "Current bar: ~$lineSyllables syllables" else "4/4 beat cadence",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = TextTertiary
                    )
                )

                if (hasAiLyrics) {
                    OutlinedButton(
                        onClick = onImportAiLyrics,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricMagenta),
                        border = BorderStroke(1.dp, ElectricMagenta.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("import_ai_lyrics_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Import AI Lyrics",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                        )
                    }
                }
            }
        }
    }
}
