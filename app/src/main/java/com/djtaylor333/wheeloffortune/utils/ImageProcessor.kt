package com.djtaylor333.wheeloffortune.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ImageProcessor(private val context: Context) {
    
    private val textRecognizer: TextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val TAG = "ImageProcessor"
    
    suspend fun processImage(imageUri: Uri): String = suspendCoroutine { continuation ->
        try {
            // Load and preprocess the image
            val bitmap = loadAndPreprocessImage(imageUri)
            
            if (bitmap == null) {
                continuation.resumeWithException(IOException("Failed to load image"))
                return@suspendCoroutine
            }
            
            val image = InputImage.fromBitmap(bitmap, 0)
            
            // Recognize text in the image
            textRecognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Process the text to extract puzzle information
                    val processedText = extractPuzzleText(visionText.text)
                    Log.d(TAG, "Extracted text: $processedText")
                    continuation.resume(processedText)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Text recognition failed", e)
                    continuation.resumeWithException(e)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Image processing failed", e)
            continuation.resumeWithException(e)
        }
    }
    
    private fun loadAndPreprocessImage(imageUri: Uri): Bitmap? {
        return try {
            // Load the image
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            if (originalBitmap == null) {
                Log.e(TAG, "Failed to decode bitmap from URI: $imageUri")
                return null
            }
            
            // Apply preprocessing to enhance text recognition
            // For Wheel of Fortune puzzles, we might want to:
            // 1. Convert to grayscale
            // 2. Increase contrast
            // 3. Apply thresholding to make letters more distinct
            
            // For this prototype, we'll just return the original bitmap
            // In a production app, we would implement more sophisticated preprocessing
            originalBitmap
            
        } catch (e: Exception) {
            Log.e(TAG, "Error loading or preprocessing image", e)
            null
        }
    }
    
    private fun extractPuzzleText(rawText: String): String {
        // Clean and format the extracted text to match Wheel of Fortune puzzle format
        
        // 1. Convert to uppercase
        var processedText = rawText.uppercase()
        
        // 2. Replace common OCR errors
        processedText = processedText
            .replace("0", "O")
            .replace("1", "I")
            .replace("8", "B")
            .replace("5", "S")
        
        // 3. Extract lines that might contain puzzle text
        val lines = processedText.split("\n")
        
        // 4. Look for lines with patterns like "_ _ _ _ _" or "A _ _ _ _" that indicate puzzle board
        val puzzleLines = lines.filter { line ->
            // Look for lines with underscores or mix of letters and underscores/spaces
            line.contains("_") || 
            (line.matches(Regex(".*[A-Z].*")) && line.contains(" "))
        }
        
        // 5. If we found potential puzzle lines, join them
        return if (puzzleLines.isNotEmpty()) {
            puzzleLines.joinToString(" ")
        } else {
            // If no clear puzzle pattern was found, return the best guess
            // Look for the longest line that might be part of the puzzle
            lines.maxByOrNull { it.length }?.takeIf { it.length > 5 } ?: processedText
        }
    }
}
