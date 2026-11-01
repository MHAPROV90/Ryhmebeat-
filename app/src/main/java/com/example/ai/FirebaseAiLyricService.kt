package com.example.ai

import android.util.Log
import com.example.audio.Genre
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "LyricService"

enum class IntegrationMode(val displayName: String, val promptInstruction: String) {
    END_RHYME(
        "End Rhyme",
        "Place the target word/phrase (or its direct exact rhymes) prominently at the end of key rhythmic bars to lock in downbeat rhyme cadence."
    ),
    INTERNAL_RHYME(
        "Internal Rhyme",
        "Weave the target word/phrase inside the body of the lines, matching internal assonance and mid-bar syncopations before the line ends."
    ),
    THEMATIC_CONNECTION(
        "Thematic Connection",
        "Use the target word/phrase as the primary conceptual anchor, motif, and hook metaphor, building all imagery, narrative tension, and slang around it."
    )
}

enum class SongSection(val displayName: String, val barCount: Int) {
    VERSE_8("8-Bar Verse", 8),
    VERSE_16("16-Bar Full Verse", 16),
    CHORUS_HOOK("Chorus / Hook (8 bars)", 8),
    FREESTYLE_PUNCHLINES("Freestyle Cypher (8 bars)", 8)
}

data class GeneratedBar(
    val barNumber: Int,
    val text: String,
    val rhymeSchemeTag: String,
    val syllableCount: Int,
    val rhymeWords: List<String>,
    val deliveryTip: String
)

data class GeneratedLyricResult(
    val title: String,
    val tempoNotes: String,
    val integrationExplanation: String,
    val bars: List<GeneratedBar>,
    val culturalTermsUsed: List<String>,
    val suggestedRhymeFollowUps: List<String>
)

class FirebaseAiLyricService {

