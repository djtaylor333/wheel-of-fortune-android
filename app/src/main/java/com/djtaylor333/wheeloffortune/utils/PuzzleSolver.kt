package com.djtaylor333.wheeloffortune.utils

import com.djtaylor333.wheeloffortune.models.PuzzleResult
import java.util.*
import kotlin.math.max

/**
 * Enhanced PuzzleSolver class for solving Wheel of Fortune puzzles
 */
class PuzzleSolver {
    
    // Dictionary for word lookup and suggestions
    private val wordDictionary = mutableMapOf<String, List<String>>()
    private val commonWords = mutableListOf<String>()
    private val commonLetters = listOf('E', 'T', 'A', 'O', 'I', 'N', 'S', 'H', 'R', 'D', 'L', 'U')
    private val categoryKeywords = mapOf(
        "PHRASE" to listOf("THE", "AND", "OF", "TO", "IN", "FOR", "ON", "WITH", "AT", "BY", "FROM"),
        "PERSON" to listOf("ACTOR", "ACTRESS", "SINGER", "PRESIDENT", "ATHLETE", "DOCTOR", "TEACHER"),
        "PLACE" to listOf("CITY", "COUNTRY", "MOUNTAIN", "OCEAN", "RIVER", "BUILDING", "PARK"),
        "THING" to listOf("OBJECT", "ITEM", "DEVICE", "TOOL", "MACHINE", "VEHICLE"),
        "FOOD & DRINK" to listOf("APPLE", "BANANA", "COFFEE", "WATER", "PIZZA", "BURGER", "WINE"),
        "ON THE MAP" to listOf("AMERICA", "EUROPE", "AFRICA", "ASIA", "PACIFIC", "ATLANTIC", "MOUNTAIN"),
        "AROUND THE HOUSE" to listOf("KITCHEN", "BATHROOM", "BEDROOM", "LIVING", "ROOM", "TABLE", "CHAIR")
    )
    
    init {
        // Load dictionary and common words
        loadDictionary()
    }
    
    private fun loadDictionary() {
        // In a real app, this would load from a file or API
        // For this prototype, we'll use a set of common words and phrases
        
        // Common words for different categories
        val phrases = listOf(
            "WHEEL OF FORTUNE", "AGAINST ALL ODDS", "BEST KEPT SECRET", "DREAM COME TRUE",
            "EASY COME EASY GO", "FIRST THINGS FIRST", "GOOD AS NEW", "HAPPY BIRTHDAY",
            "IN THE NICK OF TIME", "JUST WHAT THE DOCTOR ORDERED", "KEEP IT SIMPLE",
            "LAST BUT NOT LEAST", "MAKE YOURSELF AT HOME", "NO PAIN NO GAIN",
            "ON CLOUD NINE", "PRACTICE MAKES PERFECT", "QUICK AS A FLASH",
            "RIGHT HERE RIGHT NOW", "SAVED BY THE BELL", "TIME FLIES WHEN HAVING FUN",
            "UNDER THE WEATHER", "VARIETY IS THE SPICE OF LIFE", "WHAT GOES AROUND COMES AROUND",
            "YOU ONLY LIVE ONCE", "ZERO TOLERANCE POLICY"
        )
        
        val people = listOf(
            "ABRAHAM LINCOLN", "BEYONCE KNOWLES", "CHARLIE CHAPLIN", "DIANA PRINCESS OF WALES",
            "ELVIS PRESLEY", "FRIDA KAHLO", "GEORGE WASHINGTON", "HELEN KELLER",
            "ISAAC NEWTON", "JENNIFER LOPEZ", "KOBE BRYANT", "LEONARDO DA VINCI",
            "MICHAEL JORDAN", "NELSON MANDELA", "OPRAH WINFREY", "PABLO PICASSO",
            "QUEEN ELIZABETH", "ROSA PARKS", "STEPHEN HAWKING", "THOMAS EDISON",
            "USAIN BOLT", "VINCENT VAN GOGH", "WALT DISNEY", "XAVIER WOODS"
        )
        
        val places = listOf(
            "ATLANTIC OCEAN", "BUCKINGHAM PALACE", "CENTRAL PARK", "DEATH VALLEY",
            "EIFFEL TOWER", "FRENCH RIVIERA", "GRAND CANYON", "HOLLYWOOD BOULEVARD",
            "IGUAZU FALLS", "JAPANESE GARDENS", "KINGDOM OF MOROCCO", "LONDON BRIDGE",
            "MOUNT EVEREST", "NIAGARA FALLS", "OLYMPIC STADIUM", "PACIFIC ISLANDS",
            "QUEENSLAND AUSTRALIA", "ROCKY MOUNTAINS", "STATUE OF LIBERTY", "TIMES SQUARE",
            "UNDER THE SEA", "VATICAN CITY", "WASHINGTON MONUMENT", "YELLOWSTONE PARK"
        )
        
        // Add all words to the dictionary by category
        addToDictionary("PHRASE", phrases)
        addToDictionary("PERSON", people)
        addToDictionary("PLACE", places)
        
        // Add all words to common words list for general matching
        commonWords.addAll(phrases)
        commonWords.addAll(people)
        commonWords.addAll(places)
        
        // Add individual words from phrases to improve matching
        phrases.forEach { phrase ->
            phrase.split(" ").forEach { word ->
                if (word.length > 2) {
                    commonWords.add(word)
                }
            }
        }
    }
    
