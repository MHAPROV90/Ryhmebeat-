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
                RhymeEntry("No cap", RhymeType.SLANG_IDIOM, Genre.HIP_HOP, 2, "No lie, telling pure undeniable truth", listOf("on the map", "spit a rap", "snap", "trap", "clap back")),
                RhymeEntry("Spit fire", RhymeType.SLANG_IDIOM, Genre.HIP_HOP, 2, "Rapping intensely with unmatched lyrical delivery", listOf("aspire", "empire", "wire", "higher", "soul's desire")),
                RhymeEntry("Grit & Grind", RhymeType.SLANG_IDIOM, Genre.HIP_HOP, 3, "Relentless work ethic on the street and in the booth", listOf("state of mind", "realigned", "defined", "intertwined")),
                RhymeEntry("Game time", RhymeType.SLANG_IDIOM, Genre.HIP_HOP, 2, "The moment of high-stakes performance", listOf("paradigm", "pantmime", "pay the fine", "on time"))
            ),
            rhymeClusters = mapOf(
                "ar" to listOf("bars", "cars", "scars", "stars", "guitars", "bizarre", "radar", "cigar", "above par", "raise the bar"),
                "ow" to listOf("flow", "glow", "show", "pro", "know", "blow", "tempo", "low", "status quo", "heavy blow", "overflow"),
                "it" to listOf("spit", "hit", "lit", "grit", "commit", "legit", "equip", "admit", "never quit", "holy fit"),
                "ack" to listOf("track", "stack", "back", "pack", "attack", "payback", "exact", "knack", "on track", "comeback"),
                "eet" to listOf("street", "beat", "heat", "complete", "concrete", "defeat", "discreet", "elite", "on repeat", "never retreat"),
                "in" to listOf("win", "spin", "begin", "within", "discipline", "origin", "in", "chin", "lock in", "all in"),
                "one" to listOf("zone", "throne", "stone", "alone", "microphone", "unknown", "backbone", "stepping stone", "all on my own"),
                "ite" to listOf("light", "night", "fight", "tight", "sight", "ignite", "spotlight", "out of sight", "ready to bite"),
                "ime" to listOf("rhyme", "time", "prime", "climb", "sublime", "paradigm", "lifetime", "pass time", "drop a dime"),
                "ay" to listOf("play", "day", "way", "stay", "sway", "display", "betray", "make way", "night and day"),
                "ound" to listOf("sound", "ground", "round", "bound", "profound", "found", "unbound", "crowned", "stand my ground"),
                "ain" to listOf("rain", "pain", "train", "chain", "gain", "brain", "remain", "maintain", "break the chain"),
                "ire" to listOf("fire", "wire", "higher", "aspire", "desire", "entire", "empire", "spit fire"),
                "op" to listOf("drop", "top", "stop", "pop", "chop", "crop", "nonstop", "to the top", "never drop"),
                "ash" to listOf("cash", "stash", "flash", "smash", "clash", "crash", "quick flash", "make a dash"),
                "all" to listOf("call", "fall", "wall", "all", "stand tall", "through it all", "give my all")
            )
        ),
        Genre.TRAP to GenreLexicon(
            genre = Genre.TRAP,
            culturalTerms = listOf(
                RhymeEntry("808", RhymeType.SLANG_IDIOM, Genre.TRAP, 3, "Booming low-end Roland bass drum that rattles trunks", listOf("weight", "state", "fate", "straight", "escalate", "great", "out the gate")),
                RhymeEntry("Rollie", RhymeType.SLANG_IDIOM, Genre.TRAP, 2, "Rolex luxury watch symbolizing elite success", listOf("slowly", "holy", "goalie", "lonely", "one and only")),
                RhymeEntry("Flex", RhymeType.SLANG_IDIOM, Genre.TRAP, 1, "Showcasing wealth, talent, or jewelry boldly", listOf("next", "checks", "complex", "specs", "collect checks", "on to the next")),
                RhymeEntry("Double cup", RhymeType.SLANG_IDIOM, Genre.TRAP, 3, "Iconic southern trap container staple", listOf("bubble up", "trouble up", "turn it up", "run it up", "never interrupt")),
                RhymeEntry("Spin the block", RhymeType.SLANG_IDIOM, Genre.TRAP, 3, "Returning to an area with vigilance", listOf("tick of clock", "solid rock", "on the block", "never stop", "round the clock")),
                RhymeEntry("Trap out", RhymeType.SLANG_IDIOM, Genre.TRAP, 2, "Working relentlessly from a gritty location", listOf("black out", "tap out", "cash out", "mash out", "route", "clear out")),
                RhymeEntry("Ice on neck", RhymeType.SLANG_IDIOM, Genre.TRAP, 3, "Diamond jewelry glistening on a necklace", listOf("cut the check", "earn respect", "no regret", "effect", "stay in check")),
                RhymeEntry("Bando", RhymeType.SLANG_IDIOM, Genre.TRAP, 2, "Abandoned house transformed into a studio or hub", listOf("commando", "Lando", "stand tall", "demand more", "out the front door")),
                RhymeEntry("Plug", RhymeType.SLANG_IDIOM, Genre.TRAP, 1, "The reliable source or connect", listOf("shrug", "mug", "snug", "show love", "pull the rug")),
                RhymeEntry("Drip", RhymeType.SLANG_IDIOM, Genre.TRAP, 1, "Impeccable designer fashion and icy demeanor", listOf("trip", "grip", "flip", "whip", "round trip", "championship"))
            ),
            rhymeClusters = mapOf(
                "ip" to listOf("drip", "flip", "grip", "strip", "clip", "whip", "trip", "round trip", "championship", "let it rip"),
                "ash" to listOf("cash", "stash", "flash", "smash", "dash", "clash", "crash", "quick flash", "count the cash"),
                "op" to listOf("drop", "top", "stop", "pop", "chop", "crop", "nonstop", "to the top", "from the top"),
                "ate" to listOf("808", "weight", "plate", "straight", "elevate", "hesitate", "celebrate", "golden state", "innovate"),
                "up" to listOf("run it up", "cup", "double up", "erupt", "corrupt", "abrupt", "toughen up", "fill it up"),
                "eet" to listOf("street", "fleet", "beat", "repeat", "concrete", "discreet", "no defeat"),
                "ack" to listOf("stack", "racks", "track", "pack", "blackjack", "payback", "count it back"),
                "in" to listOf("spin", "win", "twin", "chin", "lock in", "take the win"),
                "ite" to listOf("night", "bright", "tight", "light", "flight", "satellite", "neon light"),
                "ar" to listOf("car", "star", "bar", "radar", "foreign car", "superstar")
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
                RhymeEntry("Spotlight", RhymeType.SLANG_IDIOM, Genre.POP, 2, "Center stage glow where all eyes fixate", listOf("tonight", "ignite", "daylight", "satellite", "bright light")),
                RhymeEntry("Catching feelings", RhymeType.SLANG_IDIOM, Genre.POP, 4, "Falling in love despite trying to stay composed", listOf("glass ceilings", "revealing", "healing", "dancing on ceiling"))
            ),
            rhymeClusters = mapOf(
                "ite" to listOf("night", "light", "ignite", "tonight", "bright", "sight", "electric light", "satellite", "hold on tight"),
                "eam" to listOf("dream", "gleam", "beam", "stream", "extreme", "supreme", "color scheme", "laser beam"),
                "ance" to listOf("dance", "chance", "glance", "romance", "advance", "in a trance", "second chance"),
                "ove" to listOf("groove", "move", "prove", "improve", "make a move", "in the groove", "smooth"),
                "ine" to listOf("shine", "skyline", "divine", "fine", "design", "combine", "neon sign", "stars align"),
                "eet" to listOf("sweet", "beat", "street", "meet", "complete", "on repeat", "heartbeat"),
                "art" to listOf("heart", "start", "part", "spark", "apart", "state of the art"),
                "ay" to listOf("day", "stay", "say", "away", "play", "holiday", "run away"),
                "ow" to listOf("glow", "show", "slow", "know", "go", "overflow", "radio")
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
                RhymeEntry("Southern drawl", RhymeType.SLANG_IDIOM, Genre.COUNTRY, 3, "Warm cadence and slow comforting speech", listOf("stand tall", "fall", "hall", "call", "through it all")),
                RhymeEntry("Blue jeans", RhymeType.SLANG_IDIOM, Genre.COUNTRY, 2, "Worn-in denim worn for chores and Friday night dates", listOf("dreams", "streams", "by all means", "tambourines", "seams"))
            ),
            rhymeClusters = mapOf(
                "own" to listOf("town", "down", "brown", "small town", "sundown", "homebound", "turn around", "settle down"),
                "ain" to listOf("rain", "train", "pain", "dirt train", "freight train", "cane", "plain", "country lane"),
                "ust" to listOf("dust", "rust", "trust", "must", "robust", "highway dust", "bite the dust", "iron rust"),
                "eans" to listOf("jeans", "blue jeans", "dreams", "streams", "by all means", "tambourines"),
                "eed" to listOf("creek", "need", "speed", "southern seed", "guaranteed", "dirt road weed"),
                "ack" to listOf("track", "back", "shack", "dirt pack", "whiskey stack", "head on back"),
                "ight" to listOf("night", "light", "bonfire light", "stars so bright", "hold on tight", "firelight"),
                "ay" to listOf("day", "hay", "stay", "way", "stray", "yesterday", "pave the way")
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
                "ire" to listOf("fire", "desire", "higher", "aspire", "choir", "wire", "soul's desire", "burn with desire"),
                "eep" to listOf("deep", "keep", "sleep", "weep", "secrets keep", "leap", "run too deep"),
                "ow" to listOf("glow", "slow", "know", "flow", "shadow", "window", "steady and slow", "let it show"),
                "oul" to listOf("soul", "whole", "control", "role", "toll", "heart and soul", "make me whole"),
                "ine" to listOf("mine", "fine", "divine", "wine", "twine", "candle shine", "in time"),
                "eet" to listOf("sweet", "heartbeat", "meet", "complete", "so sweet", "in the heat")
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
                "aze" to listOf("haze", "days", "gaze", "maze", "rays", "golden haze", "rainy days", "summer haze"),
                "eez" to listOf("breeze", "trees", "ease", "freeze", "autumn breeze", "put at ease", "mellow breeze"),
                "alm" to listOf("calm", "palm", "balm", "psalm", "healing balm", "peace and calm"),
                "ow" to listOf("slow", "glow", "flow", "shadow", "mellow", "window", "drifting slow"),
                "ain" to listOf("rain", "window pane", "gain", "gentle rain", "summer rain")
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
                "age" to listOf("rage", "stage", "cage", "page", "gauge", "break the cage", "turn the page", "center stage"),
                "ire" to listOf("wire", "fire", "desire", "live wire", "set on fire", "higher", "burn down the wire"),
                "ash" to listOf("crash", "smash", "flash", "trash", "whiplash", "electric clash", "lightning flash"),
                "ell" to listOf("yell", "hell", "bell", "spell", "rebel yell", "ring the bell", "unravel"),
                "ock" to listOf("rock", "shock", "lock", "block", "electric shock", "round the clock", "hard rock")
            )
        )
    )

    /**
     * Extracts the last meaningful word typed in a lyric string, ignoring trailing punctuation or spacing.
     */
    fun extractLastWord(text: String): String {
        if (text.isBlank()) return ""
        val words = text.split(Regex("[^a-zA-Z0-9']+")).filter { it.isNotBlank() }
        val candidate = words.lastOrNull() ?: return ""
        return candidate.trim { !it.isLetterOrDigit() }.lowercase()
    }

    /**
     * Map common English spelling variations to phonetic sound families for robust real-time rhyme lookup.
     */
    private fun getPhoneticEndingKeys(cleanWord: String): List<String> {
        val w = cleanWord.lowercase().trim { !it.isLetter() }
        if (w.isEmpty()) return emptyList()

        val keys = mutableListOf<String>()

        // Canonical phonetic mapping rules
        when {
            w.endsWith("ight") || w.endsWith("ite") || w.endsWith("yke") || w.endsWith("ike") -> {
                keys.add("ite")
                keys.add("ight")
            }
            w.endsWith("eet") || w.endsWith("eat") || w.endsWith("ete") -> {
                keys.add("eet")
                keys.add("eat")
            }
            w.endsWith("ay") || w.endsWith("ey") || w.endsWith("ai") -> {
                keys.add("ay")
            }
            w.endsWith("ow") || w.endsWith("oe") || w.endsWith("o") || w.endsWith("oa") -> {
                keys.add("ow")
            }
            w.endsWith("ain") || w.endsWith("ane") || w.endsWith("ein") -> {
                keys.add("ain")
            }
            w.endsWith("ack") -> {
                keys.add("ack")
            }
            w.endsWith("in") || w.endsWith("inn") || w.endsWith("yn") -> {
                keys.add("in")
            }
            w.endsWith("one") || w.endsWith("oan") -> {
                keys.add("one")
            }
            w.endsWith("own") || w.endsWith("ound") -> {
                keys.add("own")
                keys.add("ound")
            }
            w.endsWith("ip") -> {
                keys.add("ip")
            }
            w.endsWith("ate") || w.endsWith("ait") || w.endsWith("eight") -> {
                keys.add("ate")
            }
            w.endsWith("op") || w.endsWith("opp") -> {
                keys.add("op")
            }
            w.endsWith("ash") -> {
                keys.add("ash")
            }
            w.endsWith("ire") || w.endsWith("yre") -> {
                keys.add("ire")
            }
            w.endsWith("art") || w.endsWith("eart") -> {
                keys.add("art")
                keys.add("ar")
            }
            w.endsWith("all") || w.endsWith("awl") -> {
                keys.add("all")
            }
            w.endsWith("ell") -> {
                keys.add("ell")
            }
            w.endsWith("eam") || w.endsWith("eme") -> {
                keys.add("eam")
            }
            w.endsWith("ive") -> {
                keys.add("ive")
            }
            w.endsWith("ime") || w.endsWith("yme") -> {
                keys.add("ime")
            }
            w.endsWith("ock") -> {
                keys.add("ock")
            }
            w.endsWith("un") || w.endsWith("unn") -> {
                keys.add("un")
                keys.add("up")
            }
            w.endsWith("eep") -> {
                keys.add("eep")
            }
            w.endsWith("aze") || w.endsWith("ays") -> {
                keys.add("aze")
            }
            w.endsWith("ove") || w.endsWith("oove") -> {
                keys.add("ove")
            }
            w.endsWith("ance") -> {
                keys.add("ance")
            }
            w.endsWith("ust") -> {
                keys.add("ust")
            }
        }

        // Add 2 and 3 letter tail endings as fallback sounds
        if (w.length >= 2) keys.add(w.takeLast(2))
        if (w.length >= 3) keys.add(w.takeLast(3))

        return keys.distinct()
    }

    /**
     * Real-time rhyme lookup filtered by the selected genre.
     * High priority is given to the target genre's slang, idioms, and vocabulary.
     */
    fun searchRhymes(query: String, genre: Genre, typeFilter: RhymeType = RhymeType.ALL): List<RhymeEntry> {
        val cleanQuery = query.trim().lowercase()
        val targetLexicon = GENRE_LEXICONS[genre] ?: return emptyList()
        val results = mutableListOf<RhymeEntry>()
        val seenWords = mutableSetOf<String>()

        // 1. Cultural Slang & Idioms from current genre matching query or ending sounds
        val soundKeys = if (cleanQuery.isNotEmpty()) getPhoneticEndingKeys(cleanQuery) else emptyList()

        for (term in targetLexicon.culturalTerms) {
            val matchesQuery = cleanQuery.isEmpty() ||
                    term.wordOrPhrase.lowercase().contains(cleanQuery) ||
                    term.definitionOrContext.lowercase().contains(cleanQuery) ||
                    term.relatedRhymes.any { it.lowercase().contains(cleanQuery) || soundKeys.any { k -> it.lowercase().endsWith(k) } }

            if (matchesQuery) {
                if (typeFilter == RhymeType.ALL || typeFilter == RhymeType.SLANG_IDIOM) {
                    if (seenWords.add(term.wordOrPhrase.lowercase())) {
                        results.add(term)
                    }
                }
            }
        }

        // 2. Rhyme clusters from the selected genre matching any phonetic sound keys
        if (cleanQuery.isNotEmpty()) {
            for ((clusterSound, words) in targetLexicon.rhymeClusters) {
                val matchesCluster = soundKeys.contains(clusterSound) ||
                        words.any { it.lowercase() == cleanQuery || soundKeys.any { k -> it.lowercase().endsWith(k) } }

                if (matchesCluster) {
                    for (word in words) {
                        val wordLower = word.lowercase()
                        if (wordLower != cleanQuery && seenWords.add(wordLower)) {
                            val syl = estimateSyllables(word)
                            val isMulti = syl >= 3 || word.contains(" ")
                            val isExact = soundKeys.any { k -> wordLower.endsWith(k) }

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
                                        definitionOrContext = "Matches '$clusterSound' cadence in ${genre.displayName}",
                                        relatedRhymes = words.filter { it != word }
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 3. Fallback: If current genre has fewer than 6 suggestions, pull compatible sounds from other genres
            if (results.size < 6) {
                for ((otherGenre, otherLexicon) in GENRE_LEXICONS) {
                    if (otherGenre == genre) continue
                    for ((clusterSound, words) in otherLexicon.rhymeClusters) {
                        if (soundKeys.contains(clusterSound)) {
                            for (word in words) {
                                val wordLower = word.lowercase()
                                if (wordLower != cleanQuery && seenWords.add(wordLower)) {
                                    val syl = estimateSyllables(word)
                                    val isMulti = syl >= 3 || word.contains(" ")
                                    val category = if (isMulti) RhymeType.MULTI_SYLLABLE else RhymeType.SLANT

                                    if (typeFilter == RhymeType.ALL || typeFilter == category) {
                                        results.add(
                                            RhymeEntry(
                                                wordOrPhrase = word,
                                                type = category,
                                                genre = genre, // Adapted for user genre
                                                syllableCount = syl,
                                                definitionOrContext = "Rhythmic rhyme crossover for ${genre.displayName}",
                                                relatedRhymes = words.filter { it != word }
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Default / Empty query state: supply core genre signature rhymes & inspiration
        if (cleanQuery.isEmpty()) {
            for ((_, words) in targetLexicon.rhymeClusters) {
                for (word in words.take(3)) {
                    val wordLower = word.lowercase()
                    if (seenWords.add(wordLower)) {
                        val syl = estimateSyllables(word)
                        val cat = if (syl >= 3 || word.contains(" ")) RhymeType.MULTI_SYLLABLE else RhymeType.EXACT
                        if (typeFilter == RhymeType.ALL || typeFilter == cat) {
                            results.add(
                                RhymeEntry(
                                    wordOrPhrase = word,
                                    type = cat,
                                    genre = genre,
                                    syllableCount = syl,
                                    definitionOrContext = "Signature vocabulary in ${genre.displayName}",
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