    private val lyricSchema = Schema.obj(
        properties = mapOf(
            "title" to Schema.string(description = "Creative title of the verse or song"),
            "tempoNotes" to Schema.string(description = "How the syllable flow rides the BPM"),
            "integrationExplanation" to Schema.string(description = "How the target word/phrase was woven in"),
            "bars" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "barNumber" to Schema.integer(description = "Bar number (1, 2, 3...)"),
                        "text" to Schema.string(description = "The lyric line fitting 1 bar of 4/4 time"),
                        "rhymeSchemeTag" to Schema.string(description = "Rhyme scheme letter like A, B, C"),
                        "syllableCount" to Schema.integer(description = "Total syllables in line"),
                        "rhymeWords" to Schema.array(
                            items = Schema.string(),
                            description = "Key rhyming or slang words in this line"
                        ),
                        "deliveryTip" to Schema.string(description = "Vocal cadence tip for the performer")
                    )
                ),
                description = "Sequential bars of lyrics"
            ),
            "culturalTermsUsed" to Schema.array(
                items = Schema.string(),
                description = "Genre-specific slang and cultural terms utilized"
            ),
            "suggestedRhymeFollowUps" to Schema.array(
                items = Schema.string(),
                description = "Further rhyming words for expanding the track"
            )
        )
    )

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = lyricSchema
                temperature = 0.85f
            }
        )
    }

    suspend fun generateLyrics(
        genre: Genre,
        bpm: Int,
        targetPhrase: String,
        integrationMode: IntegrationMode,
        section: SongSection,
        genreSlangContext: List<String>
    ): GeneratedLyricResult {
        return withTimeout(60_000L) {
            try {
                val slangText = if (genreSlangContext.isNotEmpty()) {
                    "Incorporate some of these authentic ${genre.displayName} slang terms or cultural concepts naturally: ${genreSlangContext.joinToString(", ")}."
                } else ""

                val prompt = """
                    You are a master music producer, platinum lyricist, and battle rap champion.
                    Write a professional, authentic ${section.barCount}-bar ${section.displayName} in the genre of ${genre.displayName}.
                    
                    MUSIC & TEMPO SPECS:
                    - Musical Genre: ${genre.displayName} (${genre.description})
                    - Tempo: $bpm BPM (4/4 time signature).
                    - Make sure the syllable count per bar matches the speed of $bpm BPM:
                      * At faster tempos (e.g. 130-150 BPM like Trap), use sharp, rhythmic bursts or bouncy triplets.
                      * At mid tempos (e.g. 85-110 BPM like Boom-Bap or Country), allow thoughtful syncopation and storytelling.
                      * At slower tempos (e.g. 70-85 BPM like Lo-Fi or R&B), leave space between bars for groove.
                    
                    USER INPUT INTEGRATION:
                    - Target Word / Phrase: "$targetPhrase"
                    - Integration Mode: ${integrationMode.displayName}
                    - Instruction: ${integrationMode.promptInstruction}
                    
                    GENRE FLAVOR & SLANG:
                    $slangText
                    Use authentic cultural vocabulary, genuine idioms, and clever wordplay suitable for ${genre.displayName}.
                    
                    OUTPUT REQUIREMENTS:
                    - Exactly ${section.barCount} bars in the 'bars' array.
                    - Mark the rhymeSchemeTag (e.g. AABB or ABAB or AAAA).
                    - Count syllables realistically.
                    - Highlight the specific rhyme words.
                    - Provide a concise delivery tip for each bar (e.g., 'punch downbeat on beat 2', 'triplet roll on the last 4 syllables').
                """.trimIndent()

                val response = model.generateContent(prompt)
                val jsonString = response.text ?: throw IllegalStateException("Model returned empty text.")
                parseLyricJson(jsonString)
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Lyric generation timed out after 60s", e)
                throw e
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to generate lyrics via Gemini", e)
                throw e
            }
        }
    }

    private fun parseLyricJson(jsonString: String): GeneratedLyricResult {
        val root = JSONObject(jsonString)
        val title = root.optString("title", "Untitled Track")
        val tempoNotes = root.optString("tempoNotes", "")
        val integrationExplanation = root.optString("integrationExplanation", "")

        val barsArray = root.optJSONArray("bars") ?: org.json.JSONArray()
        val barsList = mutableListOf<GeneratedBar>()

        for (i in 0 until barsArray.length()) {
            val barObj = barsArray.getJSONObject(i)
            val barNumber = barObj.optInt("barNumber", i + 1)
            val text = barObj.optString("text", "")
            val rhymeSchemeTag = barObj.optString("rhymeSchemeTag", "A")
            val syllableCount = barObj.optInt("syllableCount", 0)

            val rhymeWordsArr = barObj.optJSONArray("rhymeWords")
            val rhymeWordsList = mutableListOf<String>()
            if (rhymeWordsArr != null) {
                for (j in 0 until rhymeWordsArr.length()) {
                    rhymeWordsList.add(rhymeWordsArr.getString(j))
                }
            }

            val deliveryTip = barObj.optString("deliveryTip", "")
            barsList.add(
                GeneratedBar(
                    barNumber = barNumber,
                    text = text,
                    rhymeSchemeTag = rhymeSchemeTag,
                    syllableCount = syllableCount,
                    rhymeWords = rhymeWordsList,
                    deliveryTip = deliveryTip
                )
            )
        }

        val culturalTermsArr = root.optJSONArray("culturalTermsUsed")
        val culturalTerms = mutableListOf<String>()
        if (culturalTermsArr != null) {
            for (i in 0 until culturalTermsArr.length()) {
                culturalTerms.add(culturalTermsArr.getString(i))
            }
        }

        val followUpsArr = root.optJSONArray("suggestedRhymeFollowUps")
        val followUps = mutableListOf<String>()
        if (followUpsArr != null) {
            for (i in 0 until followUpsArr.length()) {
                followUps.add(followUpsArr.getString(i))
            }
        }

        return GeneratedLyricResult(
            title = title,
            tempoNotes = tempoNotes,
            integrationExplanation = integrationExplanation,
            bars = barsList,
            culturalTermsUsed = culturalTerms,
            suggestedRhymeFollowUps = followUps
        )
    }
}
