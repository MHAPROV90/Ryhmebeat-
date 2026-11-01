package com.example.data

import com.example.audio.Genre

enum class RhymeType(val label: String) {
    ALL("All"),
    SLANG_IDIOM("Slang & Idioms"),
    EXACT("Exact Rhymes"),
    SLANT("Slant / Cadence"),
    MULTI_SYLLABLE("Multi-Syllable")
}

data class RhymeEntry(
    val wordOrPhrase: String,
    val type: RhymeType,
    val genre: Genre,
    val syllableCount: Int,
    val definitionOrContext: String,
    val relatedRhymes: List<String> = emptyList()
)

data class GenreLexicon(
    val genre: Genre,
    val culturalTerms: List<RhymeEntry>,
    val rhymeClusters: Map<String, List<String>> // ending sound -> rhyming words
)

object GenreRhymeDictionary {

    val GENRE_LEXICONS: Map<Genre, GenreLexicon> = mapOf(
        Genre.HIP_HOP to GenreLexicon(
            genre = Genre.HIP_HOP,
            culturalTerms = listOf(
                RhymeEntry("Bars", RhymeType.SLANG_IDIOM, Genre.HIP_HOP, 1, "Clever, rhythmic lyric verses with punchlines", listOf("stars", "scars", "cars", "bizarre", "behind bars")),
                RhymeEntry("Drop 16", RhymeType.SLANG_IDIOM, Genre.HIP_HOP, 3, "Spit a classic 16-bar verse", listOf("routine", "unseen", "serene", "machine", "on the scene")),
                RhymeEntry("Cypher", RhymeType.SLANG_IDIOM, Genre.HIP_HOP, 2, "A circle of rappers taking turns freestyling", listOf("hyper", "sniper", "viper", "decipher")),
                RhymeEntry("Secure the bag", RhymeType.SLANG_IDIOM, Genre.HIP_HOP, 4, "Obtain the money, achieve financial win", listOf("no lag", "never brag", "wave the flag", "stay on tag")),
                RhymeEntry("Drippin'", RhymeType.SLANG_IDIOM, Genre.HIP_HOP, 2, "Exuding immense style, confidence, and swagger", listOf("trippin'", "flippin'", "grippin'", "slippin'")),
                RhymeEntry("Keep it 100", RhymeType.SLANG_IDIOM, Genre.HIP_HOP, 4, "Stay authentic, honest, and loyal", listOf("blunted", "confronted", "undisputed", "hunted")),
                RhymeEntry("Mic check", RhymeType.SLANG_IDIOM, Genre.HIP_HOP, 2, "Testing the microphone or taking command of the stage", listOf("neck", "deck", "wreck", "respect", "in check")),
                RhymeEntry("Clock in", RhymeType.SLANG_IDIOM, Genre.HIP_HOP, 2, "Focus deeply on the grind or studio session", listOf("lock in", "walk in", "talk in", "rockin'", "shockin'")),
                RhymeEntry("Cook up", RhymeType.SLANG_IDIOM, Genre.HIP_HOP, 2, "Producing a heavy beat or writing fire lyrics", listOf("hook up", "book up", "shook up", "look up")),
                RhymeEntry("No cap", RhymeType.SLANG_IDIOM, Genre.HIP_HOP, 2, "No lie, telling pure undeniable truth", listOf("on the map", "spit a rap", "snap", "trap", "clap back"))
            ),
            rhymeClusters = mapOf(
                "ar" to listOf("bars", "cars", "scars", "stars", "guitars", "bizarre", "radar", "cigar", "above par"),
                "ow" to listOf("flow", "glow", "show", "pro", "know", "blow", "tempo", "low", "status quo", "heavy blow"),
                "it" to listOf("spit", "hit", "lit", "grit", "commit", "legit", "equip", "admit", "never quit"),
                "ack" to listOf("track", "stack", "back", "pack", "attack", "payback", "exact", "knack", "on track"),
                "eet" to listOf("street", "beat", "heat", "complete", "concrete", "defeat", "discreet", "elite", "on repeat"),
                "in" to listOf("win", "spin", "begin", "within", "discipline", "origin", "in", "chin", "lock in"),
                "one" to listOf("zone", "throne", "stone", "alone", "microphone", "unknown", "backbone", "stepping stone")
            )
        ),
        Genre.TRAP to GenreLexicon(
            genre = Genre.TRAP,
            culturalTerms = listOf(
                RhymeEntry("808", RhymeType.SLANG_IDIOM, Genre.TRAP, 3, "Booming low-end Roland bass drum that rattles trunks", listOf("weight", "state", "fate", "straight", "escalate", "great")),
                RhymeEntry("Rollie", RhymeType.SLANG_IDIOM, Genre.TRAP, 2, "Rolex luxury watch symbolizing elite success", listOf("slowly", "holy", "goalie", "lonely", "one and only")),
                RhymeEntry("Flex", RhymeType.SLANG_IDIOM, Genre.TRAP, 1, "Showcasing wealth, talent, or jewelry boldly", listOf("next", "checks", "complex", "specs", "collect checks")),
                RhymeEntry("Double cup", RhymeType.SLANG_IDIOM, Genre.TRAP, 3, "Iconic southern trap container staple", listOf("bubble up", "trouble up", "turn it up", "run it up")),
                RhymeEntry("Spin the block", RhymeType.SLANG_IDIOM, Genre.TRAP, 3, "Returning to an area with vigilance", listOf("tick of clock", "solid rock", "on the block", "never stop")),
                RhymeEntry("Trap out", RhymeType.SLANG_IDIOM, Genre.TRAP, 2, "Working relentlessly from a gritty location", listOf("black out", "tap out", "cash out", "mash out", "route")),
                RhymeEntry("Ice on neck", RhymeType.SLANG_IDIOM, Genre.TRAP, 3, "Diamond jewelry glistening on a necklace", listOf("cut the check", "earn respect", "no regret", "effect")),
                RhymeEntry("Bando", RhymeType.SLANG_IDIOM, Genre.TRAP, 2, "Abandoned house transformed into a studio or hub", listOf("commando", "Lando", "stand tall", "demand more"))
            ),
            rhymeClusters = mapOf(
                "ip" to listOf("drip", "flip", "grip", "strip", "clip", "whip", "trip", "round trip", "championship"),
                "ash" to listOf("cash", "stash", "flash", "smash", "dash", "clash", "crash", "quick flash"),
                "op" to listOf("drop", "top", "stop", "pop", "chop", "crop", "nonstop", "to the top"),
                "ate" to listOf("808", "weight", "plate", "straight", "elevate", "hesitate", "celebrate", "golden state"),
                "up" to listOf("run it up", "cup", "double up", "erupt", "corrupt", "abrupt", "toughen up")
            )
        ),
        Genre.POP to GenreLexicon(
            genre = Genre.POP,
            culturalTerms = listOf(
                RhymeEntry("Main character", RhymeType.SLANG_IDIOM, Genre.POP, 4, "Radiating undeniable cinematic focus and confidence", listOf("no barrier", "traveler", "surrender", "unmatched flair")),
                RhymeEntry("Glow up", RhymeType.SLANG_IDIOM, Genre.POP, 2, "Incredible physical, artistic, or emotional transformation", listOf("show up", "blow up", "grow up", "throw up")),
                RhymeEntry("Golden hour", RhymeType.SLANG_IDIOM, Genre.POP, 3, "The magic twilight glow just before dusk", listOf("empower", "shower", "solar power", "every hour")),
                RhymeEntry("Electric night", RhymeType.SLANG_IDIOM, Genre.POP, 4, "High energy evening full of neon sparks and club rhythm", listOf("neon light", "hold on tight", "take to flight", "out of sight")),
                RhymeEntry("Heartbeat", RhymeType.SLANG_IDIOM, Genre.POP, 2, "The pulse of attraction and synth-pop tempo", listOf("city street", "bittersweet", "incomplete", "on repeat")),
                RhymeEntry("Midnight drive", RhymeType.SLANG_IDIOM, Genre.POP, 3, "Cruising windows down under city streetlights", listOf("stay alive", "revive", "five", "take a dive", "arrive")),
                RhymeEntry("Vibe check", RhymeType.SLANG_IDIOM, Genre.POP, 2, "Testing if the mood and mutual connection match", listOf("side step", "high step", "electric", "direct")),
                RhymeEntry("Spotlight", RhymeType.SLANG_IDIOM, Genre.POP, 2, "Center stage glow where all eyes fixate", listOf("tonight", "ignite", "daylight", "satellite", "bright light"))
            ),
            rhymeClusters = mapOf(
                "ite" to listOf("night", "light", "ignite", "tonight", "bright", "sight", "electric light", "satellite"),
                "eam" to listOf("dream", "gleam", "beam", "stream", "extreme", "supreme", "color scheme"),
                "ance" to listOf("dance", "chance", "glance", "romance", "advance", "in a trance"),
                "ove" to listOf("groove", "move", "prove", "improve", "make a move", "in the groove"),
                "ine" to listOf("shine", "skyline", "divine", "fine", "design", "combine", "neon sign")
            )
        ),
        Genre.COUNTRY to GenreLexicon(
            genre = Genre.COUNTRY,
            culturalTerms = listOf(
                RhymeEntry("Tailgate", RhymeType.SLANG_IDIOM, Genre.COUNTRY, 2, "Sitting on the back of a pickup truck under the stars", listOf("late", "date", "wait", "state", "pearly gate", "first rate")),
                RhymeEntry("Backroads", RhymeType.SLANG_IDIOM, Genre.COUNTRY, 2, "Unpaved dirt paths far from highway congestion", listOf("heavy loads", "crossroads", "gravel roads", "abodes")),
                RhymeEntry("Two-step", RhymeType.SLANG_IDIOM, Genre.COUNTRY, 2, "Classic country partner dance across wooden tavern floors", listOf("side-step", "misstep", "kept", "wept", "well-swept")),
                RhymeEntry("Front porch", RhymeType.SLANG_IDIOM, Genre.COUNTRY, 2, "Where family gathers to strum guitars and watch sunsets", listOf("torch", "scorch", "burn the torch", "porch swing")),
                RhymeEntry("Honeysuckle", RhymeType.SLANG_IDIOM, Genre.COUNTRY, 4, "Sweet summer blossom fragrance along country fences", listOf("silver buckle", "chuckle", "knuckle", "never buckle")),
                RhymeEntry("Silverado", RhymeType.SLANG_IDIOM, Genre.COUNTRY, 4, "Iconic workhorse truck rolling down country lanes", listOf("El Dorado", "desperado", "Colorado", "bravado")),
                RhymeEntry("Whiskey river", RhymeType.SLANG_IDIOM, Genre.COUNTRY, 4, "Washing away heartache and hard memories", listOf("cold river", "make me shiver", "quiver", "deliver")),
                RhymeEntry("Southern drawl", RhymeType.SLANG_IDIOM, Genre.COUNTRY, 3, "Warm cadence and slow comforting speech", listOf("stand tall", "fall", "hall", "call", "through it all"))
            ),
            rhymeClusters = mapOf(
                "own" to listOf("town", "down", "brown", "small town", "sundown", "homebound", "turn around"),
                "ain" to listOf("rain", "train", "pain", "dirt train", "freight train", "cane", "plain"),
                "ust" to listOf("dust", "rust", "trust", "must", "robust", "highway dust", "bite the dust"),
                "eans" to listOf("jeans", "blue jeans", "dreams", "streams", "by all means", "tambourines"),
                "eed" to listOf("creek", "week", "feet", "need", "speed", "southern seed", "guaranteed")
            )
        ),
        Genre.RNB to GenreLexicon(
            genre = Genre.RNB,
            culturalTerms = listOf(
                RhymeEntry("Slow jam", RhymeType.SLANG_IDIOM, Genre.RNB, 2, "Smooth romantic ballad with deep emotional groove", listOf("program", "hologram", "damn", "who I am")),
                RhymeEntry("Midnight call", RhymeType.SLANG_IDIOM, Genre.RNB, 3, "Late night vulnerability when emotions peak", listOf("fall", "wall", "give my all", "through the hall")),
                RhymeEntry("Velvet voice", RhymeType.SLANG_IDIOM, Genre.RNB, 3, "Silky rich tone that melts tension and soothes", listOf("no choice", "rejoice", "one voice", "poise")),
                RhymeEntry("Tender touch", RhymeType.SLANG_IDIOM, Genre.RNB, 3, "Gentle affection that speaks without words", listOf("too much", "crutch", "such", "in clutch")),
                RhymeEntry("Sweet surrender", RhymeType.SLANG_IDIOM, Genre.RNB, 4, "Letting down guards completely for real love", listOf("warm defender", "tender", "remember", "forever")),
                RhymeEntry("Soul vibration", RhymeType.SLANG_IDIOM, Genre.RNB, 4, "Deep energetic resonance connecting two spirits", listOf("hesitation", "destination", "temptation", "creation"))
            ),
            rhymeClusters = mapOf(
                "ire" to listOf("fire", "desire", "higher", "aspire", "choir", "wire", "soul's desire"),
                "eep" to listOf("deep", "keep", "sleep", "weep", "secrets keep", "leap"),
                "ow" to listOf("glow", "slow", "know", "flow", "shadow", "window", "steady and slow"),
                "oul" to listOf("soul", "whole", "control", "role", "toll", "heart and soul")
            )
        ),
        Genre.LO_FI to GenreLexicon(
            genre = Genre.LO_FI,
            culturalTerms = listOf(
                RhymeEntry("Vinyl crackle", RhymeType.SLANG_IDIOM, Genre.LO_FI, 4, "Warm analog surface noise comforting the listener", listOf("gentle tackle", "shackle", "crackle and hum")),
                RhymeEntry("Cassette tape", RhymeType.SLANG_IDIOM, Genre.LO_FI, 3, "Wobbly vintage audio texture evoking nostalgia", listOf("landscape", "escape", "drape", "shape")),
                RhymeEntry("Foggy window", RhymeType.SLANG_IDIOM, Genre.LO_FI, 4, "Raindrops streaming as thoughts drift inward", listOf("soft willow", "feather pillow", "candle glow", "gentle blow")),
                RhymeEntry("Warm coffee", RhymeType.SLANG_IDIOM, Genre.LO_FI, 3, "Morning tranquility accompanied by mellow chord loops", listOf("toffee", "softly", "lofty", "calm sea")),
                RhymeEntry("Daydreaming", RhymeType.SLANG_IDIOM, Genre.LO_FI, 3, "Mind wandering into peaceful realms", listOf("gleaming", "streaming", "seeming", "teaming"))
            ),
            rhymeClusters = mapOf(
                "aze" to listOf("haze", "days", "gaze", "maze", "rays", "golden haze", "rainy days"),
                "eez" to listOf("breeze", "trees", "ease", "freeze", "autumn breeze", "put at ease"),
                "alm" to listOf("calm", "palm", "balm", "psalm", "healing balm", "peace and calm")
            )
        ),
        Genre.ROCK to GenreLexicon(
            genre = Genre.ROCK,
            culturalTerms = listOf(
                RhymeEntry("Overdrive", RhymeType.SLANG_IDIOM, Genre.ROCK, 3, "Tube amplifier pushed past its clean limit into grit", listOf("alive", "thrive", "dive", "survive", "drive")),
                RhymeEntry("Power chord", RhymeType.SLANG_IDIOM, Genre.ROCK, 3, "Raw fundamental fifth chord driving stadium anthems", listOf("sword", "roared", "record", "reward", "restored")),
                RhymeEntry("Rebel yell", RhymeType.SLANG_IDIOM, Genre.ROCK, 3, "Defiant shout rising against conformity", listOf("tell", "bell", "break the spell", "unravel", "swell")),
                RhymeEntry("Breakdown", RhymeType.SLANG_IDIOM, Genre.ROCK, 2, "Heavy syncopated shift where the crowd surges", listOf("shake down", "take down", "wake the town", "break ground")),
                RhymeEntry("Stage dive", RhymeType.SLANG_IDIOM, Genre.ROCK, 2, "Leaping directly into the hands of the pit", listOf("survive", "revive", "hive", "alive at five"))
            ),
            rhymeClusters = mapOf(
                "age" to listOf("rage", "stage", "cage", "page", "gauge", "break the cage", "turn the page"),
                "ire" to listOf("wire", "fire", "desire", "live wire", "set on fire", "higher"),
                "ash" to listOf("crash", "smash", "flash", "trash", "whiplash", "electric clash")
            )
        )
    )