    private fun addToDictionary(category: String, words: List<String>) {
        wordDictionary[category] = words
    }
    
    /**
     * Solve a Wheel of Fortune puzzle
     * @param puzzleText The current state of the puzzle with blanks
     * @param clue The category or clue for the puzzle
     * @return PuzzleResult with solution and suggested letters
     */
    fun solvePuzzle(puzzleText: String, clue: String): PuzzleResult {
        // Clean up the puzzle text
        val cleanPuzzle = cleanPuzzleText(puzzleText)
        
        // Determine the category from the clue
        val category = determineCategory(clue)
        
        // Create a pattern for matching
        val pattern = createRegexPattern(cleanPuzzle)
        
        // Find matching words or phrases from dictionary
        val possibleSolutions = findPossibleSolutions(pattern, category)
        
        // Get the best solution
        val bestSolution = if (possibleSolutions.isNotEmpty()) {
            possibleSolutions.first()
        } else {
            // If no solution found, return the original puzzle
            cleanPuzzle
        }
        
        // Suggest letters that are not yet revealed
        val suggestedLetters = suggestLetters(cleanPuzzle, bestSolution)
        
        return PuzzleResult(
            solution = bestSolution,
            suggestedLetters = suggestedLetters,
            confidence = calculateConfidence(possibleSolutions, cleanPuzzle, bestSolution)
        )
    }
    
