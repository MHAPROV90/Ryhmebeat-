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
}
