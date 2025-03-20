package com.djtaylor333.wheeloffortune.utils

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.TimeUnit

class ImageAnalyzer(private val listener: (String) -> Unit) : ImageAnalysis.Analyzer {
    
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private var lastAnalyzedTimestamp = 0L
    
    override fun analyze(imageProxy: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        // Process every 2 seconds to avoid overwhelming the system
        if (currentTimestamp - lastAnalyzedTimestamp >= TimeUnit.SECONDS.toMillis(2)) {
            imageProxy.image?.let { mediaImage ->
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                
                textRecognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        // Process the text blocks to extract puzzle board text
                        val extractedText = processPuzzleBoard(visionText.text)
                        listener(extractedText)
                    }
                    .addOnFailureListener { /* Handle failure */ }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
                
                lastAnalyzedTimestamp = currentTimestamp
            } ?: imageProxy.close()
        } else {
            imageProxy.close()
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
