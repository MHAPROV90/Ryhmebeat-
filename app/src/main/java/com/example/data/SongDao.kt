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
    @Query("SELECT * FROM song_drafts ORDER BY createdAt DESC")
    fun getAllDrafts(): Flow<List<SongDraft>>

    @Query("SELECT * FROM song_drafts WHERE id = :id LIMIT 1")
    suspend fun getDraftById(id: Long): SongDraft?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: SongDraft): Long

    @Update
    suspend fun updateDraft(draft: SongDraft)

    @Delete
    suspend fun deleteDraft(draft: SongDraft)

    @Query("DELETE FROM song_drafts WHERE id = :id")
    suspend fun deleteById(id: Long)
}
