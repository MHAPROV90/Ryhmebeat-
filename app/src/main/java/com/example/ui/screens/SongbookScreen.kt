package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ControlPointDuplicate
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.Genre
import com.example.data.SongDraft
import com.example.ui.RhymeBeatViewModel
import com.example.ui.SongbookSortOrder
import com.example.ui.theme.BeatDownbeat
import com.example.ui.theme.BeatLedActive
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SongbookScreen(
    viewModel: RhymeBeatViewModel,
    onLoadDraft: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allDrafts by viewModel.savedDrafts.collectAsState()
    val filteredDrafts by viewModel.filteredDrafts.collectAsState()
    val searchQuery by viewModel.songbookSearchQuery.collectAsState()
    val selectedGenreFilter by viewModel.songbookGenreFilter.collectAsState()
    val onlyFavorites by viewModel.songbookOnlyFavorites.collectAsState()
    val sortOrder by viewModel.songbookSortOrder.collectAsState()

    // Music playback state in Songbook
    val playingDraftId by viewModel.songbookPlayingDraftId.collectAsState()
    val beatPosition by viewModel.beatPosition.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()

    var draftToRename by remember { mutableStateOf<SongDraft?>(null) }
    var draftToDelete by remember { mutableStateOf<SongDraft?>(null) }

    val currentlyPlayingDraft = remember(playingDraftId, allDrafts) {
        allDrafts.find { it.id == playingDraftId }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. Header with Stats & New Project Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PROJECT HISTORY",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "${allDrafts.size} saved drafts in local Room database",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }

            Button(
                onClick = {
                    viewModel.startNewProject()
                    onLoadDraft() // Navigate to Studio
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricMagenta,
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("new_project_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "NEW DRAFT",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // 2. Now Playing Music Player Banner in Songbook
        if (currentlyPlayingDraft != null && beatPosition.isPlaying) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StudioCardElevated,
                border = BorderStroke(1.5.dp, NeonCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("songbook_music_player_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Flashing beat LED
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (beatPosition.beat == 1) BeatDownbeat else BeatLedActive)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "PLAYING: ${currentlyPlayingDraft.title}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${currentlyPlayingDraft.genreName} • ${currentlyPlayingDraft.tempoBpm} BPM • Bar ${beatPosition.bar}.${beatPosition.beat}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = NeonCyan,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = viewModel::toggleMute,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Mute toggle",
                                tint = if (isMuted) TextTertiary else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = viewModel::stopSongbookMusic,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Stop music",
                                tint = Color(0xFFFF3366),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::setSongbookSearchQuery,
            placeholder = { Text("Search projects by title or lyrics...", style = MaterialTheme.typography.bodySmall) },
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
                    IconButton(onClick = { viewModel.setSongbookSearchQuery("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
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
                .height(50.dp)
                .testTag("songbook_search_input"),
            shape = RoundedCornerShape(12.dp),
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

        // 4. Filter Chips (Favorites, Genre Filters)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Favorites filter
            FilterChip(
                selected = onlyFavorites,
                onClick = viewModel::toggleSongbookOnlyFavorites,
                leadingIcon = {
                    Icon(
                        imageVector = if (onlyFavorites) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = if (onlyFavorites) Color(0xFFFFB703) else TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                },
                label = { Text("Favorites", style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFFFB703).copy(alpha = 0.2f),
                    selectedLabelColor = Color(0xFFFFB703),
                    containerColor = StudioCardElevated,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = onlyFavorites,
                    borderColor = StudioBorder,
                    selectedBorderColor = Color(0xFFFFB703)
                ),
                modifier = Modifier.testTag("filter_favorites")
            )

            // "All Genres"
            FilterChip(
                selected = selectedGenreFilter == null,
                onClick = { viewModel.setSongbookGenreFilter(null) },
                label = { Text("All Genres", style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                    selectedLabelColor = NeonCyan,
                    containerColor = StudioCardElevated,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedGenreFilter == null,
                    borderColor = StudioBorder,
                    selectedBorderColor = NeonCyan
                )
            )

            // Specific Genres
            Genre.entries.forEach { genre ->
                val isSelected = selectedGenreFilter == genre
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        viewModel.setSongbookGenreFilter(if (isSelected) null else genre)
                    },
                    label = { Text(genre.displayName, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                        selectedLabelColor = NeonCyan,
                        containerColor = StudioCardElevated,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = StudioBorder,
                        selectedBorderColor = NeonCyan
                    ),
                    modifier = Modifier.testTag("filter_genre_${genre.name.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 5. Sort Order Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Showing ${filteredDrafts.size} of ${allDrafts.size} projects",
                style = MaterialTheme.typography.labelSmall.copy(color = TextTertiary)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort", tint = TextTertiary, modifier = Modifier.size(14.dp))
                SongbookSortOrder.entries.forEach { order ->
                    val isSelected = order == sortOrder
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) StudioBorder else Color.Transparent,
                        modifier = Modifier
                            .clickable { viewModel.setSongbookSortOrder(order) }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = order.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) NeonCyan else TextSecondary
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 6. Drafts List or Empty State
        if (filteredDrafts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LibraryMusic,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (allDrafts.isEmpty()) "No Saved Projects Yet" else "No Matching Drafts Found",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (allDrafts.isEmpty()) {
                            "Write lyrics in the Studio and tap 'Save' to persist drafts in your local Room database."
                        } else {
                            "Try clearing your search query or adjusting genre and favorite filters."
                        },
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    if (allDrafts.isEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onLoadDraft,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF00382E)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("GO TO STUDIO WRITER", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("project_history_list")
            ) {
                items(filteredDrafts, key = { it.id }) { draft ->
                    val isPlayingMusic = playingDraftId == draft.id && beatPosition.isPlaying

                    ProjectHistoryCard(
                        draft = draft,
                        isPlayingMusic = isPlayingMusic,
                        beatPosition = beatPosition,
                        onToggleMusic = { viewModel.togglePlaySongbookDraft(draft) },
                        onLoadToStudio = {
                            viewModel.loadDraftIntoStudio(draft)
                            onLoadDraft()
                        },
                        onToggleFavorite = { viewModel.toggleFavoriteDraft(draft) },
                        onDuplicate = { viewModel.duplicateDraft(draft) },
                        onRename = { draftToRename = draft },
                        onDelete = { draftToDelete = draft },
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText(draft.title, draft.lyricsContent))
                            Toast.makeText(context, "Copied '${draft.title}'", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }

    // Rename Dialog
    draftToRename?.let { draft ->
        RenameProjectDialog(
            draft = draft,
            onDismiss = { draftToRename = null },
            onConfirm = { newTitle ->
                viewModel.updateDraftTitle(draft, newTitle)
                draftToRename = null
            }
        )
    }

    // Delete Confirmation Dialog
    draftToDelete?.let { draft ->
        AlertDialog(
            onDismissRequest = { draftToDelete = null },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text("Delete Project?", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Text(
                    "Are you sure you want to permanently delete \"${draft.title}\" from your local database history?",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDraft(draft)
                        draftToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3366), contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Delete", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }
            },
            dismissButton = {
                TextButton(onClick = { draftToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun ProjectHistoryCard(
    draft: SongDraft,
    isPlayingMusic: Boolean,
    beatPosition: com.example.audio.BeatPosition,
    onToggleMusic: () -> Unit,
    onLoadToStudio: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDuplicate: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val formattedDate = remember(draft.updatedAt) {
        val sdf = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
        sdf.format(Date(draft.updatedAt))
    }

    val lines = remember(draft.lyricsContent) {
        draft.lyricsContent.lines().filter { it.isNotBlank() }
    }
    val words = remember(draft.lyricsContent) {
        draft.lyricsContent.split("\\s+".toRegex()).filter { it.isNotBlank() }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("project_draft_card_${draft.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.5.dp,
            when {
                isPlayingMusic -> NeonCyan
                draft.isFavorite -> Color(0xFFFFB703).copy(alpha = 0.5f)
                else -> StudioBorder
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Title, Favorite Star, Expand Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("favorite_button_${draft.id}")
                    ) {
                        Icon(
                            imageVector = if (draft.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = if (draft.isFavorite) "Unfavorite" else "Favorite",
                            tint = if (draft.isFavorite) Color(0xFFFFB703) else TextTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Column {
                        Text(
                            text = draft.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextTertiary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Metadata Chips Row (Genre, Tempo, Line & Word stats, Music playing status)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (draft.isAiGenerated) NeonCyan.copy(alpha = 0.15f) else Color(0xFFFFB703).copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, if (draft.isAiGenerated) NeonCyan.copy(alpha = 0.4f) else Color(0xFFFFB703).copy(alpha = 0.4f))
                ) {
                    Text(
                        text = if (draft.isAiGenerated) "AI GENERATED" else "WRITTEN DRAFT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (draft.isAiGenerated) NeonCyan else Color(0xFFFFB703),
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ElectricMagenta.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, ElectricMagenta.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = draft.genreName.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ElectricMagenta,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = NeonCyan.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "${draft.tempoBpm} BPM",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StudioCardElevated
                ) {
                    Text(
                        text = "${lines.size} bars • ${words.size} words",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Real-time playing pulse indicator
                if (isPlayingMusic) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NeonCyan.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, NeonCyan)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (beatPosition.beat == 1) BeatDownbeat else BeatLedActive)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "B${beatPosition.bar}.${beatPosition.beat}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            // Expandable Lyrics Content Preview
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = StudioCardElevated,
                        border = BorderStroke(1.dp, StudioBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = draft.lyricsContent,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 22.sp
                            ),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row: Copy, Rename, Duplicate, Delete, PLAY MUSIC BEAT, Open in Studio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(onClick = onCopy, modifier = Modifier.size(34.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy lyrics",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onRename, modifier = Modifier.size(34.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Rename project",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onDuplicate, modifier = Modifier.size(34.dp)) {
                        Icon(
                            imageVector = Icons.Default.ControlPointDuplicate,
                            contentDescription = "Duplicate project",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete project",
                            tint = TextTertiary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Play / Pause Music Beat Button
                    Button(
                        onClick = onToggleMusic,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPlayingMusic) ElectricMagenta else StudioCardElevated,
                            contentColor = if (isPlayingMusic) Color.White else NeonCyan
                        ),
                        border = BorderStroke(1.dp, if (isPlayingMusic) ElectricMagenta else NeonCyan),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("play_music_button_${draft.id}")
                    ) {
                        Icon(
                            imageVector = if (isPlayingMusic) Icons.Default.Pause else Icons.Default.MusicNote,
                            contentDescription = if (isPlayingMusic) "Pause Music" else "Play Music",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPlayingMusic) "PAUSE" else "PLAY BEAT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            )
                        )
                    }

                    // Open in Studio Button
                    Button(
                        onClick = onLoadToStudio,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = Color(0xFF00382E)
                        ),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("load_project_button_${draft.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "EDIT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RenameProjectDialog(
    draft: SongDraft,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var title by remember { mutableStateOf(draft.title) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text("Rename Project", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Project Title") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rename_title_input"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = StudioBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title) },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF00382E)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
