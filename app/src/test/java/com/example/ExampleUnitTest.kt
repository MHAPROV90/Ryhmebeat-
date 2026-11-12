package com.example

import com.example.audio.Genre
import com.example.data.GenreRhymeDictionary
import com.example.data.RhymeType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testSyllableEstimator() {
        assertEquals(1, GenreRhymeDictionary.estimateSyllables("Bars"))
        assertEquals(2, GenreRhymeDictionary.estimateSyllables("Cypher"))
        assertEquals(3, GenreRhymeDictionary.estimateSyllables("Microphone"))
        assertEquals(4, GenreRhymeDictionary.estimateSyllables("Honeysuckle"))
    }

    @Test
    fun testHipHopSlangDictionary() {
        val results = GenreRhymeDictionary.searchRhymes(
            query = "Bars",
            genre = Genre.HIP_HOP,
            typeFilter = RhymeType.ALL
        )
        assertTrue(results.isNotEmpty())
        assertTrue(results.any { it.wordOrPhrase.equals("Bars", ignoreCase = true) })
    }

    @Test
    fun testCountryRhymeDictionary() {
        val results = GenreRhymeDictionary.searchRhymes(
            query = "Tailgate",
            genre = Genre.COUNTRY,
            typeFilter = RhymeType.ALL
        )
        assertTrue(results.isNotEmpty())
        assertTrue(results.any { it.wordOrPhrase.equals("Tailgate", ignoreCase = true) })
    }

    @Test
    fun testDrumPatternPresets() {
        val hipHop = com.example.audio.DrumPattern.HIP_HOP
        assertTrue("Hip-Hop should have kick on step 0", hipHop.isStepActive(com.example.audio.DrumSound.KICK, 0))
        assertTrue("Hip-Hop should have snare on step 4", hipHop.isStepActive(com.example.audio.DrumSound.SNARE, 4))
        assertTrue("Hip-Hop should have hi-hat on step 2", hipHop.isStepActive(com.example.audio.DrumSound.HI_HAT_CLOSED, 2))

        val trap = com.example.audio.DrumPattern.TRAP
        assertTrue("Trap should have 808 on step 0", trap.isStepActive(com.example.audio.DrumSound.SUB_808, 0))
        assertTrue("Trap should have clap on step 8", trap.isStepActive(com.example.audio.DrumSound.CLAP, 8))

        val metronome = com.example.audio.DrumPattern.METRONOME
        assertTrue("Metronome should click on step 0", metronome.isStepActive(com.example.audio.DrumSound.RIMSHOT, 0))
        assertTrue("Metronome should click on step 4", metronome.isStepActive(com.example.audio.DrumSound.RIMSHOT, 4))
        assertTrue("Metronome should click on step 8", metronome.isStepActive(com.example.audio.DrumSound.RIMSHOT, 8))
        assertTrue("Metronome should click on step 12", metronome.isStepActive(com.example.audio.DrumSound.RIMSHOT, 12))
    }

    @Test
    fun testDrumPatternToggling() {
        val empty = com.example.audio.DrumPattern.createEmpty("Custom Grid")
        org.junit.Assert.assertFalse(empty.isStepActive(com.example.audio.DrumSound.KICK, 0))

        val toggled = empty.copyWithToggledStep(com.example.audio.DrumSound.KICK, 0)
        assertTrue(toggled.isStepActive(com.example.audio.DrumSound.KICK, 0))

        val toggledBack = toggled.copyWithToggledStep(com.example.audio.DrumSound.KICK, 0)
        org.junit.Assert.assertFalse(toggledBack.isStepActive(com.example.audio.DrumSound.KICK, 0))
    }

    @Test
    fun testDrumBeatEngineBpmAndControls() {
        val testScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Unconfined)
        val engine = com.example.audio.DrumBeatEngine(testScope)

        // BPM clamping
        engine.setBpm(120)
        assertEquals(120, engine.bpm.value)
        engine.setBpm(300)
        assertEquals(240, engine.bpm.value)
        engine.setBpm(20)
        assertEquals(40, engine.bpm.value)

        // Mute & Master Volume
        org.junit.Assert.assertFalse(engine.isMuted.value)
        engine.toggleMute()
        assertTrue(engine.isMuted.value)

        engine.setMasterVolume(0.75f)
        assertEquals(0.75f, engine.masterVolume.value, 0.001f)

        // Pattern assignment
        engine.setPattern(com.example.audio.DrumPattern.POP)
        assertEquals("Pop / Dance", engine.activePattern.value.name)
    }
}
