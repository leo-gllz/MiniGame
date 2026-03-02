package fr.alpha.minigame.games

import fr.alpha.minigame.models.Player

interface MiniGame {
    val title : String

    fun setup(playeList : List<Player>)

    fun handleInput(input: String) : Boolean

    fun getDisplayState(): String

    fun isFinished(): Boolean
}