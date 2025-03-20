package com.djtaylor333.wheeloffortune.utils

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlin.math.max
import kotlin.math.min

/**
 * Utility class for image enhancement to improve text recognition
 */
class ImageEnhancer {
    
    /**
     * Converts a bitmap to grayscale
     */
    fun toGrayscale(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                result.setPixel(x, y, Color.rgb(gray, gray, gray))
            }
        }
        
        return result
    }
    
    /**
     * Increases contrast of the image
     * @param bitmap Input bitmap
     * @param factor Contrast factor (1.0 = no change, 2.0 = double contrast)
     */
    fun increaseContrast(bitmap: Bitmap, factor: Float = 1.5f): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        // Calculate average luminance as the midpoint
        var totalLuminance = 0
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val luminance = (0.299 * pixel.red + 0.587 * pixel.green + 0.114 * pixel.blue).toInt()
                totalLuminance += luminance
            }
        }
        val avgLuminance = totalLuminance / (width * height)
        
        // Apply contrast adjustment
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                
                // Extract channels
                val r = pixel.red
                val g = pixel.green
                val b = pixel.blue
                
                // Apply contrast to each channel
                val newR = (factor * (r - avgLuminance) + avgLuminance).toInt().coerceIn(0, 255)
                val newG = (factor * (g - avgLuminance) + avgLuminance).toInt().coerceIn(0, 255)
                val newB = (factor * (b - avgLuminance) + avgLuminance).toInt().coerceIn(0, 255)
                
                result.setPixel(x, y, Color.rgb(newR, newG, newB))
            }
        }
        
        return result
    }
    
    /**
     * Applies thresholding to create a binary image (black and white only)
     * @param bitmap Input bitmap
     * @param threshold Threshold value (0-255)
     */
    fun applyThreshold(bitmap: Bitmap, threshold: Int = 128): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val luminance = (0.299 * pixel.red + 0.587 * pixel.green + 0.114 * pixel.blue).toInt()
                
                val newColor = if (luminance > threshold) {
                    Color.WHITE
                } else {
                    Color.BLACK
                }
                
                result.setPixel(x, y, newColor)
            }
        }
        
        return result
    }
    
    /**
     * Enhances an image for better text recognition
     * Applies a series of image processing techniques
     */
    fun enhanceForTextRecognition(bitmap: Bitmap): Bitmap {
        // Convert to grayscale
        val grayscale = toGrayscale(bitmap)
        
        // Increase contrast
        val contrasted = increaseContrast(grayscale)
        
        // Apply thresholding for better letter recognition
        // For Wheel of Fortune puzzles, we want to make the letters stand out
        return applyThreshold(contrasted, 140)
    }
}
