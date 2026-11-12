package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.audio.Genre
import com.example.data.GenreRhymeDictionary
import com.example.data.RhymeType
import com.example.data.SongDatabase
import com.example.data.SongDraft
import com.example.data.SongRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var inMemoryDb: SongDatabase
    private lateinit var repository: SongRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        inMemoryDb = Room.inMemoryDatabaseBuilder(context, SongDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = SongRepository(inMemoryDb.songDao())
    }

    @After
    fun teardown() {
        inMemoryDb.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("RhymeBeat", appName)
    }

    @Test
    fun `launch MainActivity without crashing`() {
        org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
    }

    @Test
    fun `audio beat engines initialize valid audio buffer sizes without exception`() {
        val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default)
        val audioEngine = com.example.audio.AudioBeatEngine(scope)
        audioEngine.play()
        audioEngine.stop()

        val drumEngine = com.example.audio.DrumBeatEngine(scope)
        drumEngine.start()
        drumEngine.stop()
    }

    @Test
    fun `extractLastWord correctly isolates trailing word across punctuation and lines`() {
        assertEquals("street", GenreRhymeDictionary.extractLastWord("Walking down the dark street"))
        assertEquals("street", GenreRhymeDictionary.extractLastWord("Walking down the dark street!"))
        assertEquals("night", GenreRhymeDictionary.extractLastWord("I'm waiting for the night... \n"))
        assertEquals("flow", GenreRhymeDictionary.extractLastWord("Line 1: spitting fire\nLine 2: riding the flow"))
        assertEquals("", GenreRhymeDictionary.extractLastWord("   \n\t  "))
    }

    @Test
    fun `real-time rhyme suggestions are filtered by genre`() {
        val hipHopRhymes = GenreRhymeDictionary.searchRhymes(
            query = "street",
            genre = Genre.HIP_HOP,
            typeFilter = RhymeType.ALL
        )
        assertTrue("Hip Hop should return rhymes for 'street'", hipHopRhymes.isNotEmpty())
        assertTrue("All returned rhymes should have HIP_HOP genre", hipHopRhymes.all { it.genre == Genre.HIP_HOP })
        assertTrue("Should include 'beat' or 'heat'", hipHopRhymes.any {
            it.wordOrPhrase.equals("beat", ignoreCase = true) || it.wordOrPhrase.equals("heat", ignoreCase = true)
        })

        val popRhymes = GenreRhymeDictionary.searchRhymes(
            query = "night",
            genre = Genre.POP,
            typeFilter = RhymeType.ALL
        )
        assertTrue("Pop should return rhymes for 'night'", popRhymes.isNotEmpty())
        assertTrue("All returned rhymes should have POP genre", popRhymes.all { it.genre == Genre.POP })
        assertTrue("Should include 'light' or 'bright' or 'ignite'", popRhymes.any {
            it.wordOrPhrase.equals("light", ignoreCase = true) || it.wordOrPhrase.equals("bright", ignoreCase = true)
        })
    }

    @Test
    fun `room database stores user-written lyrics drafts and past project history`() = runBlocking {
        val draft1 = SongDraft(
            title = "Midnight Cypher",
            genreName = Genre.HIP_HOP.name,
            tempoBpm = 92,
            targetPhrase = "Drop 16",
            integrationMode = "END_RHYME",
            lyricsContent = "Clocking in the booth with the rhythm on lock\nSpitting true bars around the clock"
        )
        val id1 = repository.insertDraft(draft1)
        assertTrue("Inserted draft should have positive ID", id1 > 0)

        val allDrafts = repository.getAllDraftsList()
        assertEquals(1, allDrafts.size)
        assertEquals("Midnight Cypher", allDrafts.first().title)

        // Toggle Favorite
        repository.toggleFavorite(allDrafts.first())
        val afterFav = repository.getAllDraftsList()
        assertTrue(afterFav.first().isFavorite)

        // Duplicate Project
        val copyId = repository.duplicateDraft(allDrafts.first())
        assertTrue(copyId > 0)
        val afterDupe = repository.getAllDraftsList()
        assertEquals(2, afterDupe.size)
        assertTrue(afterDupe.any { it.title.contains("Copy") })

        // Search Projects
        val searchResult = repository.searchDraftsList("Cypher")
        assertEquals(2, searchResult.size)

        // Delete draft
        repository.deleteById(id1)
        val remaining = repository.getAllDraftsList()
        assertEquals(1, remaining.size)
    }

    @Test
    fun `songbook past project history supports filtering and search`() = runBlocking {
        val draft1 = SongDraft(
            title = "Boom Bap Classic",
            genreName = Genre.HIP_HOP.name,
            tempoBpm = 94,
            lyricsContent = "Spitting fire on the mic",
            isFavorite = true
        )
        val draft2 = SongDraft(
            title = "Trap Anthem",
            genreName = Genre.TRAP.name,
            tempoBpm = 140,
            lyricsContent = "808 rolling in the trap",
            isFavorite = false
        )
        repository.insertDraft(draft1)
        repository.insertDraft(draft2)

        val allDrafts = repository.getAllDraftsList()
        assertEquals(2, allDrafts.size)

        val search = repository.searchDraftsList("trap")
        assertEquals(1, search.size)
        assertEquals("Trap Anthem", search.first().title)
    }

    @Test
    fun `room database stores generated lyrics locally and queries by isAiGenerated`() = runBlocking {
        val aiDraft = SongDraft(
            title = "Midnight Neon Verse",
            genreName = Genre.TRAP.name,
            tempoBpm = 135,
            targetPhrase = "Midnight Drive",
            integrationMode = "END_RHYME",
            lyricsContent = "Cruising slow with the headlights gleaming\nMidnight drive and the city dreaming",
            rhymeNotes = "AI Generated Verse",
            isAiGenerated = true
        )
        val userDraft = SongDraft(
            title = "My Freestyle",
            genreName = Genre.HIP_HOP.name,
            tempoBpm = 90,
            lyricsContent = "Writing this down in my studio room",
            isAiGenerated = false
        )

        val aiId = repository.insertDraft(aiDraft)
        val userId = repository.insertDraft(userDraft)
        assertTrue("AI draft inserted with positive ID", aiId > 0)
        assertTrue("User draft inserted with positive ID", userId > 0)

        val aiDrafts = repository.getAiGeneratedDraftsList()
        assertEquals(1, aiDrafts.size)
        assertEquals("Midnight Neon Verse", aiDrafts.first().title)
        assertTrue(aiDrafts.first().isAiGenerated)

        val userDrafts = repository.getUserWrittenDraftsList()
        assertEquals(1, userDrafts.size)
        assertEquals("My Freestyle", userDrafts.first().title)
        assertTrue(!userDrafts.first().isAiGenerated)
    }

    @Test
    fun `synth keyboard engine plays notes and drum pads across instruments`() {
        val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default)
        val synthEngine = com.example.audio.SynthKeyboardEngine(scope)

        // Test instruments and octave controls
        assertEquals(4, synthEngine.currentOctave.value)
        synthEngine.octaveUp()
        assertEquals(5, synthEngine.currentOctave.value)
        synthEngine.octaveDown()
        assertEquals(4, synthEngine.currentOctave.value)

        // Test playing notes across instruments
        com.example.audio.SynthInstrument.entries.forEach { instrument ->
            synthEngine.setInstrument(instrument)
            assertEquals(instrument, synthEngine.currentInstrument.value)
            synthEngine.playNote(com.example.audio.KeyboardNote.WHITE_KEYS[0]) // C
            assertEquals("C4", synthEngine.lastPlayedNote.value)
        }

        // Test drum pads
        com.example.audio.DrumSound.entries.forEach { drum ->
            synthEngine.playDrum(drum)
            assertEquals(drum, synthEngine.lastTriggeredDrum.value)
        }

        synthEngine.release()
    }
}

