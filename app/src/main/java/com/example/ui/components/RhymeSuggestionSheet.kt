package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.Genre
import com.example.data.RhymeEntry
import com.example.data.RhymeType
import com.example.ui.theme.BeatDownbeat
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCardBg
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

/**
 * Modal Bottom Sheet for real-time rhyme suggestions on mobile screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RhymeSuggestionBottomSheet(
    isOpen: Boolean,
    onDismissRequest: () -> Unit,
    lastWord: String,
    selectedGenre: Genre,
    rhymes: List<RhymeEntry>,
    activeFilter: RhymeType,
    onFilterChanged: (RhymeType) -> Unit,
    onGenreChanged: (Genre) -> Unit,
    onInsertRhyme: (String) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
    modifier: Modifier = Modifier
) {
    if (isOpen) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            containerColor = StudioCardBg,
            scrimColor = Color.Black.copy(alpha = 0.65f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .width(48.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.5f))
                )
            },
            modifier = modifier.testTag("rhyme_suggestions_bottom_sheet")
        ) {
            RhymeSuggestionContent(
                lastWord = lastWord,
                selectedGenre = selectedGenre,
                rhymes = rhymes,
                activeFilter = activeFilter,
                onFilterChanged = onFilterChanged,
                onGenreChanged = onGenreChanged,
                onInsertRhyme = { word ->
                    onInsertRhyme(word)
                },
                onClose = onDismissRequest,
                isSidebar = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            )
        }
    }
}

/**
 * Side-by-side Sidebar for large screens (tablets, foldables, or wide windows).
 */
@Composable
fun RhymeSuggestionSidebar(
    lastWord: String,
    selectedGenre: Genre,
    rhymes: List<RhymeEntry>,
    activeFilter: RhymeType,
    onFilterChanged: (RhymeType) -> Unit,
    onGenreChanged: (Genre) -> Unit,
    onInsertRhyme: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxHeight()
            .testTag("rhyme_suggestions_sidebar"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = StudioCardBg),
        border = BorderStroke(1.dp, StudioBorder)
    ) {
        RhymeSuggestionContent(
            lastWord = lastWord,
            selectedGenre = selectedGenre,
            rhymes = rhymes,
            activeFilter = activeFilter,
            onFilterChanged = onFilterChanged,
            onGenreChanged = onGenreChanged,
            onInsertRhyme = onInsertRhyme,
            onClose = null,
            isSidebar = true,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Shared content layout for both Bottom Sheet and Sidebar.
 */
@Composable
fun RhymeSuggestionContent(
    lastWord: String,
    selectedGenre: Genre,
    rhymes: List<RhymeEntry>,
    activeFilter: RhymeType,
    onFilterChanged: (RhymeType) -> Unit,
    onGenreChanged: (Genre) -> Unit,
    onInsertRhyme: (String) -> Unit,
    onClose: (() -> Unit)?,
    isSidebar: Boolean,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val displayedRhymes = remember(rhymes, searchQuery) {
        if (searchQuery.isBlank()) {
            rhymes
        } else {
            val q = searchQuery.trim().lowercase()
            rhymes.filter {
                it.wordOrPhrase.lowercase().contains(q) ||
                        it.definitionOrContext.lowercase().contains(q)
            }
        }
    }

    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // 1. Header with detected last word & close button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = ElectricMagenta.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, ElectricMagenta),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = null,
                            tint = ElectricMagenta,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "REAL-TIME RHYME ASSISTANT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.1.sp,
                            color = NeonCyan
                        )
                    )
                    Text(
                        text = if (lastWord.isNotBlank()) "Rhymes for \"$lastWord\"" else "Awaiting input...",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (onClose != null) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("close_rhyme_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close sheet",
                        tint = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Active Genre Filter Pill & Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Genre:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
            )
            Genre.entries.forEach { genre ->
                val isSelected = genre == selectedGenre
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) NeonCyan.copy(alpha = 0.18f) else StudioCardElevated,
                    border = BorderStroke(1.dp, if (isSelected) NeonCyan else StudioBorder),
                    modifier = Modifier
                        .clickable { onGenreChanged(genre) }
                        .testTag("filter_genre_${genre.name.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(NeonCyan)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = genre.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) NeonCyan else TextSecondary
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Rhyme Category Chips (All, Exact, Slang, Slant, Multi)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RhymeType.entries.forEach { type ->
                val isSelected = type == activeFilter
                FilterChip(
                    selected = isSelected,
                    onClick = { onFilterChanged(type) },
                    label = {
                        Text(
                            text = type.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricMagenta.copy(alpha = 0.2f),
                        selectedLabelColor = Color.White,
                        containerColor = StudioCardElevated,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = StudioBorder,
                        selectedBorderColor = ElectricMagenta
                    ),
                    modifier = Modifier.testTag("rhyme_type_${type.name.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4. Quick in-sheet filter / search box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search or filter rhymes...", style = MaterialTheme.typography.bodySmall) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = TextTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("sheet_search_input"),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = StudioBorder,
                focusedContainerColor = StudioCardElevated,
                unfocusedContainerColor = StudioCardElevated,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 5. Suggestions Results List
        if (displayedRhymes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (lastWord.isBlank()) "Type a word in the lyric editor" else "No matching rhymes for \"$lastWord\"",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Suggestions update automatically in real-time filtered by ${selectedGenre.displayName}.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            Text(
                text = "${displayedRhymes.size} suggestions for ${selectedGenre.displayName} (tap to insert)",
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .testTag("rhyme_suggestions_list")
            ) {
                items(displayedRhymes, key = { it.wordOrPhrase + it.type.name }) { entry ->
                    RhymeSuggestionRow(
                        entry = entry,
                        onInsert = { onInsertRhyme(entry.wordOrPhrase) }
                    )
                }
            }
        }
    }
}

/**
 * Individual suggestion card with word, category, syllable badge, and quick insert action.
 */
@Composable
fun RhymeSuggestionRow(
    entry: RhymeEntry,
    onInsert: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = when (entry.type) {
        RhymeType.EXACT -> NeonCyan
        RhymeType.SLANG_IDIOM -> ElectricMagenta
        RhymeType.MULTI_SYLLABLE -> Color(0xFFFFB703)
        RhymeType.SLANT -> Color(0xFF90E0EF)
        RhymeType.ALL -> NeonCyan
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = StudioCardElevated,
        border = BorderStroke(1.dp, StudioBorder),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onInsert() }
            .testTag("rhyme_item_${entry.wordOrPhrase.lowercase().replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.wordOrPhrase,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Rhyme Category Tag
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = categoryColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = entry.type.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = categoryColor,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Syllable Count Badge
                    Text(
                        text = "${entry.syllableCount} syl",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    )
                }

                if (entry.definitionOrContext.isNotBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = entry.definitionOrContext,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Insert Button Action
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = categoryColor.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, categoryColor),
                onClick = onInsert,
                modifier = Modifier.testTag("insert_${entry.wordOrPhrase.lowercase().replace(" ", "_")}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Insert word",
                        tint = categoryColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "INSERT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = categoryColor,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}
