package server.games

import server.models.Player

interface MiniGame {
    val title : String

    fun setup(playeList : List<Player>)

    fun handleInput(input: String) : Boolean

    fun getDisplayState(): String

    fun isFinished(): Boolean
}