    private fun cleanPuzzleText(puzzleText: String): String {
        // Convert to uppercase and standardize blanks
        return puzzleText.uppercase(Locale.getDefault())
            .replace(Regex("[^A-Z _-]"), "")
            .replace("-", "_")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
    
    private fun determineCategory(clue: String): String {
        if (clue.isBlank()) return ""
        
        val upperClue = clue.uppercase()
        
        // Check if the clue directly mentions a category
        for (category in categoryKeywords.keys) {
            if (upperClue.contains(category)) {
                return category
            }
        }
        
        // Check if the clue contains keywords associated with categories
        for ((category, keywords) in categoryKeywords) {
            for (keyword in keywords) {
                if (upperClue.contains(keyword)) {
                    return category
                }
            }
        }
        
        return ""
    }
    
    private fun createRegexPattern(puzzleText: String): String {
        // Replace blanks with regex pattern for any letter
        var pattern = puzzleText
            .replace("_", "[A-Z]")
            .replace("?", "[A-Z]")
        
        // Escape spaces for regex
        pattern = pattern.replace(" ", "\\s")
        
        return "^$pattern$"
    }
    
    private fun findPossibleSolutions(pattern: String, category: String): List<String> {
        val regex = Regex(pattern, RegexOption.IGNORE_CASE)
        
        // First try to match from the specific category
        if (category.isNotEmpty() && wordDictionary.containsKey(category)) {
            val categoryMatches = wordDictionary[category]!!.filter { regex.matches(it) }
            if (categoryMatches.isNotEmpty()) {
                return categoryMatches
            }
        }
        
        // If no matches in the category, try all common words
        val allMatches = commonWords.filter { regex.matches(it) }
        
        // If still no matches, try partial matching for phrases
        if (allMatches.isEmpty()) {
            return findPartialMatches(pattern)
        }
        
        return allMatches
    }
    
    private fun findPartialMatches(pattern: String): List<String> {
        // For complex patterns, try to match parts of phrases
        // This is useful for partially revealed puzzles
        
        // Remove the start and end anchors
        val relaxedPattern = pattern.removePrefix("^").removeSuffix("$")
        
        // Split by spaces to get word patterns
        val wordPatterns = relaxedPattern.split("\\s")
        
        // For each word pattern, find matching words
        val matchingPhrases = mutableListOf<String>()
        
        for (phrase in commonWords) {
            var matchScore = 0
            val phraseWords = phrase.split(" ")
            
            // Skip if word count doesn't match
            if (phraseWords.size != wordPatterns.size) continue
            
            // Check each word against its pattern
            for (i in phraseWords.indices) {
                val wordRegex = Regex("^${wordPatterns[i]}$", RegexOption.IGNORE_CASE)
                if (wordRegex.matches(phraseWords[i])) {
                    matchScore++
                }
            }
            
            // If more than half the words match, consider it a potential solution
            if (matchScore >= phraseWords.size / 2) {
                matchingPhrases.add(phrase)
            }
        }
        
        return matchingPhrases
    }
    
    private fun suggestLetters(puzzle: String, solution: String): List<Char> {
        // Find letters in the solution that are not yet revealed in the puzzle
        val revealedLetters = puzzle.filter { it in 'A'..'Z' }.toSet()
        val solutionLetters = solution.filter { it in 'A'..'Z' }.toSet()
        
        // Get letters that are in the solution but not revealed yet
        val missingLetters = solutionLetters.minus(revealedLetters).toMutableList()
        
        // Sort missing letters by frequency in the solution
        missingLetters.sortByDescending { letter ->
            solution.count { it == letter }
        }
        
        // If we don't have enough missing letters, add common letters
        if (missingLetters.size < 5) {
            val additionalLetters = commonLetters.filter { 
                it !in revealedLetters && it !in missingLetters 
            }.take(5 - missingLetters.size)
            missingLetters.addAll(additionalLetters)
        }
        
        // Return top 5 suggested letters
        return missingLetters.take(5)
    }
    
    private fun calculateConfidence(possibleSolutions: List<String>, puzzle: String, bestSolution: String): Float {
        // Calculate confidence based on multiple factors
        
        // Factor 1: Number of solutions found
        val solutionCountScore = when {
            possibleSolutions.isEmpty() -> 0.0f
            possibleSolutions.size == 1 -> 1.0f
            possibleSolutions.size <= 3 -> 0.8f
            possibleSolutions.size <= 10 -> 0.6f
            else -> 0.4f
        }
        
        // Factor 2: How much of the puzzle is already revealed
        val revealedLetterCount = puzzle.count { it in 'A'..'Z' }
        val totalLetterCount = puzzle.count { it in 'A'..'Z' || it == '_' || it == '?' }
        val revealedRatio = if (totalLetterCount > 0) revealedLetterCount.toFloat() / totalLetterCount else 0f
        val revealedScore = when {
            revealedRatio >= 0.7 -> 1.0f
            revealedRatio >= 0.5 -> 0.8f
            revealedRatio >= 0.3 -> 0.6f
            revealedRatio >= 0.1 -> 0.4f
            else -> 0.2f
        }
        
        // Factor 3: How well the solution fits the pattern
        val patternFitScore = if (puzzle == bestSolution) {
            1.0f
        } else {
            var matchCount = 0
            for (i in puzzle.indices) {
                if (i < bestSolution.length && (puzzle[i] == bestSolution[i] || puzzle[i] == '_' || puzzle[i] == '?' || puzzle[i] == ' ')) {
                    matchCount++
                }
            }
            matchCount.toFloat() / max(puzzle.length, 1)
        }
        
        // Combine factors with weights
        return (solutionCountScore * 0.4f + revealedScore * 0.3f + patternFitScore * 0.3f)
    }
}
