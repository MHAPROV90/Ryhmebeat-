package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM song_drafts ORDER BY updatedAt DESC")
    fun getAllDrafts(): Flow<List<SongDraft>>

    @Query("SELECT * FROM song_drafts ORDER BY updatedAt DESC")
    suspend fun getAllDraftsList(): List<SongDraft>

    @Query("SELECT * FROM song_drafts WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoriteDrafts(): Flow<List<SongDraft>>

    @Query("SELECT * FROM song_drafts WHERE isAiGenerated = 1 ORDER BY updatedAt DESC")
    fun getAiGeneratedDrafts(): Flow<List<SongDraft>>

    @Query("SELECT * FROM song_drafts WHERE isAiGenerated = 1 ORDER BY updatedAt DESC")
    suspend fun getAiGeneratedDraftsList(): List<SongDraft>

    @Query("SELECT * FROM song_drafts WHERE isAiGenerated = 0 ORDER BY updatedAt DESC")
    fun getUserWrittenDrafts(): Flow<List<SongDraft>>

    @Query("SELECT * FROM song_drafts WHERE isAiGenerated = 0 ORDER BY updatedAt DESC")
    suspend fun getUserWrittenDraftsList(): List<SongDraft>

    @Query("SELECT * FROM song_drafts WHERE title LIKE '%' || :query || '%' OR lyricsContent LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchDrafts(query: String): Flow<List<SongDraft>>

    @Query("SELECT * FROM song_drafts WHERE title LIKE '%' || :query || '%' OR lyricsContent LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    suspend fun searchDraftsList(query: String): List<SongDraft>

    @Query("SELECT * FROM song_drafts WHERE id = :id LIMIT 1")
    suspend fun getDraftById(id: Long): SongDraft?

    @Query("SELECT COUNT(*) FROM song_drafts")
    fun getDraftCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: SongDraft): Long

    @Update
    suspend fun updateDraft(draft: SongDraft)

    @Delete
    suspend fun deleteDraft(draft: SongDraft)

    @Query("DELETE FROM song_drafts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE song_drafts SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)
}