    fun searchRhymes(query: String, genre: Genre, typeFilter: RhymeType = RhymeType.ALL): List<RhymeEntry> {
        val cleanQuery = query.trim().lowercase()
        val lexicon = GENRE_LEXICONS[genre] ?: return emptyList()

        val results = mutableListOf<RhymeEntry>()

        // 1. Cultural Slang & Idioms matching query or related words
        for (term in lexicon.culturalTerms) {
            val matchesQuery = cleanQuery.isEmpty() ||
                    term.wordOrPhrase.lowercase().contains(cleanQuery) ||
                    term.definitionOrContext.lowercase().contains(cleanQuery) ||
                    term.relatedRhymes.any { it.lowercase().contains(cleanQuery) }

            if (matchesQuery) {
                if (typeFilter == RhymeType.ALL || typeFilter == RhymeType.SLANG_IDIOM) {
                    results.add(term)
                }
            }
        }

        // 2. Rhyme clusters matching the ending sound of query
        if (cleanQuery.isNotEmpty()) {
            val endingSound2 = if (cleanQuery.length >= 2) cleanQuery.takeLast(2) else cleanQuery
            val endingSound3 = if (cleanQuery.length >= 3) cleanQuery.takeLast(3) else cleanQuery

            for ((sound, words) in lexicon.rhymeClusters) {
                val matchesSound = sound == endingSound2 || sound == endingSound3 ||
                        words.any { it.lowercase().endsWith(endingSound2) || it.lowercase() == cleanQuery }

                if (matchesSound) {
                    for (word in words) {
                        if (word.lowercase() != cleanQuery && results.none { it.wordOrPhrase.equals(word, ignoreCase = true) }) {
                            val syl = estimateSyllables(word)
                            val isMulti = syl >= 3 || word.contains(" ")
                            val isExact = word.lowercase().endsWith(endingSound2)

                            val category = when {
                                isMulti -> RhymeType.MULTI_SYLLABLE
                                isExact -> RhymeType.EXACT
                                else -> RhymeType.SLANT
                            }

                            if (typeFilter == RhymeType.ALL || typeFilter == category) {
                                results.add(
                                    RhymeEntry(
                                        wordOrPhrase = word,
                                        type = category,
                                        genre = genre,
                                        syllableCount = syl,
                                        definitionOrContext = "Matches '$sound' rhyme scheme in ${genre.displayName}",
                                        relatedRhymes = words.filter { it != word }
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // If no query or results are few, supplement with top genre terms
        if (cleanQuery.isEmpty() && results.size < 8) {
            for ((_, words) in lexicon.rhymeClusters) {
                for (word in words.take(2)) {
                    if (results.none { it.wordOrPhrase.equals(word, ignoreCase = true) }) {
                        val syl = estimateSyllables(word)
                        val cat = if (syl >= 3 || word.contains(" ")) RhymeType.MULTI_SYLLABLE else RhymeType.EXACT
                        if (typeFilter == RhymeType.ALL || typeFilter == cat) {
                            results.add(
                                RhymeEntry(
                                    wordOrPhrase = word,
                                    type = cat,
                                    genre = genre,
                                    syllableCount = syl,
                                    definitionOrContext = "Essential vocabulary in ${genre.displayName}",
                                    relatedRhymes = words.filter { it != word }
                                )
                            )
                        }
                    }
                }
            }
        }

        return results
    }

    fun estimateSyllables(text: String): Int {
        var count = 0
        val words = text.lowercase().split("\\s+".toRegex())
        for (w in words) {
            var wordCount = 0
            var prevIsVowel = false
            val vowels = setOf('a', 'e', 'i', 'o', 'u', 'y')
            val trimmed = w.trim { !it.isLetter() }
            if (trimmed.isEmpty()) continue

            for (i in trimmed.indices) {
                val isVowel = trimmed[i] in vowels
                if (isVowel && !prevIsVowel) {
                    wordCount++
                }
                prevIsVowel = isVowel
            }
            if (trimmed.endsWith("e") && !trimmed.endsWith("le") && wordCount > 1) {
                wordCount--
            }
            count += maxOf(1, wordCount)
        }
        return maxOf(1, count)
    }
}
