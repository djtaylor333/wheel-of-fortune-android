package com.djtaylor333.wheeloffortune.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.djtaylor333.wheeloffortune.models.PuzzleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Main controller class that coordinates image processing and puzzle solving
 */
class WheelOfFortuneController(private val context: Context) {
    
    private val imageProcessor = ImageProcessor(context)
    private val imageEnhancer = ImageEnhancer()
    private val puzzleSolver = PuzzleSolver()
    private val TAG = "WheelOfFortuneController"
    
    /**
     * Process an image and solve the Wheel of Fortune puzzle
     * @param imageUri URI of the image to process
     * @param clue Optional clue/category for the puzzle
     * @return PuzzleResult containing solution and suggested letters
     */
    suspend fun processImageAndSolvePuzzle(imageUri: Uri, clue: String): PuzzleResult {
        return withContext(Dispatchers.Default) {
            try {
                // Step 1: Process the image to extract text
                Log.d(TAG, "Processing image: $imageUri")
                val extractedText = imageProcessor.processImage(imageUri)
                
                if (extractedText.isBlank()) {
                    Log.w(TAG, "No text extracted from image")
                    return@withContext PuzzleResult(
                        solution = "No text detected in image",
                        suggestedLetters = listOf(),
                        confidence = 0.0f
                    )
                }
                
                Log.d(TAG, "Extracted text: $extractedText")
                
                // Step 2: Solve the puzzle using the extracted text and clue
                val result = puzzleSolver.solvePuzzle(extractedText, clue)
                Log.d(TAG, "Puzzle solved. Solution: ${result.solution}")
                
                return@withContext result
                
            } catch (e: Exception) {
                Log.e(TAG, "Error processing image and solving puzzle", e)
                return@withContext PuzzleResult(
                    solution = "Error: ${e.message}",
                    suggestedLetters = listOf(),
                    confidence = 0.0f
                )
            }
        }
    }
    
    /**
     * Solve a puzzle directly from text input
     * @param puzzleText Text representation of the puzzle (e.g., "_ _ _ _ _   _ _ _ _")
     * @param clue Optional clue/category for the puzzle
     * @return PuzzleResult containing solution and suggested letters
     */
    suspend fun solvePuzzleFromText(puzzleText: String, clue: String): PuzzleResult {
        return withContext(Dispatchers.Default) {
            try {
                val result = puzzleSolver.solvePuzzle(puzzleText, clue)
                Log.d(TAG, "Puzzle solved from text. Solution: ${result.solution}")
                return@withContext result
            } catch (e: Exception) {
                Log.e(TAG, "Error solving puzzle from text", e)
                return@withContext PuzzleResult(
                    solution = "Error: ${e.message}",
                    suggestedLetters = listOf(),
                    confidence = 0.0f
                )
            }
        }
    }
}
