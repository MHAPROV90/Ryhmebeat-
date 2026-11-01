package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song_drafts")
data class SongDraft(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val genreName: String,
    val tempoBpm: Int,
    val targetPhrase: String,
    val integrationMode: String,
    val lyricsContent: String,
    val rhymeNotes: String,
    val createdAt: Long = System.currentTimeMillis()
)
