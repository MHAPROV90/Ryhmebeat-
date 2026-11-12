package com.example.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository layer abstracting local Room database operations for user song drafts and past projects.
 */
class SongRepository(private val songDao: SongDao) {

    val allDrafts: Flow<List<SongDraft>> = songDao.getAllDrafts()
    val favoriteDrafts: Flow<List<SongDraft>> = songDao.getFavoriteDrafts()
    val aiGeneratedDrafts: Flow<List<SongDraft>> = songDao.getAiGeneratedDrafts()
    val userWrittenDrafts: Flow<List<SongDraft>> = songDao.getUserWrittenDrafts()
    val draftCount: Flow<Int> = songDao.getDraftCount()
 
    fun searchDrafts(query: String): Flow<List<SongDraft>> = songDao.searchDrafts(query)
 
    suspend fun getAllDraftsList(): List<SongDraft> = songDao.getAllDraftsList()
 
    suspend fun getAiGeneratedDraftsList(): List<SongDraft> = songDao.getAiGeneratedDraftsList()
 
    suspend fun getUserWrittenDraftsList(): List<SongDraft> = songDao.getUserWrittenDraftsList()

    suspend fun searchDraftsList(query: String): List<SongDraft> = songDao.searchDraftsList(query)

    suspend fun getDraftById(id: Long): SongDraft? = songDao.getDraftById(id)

    suspend fun insertDraft(draft: SongDraft): Long = songDao.insertDraft(draft)

    suspend fun updateDraft(draft: SongDraft) = songDao.updateDraft(draft)

    suspend fun deleteDraft(draft: SongDraft) = songDao.deleteDraft(draft)

    suspend fun deleteById(id: Long) = songDao.deleteById(id)

    suspend fun toggleFavorite(draft: SongDraft) {
        songDao.setFavorite(draft.id, !draft.isFavorite)
    }

    suspend fun duplicateDraft(draft: SongDraft): Long {
        val copy = draft.copy(
            id = 0,
            title = "${draft.title} (Copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return songDao.insertDraft(copy)
    }
}
