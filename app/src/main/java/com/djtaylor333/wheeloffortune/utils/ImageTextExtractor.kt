package com.djtaylor333.wheeloffortune.utils

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ImageTextExtractor(private val context: Context) {
    
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    
    suspend fun extractTextFromImage(imageUri: Uri): String = suspendCoroutine { continuation ->
        try {
            val image = InputImage.fromFilePath(context, imageUri)
            
            textRecognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Process the text blocks to extract puzzle board text
                    val extractedText = processPuzzleBoard(visionText.text)
                    continuation.resume(extractedText)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }
    
    private fun processPuzzleBoard(rawText: String): String {
        // Clean up the extracted text to focus on the puzzle board
        // This is a simplified version - in a real app, this would be more sophisticated
        
        // Remove extra spaces and convert to uppercase
        var processedText = rawText.trim().uppercase()
        
        // Replace common OCR errors
        processedText = processedText.replace("0", "O")
            .replace("1", "I")
            .replace("8", "B")
            .replace("5", "S")
            
        // Extract only alphabetic characters and spaces/underscores
        val puzzlePattern = "[A-Z _-]+".toRegex()
        val matches = puzzlePattern.findAll(processedText)
        
        // Join all matching parts
        return matches.joinToString(" ") { it.value }
    }
}
