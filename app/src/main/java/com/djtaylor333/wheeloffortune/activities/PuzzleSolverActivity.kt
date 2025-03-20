package com.djtaylor333.wheeloffortune.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.djtaylor333.wheeloffortune.R
import com.djtaylor333.wheeloffortune.databinding.ActivityPuzzleSolverBinding
import com.djtaylor333.wheeloffortune.models.PuzzleResult
import com.djtaylor333.wheeloffortune.utils.AdManager
import com.djtaylor333.wheeloffortune.utils.ImageTextExtractor
import com.djtaylor333.wheeloffortune.utils.PuzzleSolver
import com.djtaylor333.wheeloffortune.utils.WheelOfFortuneController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PuzzleSolverActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPuzzleSolverBinding
    private lateinit var adManager: AdManager
    private lateinit var wheelOfFortuneController: WheelOfFortuneController
    private lateinit var imageTextExtractor: ImageTextExtractor
    private lateinit var puzzleSolver: PuzzleSolver
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuzzleSolverBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize AdMob
        adManager = AdManager(this)
        binding.adView.adUnitId = AdManager.getBannerAdUnitId()
        adManager.loadBannerAd(binding.adView)
        
        // Initialize utilities
        wheelOfFortuneController = WheelOfFortuneController(this)
        imageTextExtractor = ImageTextExtractor(this)
        puzzleSolver = PuzzleSolver()
        
        // Get data from intent
        val imageUriString = intent.getStringExtra("IMAGE_URI")
        val clue = intent.getStringExtra("CLUE") ?: ""
        val visibleLetters = intent.getStringExtra("VISIBLE_LETTERS")
        
        // Set clue text
        binding.tvClue.text = clue
        
        if (imageUriString != null) {
            // Process image from URI
            val imageUri = Uri.parse(imageUriString)
            processImageFromUri(imageUri, clue)
        } else if (!visibleLetters.isNullOrEmpty()) {
            // Process directly from entered text
            processPuzzleFromText(visibleLetters, clue)
        }
    }
    
    private fun processImageFromUri(uri: Uri, clue: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvProcessingStatus.text = getString(R.string.processing_image)
        binding.tvProcessingStatus.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                // Extract text from image
                val extractedText = withContext(Dispatchers.IO) {
                    imageTextExtractor.extractTextFromImage(uri)
                }
                
                if (extractedText.isNullOrEmpty()) {
                    binding.tvProcessingStatus.text = getString(R.string.no_text_detected)
                    binding.progressBar.visibility = View.GONE
                    return@launch
                }
                
                // Update UI with extracted text
                binding.tvExtractedText.text = extractedText
                binding.tvExtractedText.visibility = View.VISIBLE
                
                // Solve puzzle
                binding.tvProcessingStatus.text = getString(R.string.solving_puzzle)
                val puzzleResult = withContext(Dispatchers.Default) {
                    puzzleSolver.solvePuzzle(extractedText, clue)
                }
                
                // Display results
                displayPuzzleResults(puzzleResult)
                
            } catch (e: Exception) {
                Toast.makeText(this@PuzzleSolverActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.tvProcessingStatus.visibility = View.GONE
            }
        }
    }
    
    private fun processPuzzleFromText(visibleLetters: String, clue: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvProcessingStatus.text = getString(R.string.solving_puzzle)
        binding.tvProcessingStatus.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                // Update UI with extracted text
                binding.tvExtractedText.text = visibleLetters
                binding.tvExtractedText.visibility = View.VISIBLE
                
                // Solve puzzle
                val puzzleResult = withContext(Dispatchers.Default) {
                    puzzleSolver.solvePuzzle(visibleLetters, clue)
                }
                
                // Display results
                displayPuzzleResults(puzzleResult)
                
            } catch (e: Exception) {
                Toast.makeText(this@PuzzleSolverActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.tvProcessingStatus.visibility = View.GONE
            }
        }
    }
    
    private fun displayPuzzleResults(result: PuzzleResult) {
        // Display solution
        binding.tvSolution.text = result.solution
        binding.tvSolution.visibility = View.VISIBLE
        
        // Display suggested letters
        val suggestedLettersText = result.suggestedLetters.joinToString(", ")
        binding.tvSuggestedLetters.text = suggestedLettersText
        binding.tvSuggestedLetters.visibility = View.VISIBLE
        
        // Show results container
        binding.resultsContainer.visibility = View.VISIBLE
    }
}
