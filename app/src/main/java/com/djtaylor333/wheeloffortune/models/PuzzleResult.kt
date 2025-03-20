package com.djtaylor333.wheeloffortune.models

data class PuzzleResult(
    val solution: String,
    val suggestedLetters: List<Char>,
    val confidence: Float
)
