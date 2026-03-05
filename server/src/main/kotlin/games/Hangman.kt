package server.games

import server.models.Player
import kotlin.text.iterator

class Hangman : MiniGame {
    private var wordToGuess :String = "L'ECRITURE KOTLIN ARC-EN-CIEL"
    private var guessedLetters : MutableSet<Char> = mutableSetOf()
    private var lives : Int = 7

    override val title: String = "Le Pendu"

    override fun setup(playeList: List<Player>) {
        lives = 7
        guessedLetters = mutableSetOf()
    }

    override fun handleInput(input: String): Boolean {
        val letter = input.uppercase().firstOrNull() ?: return false

        return if (letter in wordToGuess && letter !in guessedLetters) {
            guessedLetters.add(letter)
            true
        } else {
            if (letter !in wordToGuess) lives--
            false
        }
    }

    override fun getDisplayState(): String {
        var wordState : String = ""
        val specialChars = listOf(' ', '-','\'','.', ',')

        for (letter in wordToGuess) {
            if (letter in guessedLetters || letter in specialChars){
                wordState += letter
            }else{
                wordState += "_"
            }
        }
        return wordState
    }

    override fun isFinished(): Boolean {
        return lives <= 0 || wordToGuess.all { it in guessedLetters }
    }
}
