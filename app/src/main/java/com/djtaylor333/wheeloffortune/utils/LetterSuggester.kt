package com.djtaylor333.wheeloffortune.utils

import android.util.Log

/**
 * Utility class for letter frequency analysis and suggestion
 */
class LetterSuggester {
    
    private val TAG = "LetterSuggester"
    
    // Letter frequency in English language (from most to least common)
    private val letterFrequency = mapOf(
        'E' to 12.02f,
        'T' to 9.10f,
        'A' to 8.12f,
        'O' to 7.68f,
        'I' to 7.31f,
        'N' to 6.95f,
        'S' to 6.28f,
        'R' to 6.02f,
        'H' to 5.92f,
        'D' to 4.32f,
        'L' to 3.98f,
        'U' to 2.88f,
        'C' to 2.71f,
        'M' to 2.61f,
        'F' to 2.30f,
        'Y' to 2.11f,
        'W' to 2.09f,
        'G' to 2.03f,
        'P' to 1.82f,
        'B' to 1.49f,
        'V' to 1.11f,
        'K' to 0.69f,
        'X' to 0.17f,
        'Q' to 0.11f,
        'J' to 0.10f,
        'Z' to 0.07f
    )
    
    // Common consonants to guess in Wheel of Fortune (R, S, T, L, N)
    private val commonConsonants = listOf('R', 'S', 'T', 'L', 'N')
    
    // Common vowels to guess in Wheel of Fortune (A, E, I, O, U)
    private val commonVowels = listOf('A', 'E', 'I', 'O', 'U')
    
    /**
     * Suggest the next best letters to guess based on the current puzzle state
     * @param puzzleText Current state of the puzzle with revealed letters and blanks
     * @param category Optional category to help with suggestions
     * @param revealedLetters Letters that have already been revealed
     * @return List of suggested letters in order of recommendation
     */
    fun suggestNextLetters(
        puzzleText: String, 
        category: String = "", 
        revealedLetters: Set<Char> = emptySet()
    ): List<Char> {
        Log.d(TAG, "Suggesting letters for puzzle: $puzzleText, category: $category")
        Log.d(TAG, "Already revealed letters: $revealedLetters")
        
        // Get all available letters (not yet revealed)
        val availableLetters = letterFrequency.keys.filter { it !in revealedLetters }.toMutableList()
        
        // Calculate scores for each available letter
        val letterScores = mutableMapOf<Char, Float>()
        
        for (letter in availableLetters) {
            // Base score from letter frequency
            var score = letterFrequency[letter] ?: 0f
            
            // Adjust score based on letter type (vowel or consonant)
            if (letter in commonVowels) {
                // If no vowels revealed yet, prioritize vowels
                if (commonVowels.none { it in revealedLetters }) {
                    score *= 1.5f
                }
            } else if (letter in commonConsonants) {
                // Common consonants get a boost
                score *= 1.3f
            }
            
            // Adjust score based on category-specific knowledge
            score = adjustScoreByCategory(letter, category, score)
            
            letterScores[letter] = score
        }
        
        // Sort letters by score (descending)
        val sortedLetters = letterScores.entries
            .sortedByDescending { it.value }
            .map { it.key }
        
        Log.d(TAG, "Suggested letters: ${sortedLetters.take(5)}")
        
        // Return top 5 suggested letters
        return sortedLetters.take(5)
    }
    
    /**
     * Adjust letter score based on category-specific knowledge
     */
    private fun adjustScoreByCategory(letter: Char, category: String, baseScore: Float): Float {
        var adjustedScore = baseScore
        
        // Apply category-specific adjustments
        when (category.uppercase()) {
            "PHRASE" -> {
                // Common letters in phrases
                if (letter in listOf('T', 'H', 'E', 'A', 'N', 'D', 'O', 'F')) {
                    adjustedScore *= 1.2f
                }
            }
            "PERSON" -> {
                // Common letters in person names
                if (letter in listOf('J', 'K', 'M', 'P', 'W')) {
                    adjustedScore *= 1.2f
                }
            }
            "PLACE" -> {
                // Common letters in place names
                if (letter in listOf('C', 'L', 'N', 'S', 'T')) {
                    adjustedScore *= 1.2f
                }
            }
            "FOOD & DRINK" -> {
                // Common letters in food and drink names
                if (letter in listOf('C', 'K', 'P', 'R', 'S')) {
                    adjustedScore *= 1.2f
                }
            }
            "AROUND THE HOUSE" -> {
                // Common letters in household items
                if (letter in listOf('B', 'C', 'K', 'T', 'R')) {
                    adjustedScore *= 1.2f
                }
            }
        }
        
        return adjustedScore
    }
    
    /**
     * Get the standard free letters in Wheel of Fortune final round (R, S, T, L, N, E)
     */
    fun getFinalRoundFreeLetters(): List<Char> {
        return listOf('R', 'S', 'T', 'L', 'N', 'E')
    }
}
