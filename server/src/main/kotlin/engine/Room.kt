package server.engine

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import server.games.MiniGame
import server.models.Player

@Serializable
class Room (val id: String){
    val joinedPlayers = mutableListOf<Player>()

    private var currentPlayerIndex = 0

    @Transient
    var currentGame: MiniGame? = null

    var isStarted : Boolean = false

    fun processInput(input: String) {
        val game = currentGame ?: return
        if (game.isFinished()) return

        val player = getCurrentPlayer() ?: return
        val success = game.handleInput(input)

        if (success) {
            player.score += 10
            println("${player.name} a trouvé une lettre ! (+10 pts)")
        }else{
            currentPlayerIndex = (currentPlayerIndex + 1) % joinedPlayers.size
            println("Raté ! C'est au tour de : ${getCurrentPlayer()?.name}")
        }

        if (game.isFinished()) {
            isStarted = false
            val winner = joinedPlayers.maxByOrNull { it.score }
            println("FIN DE PARTIE")
            println("Le gagnant est ${winner?.name} avec ${winner?.score} points !")
        }
    }

    fun getCurrentPlayer(): Player? = joinedPlayers.getOrNull(currentPlayerIndex)

    fun getPlayers(): List<Player> = joinedPlayers.toList()

    fun join(player: Player) {
        if (!isStarted) {
            joinedPlayers.add(player)
            println("${player.name} a rejoint la salle $id")
        }
    }

    fun leave(player: Player) {
        val removed = joinedPlayers.remove(player)
        if (removed) {
            println("${player.name} a quitté la salle $id.")
        }

        if (joinedPlayers.isEmpty()) {
            isStarted = false
            currentGame = null
            println("La salle $id est maintenant vide.")
        }
    }

    fun startGame(game: MiniGame) {
        if (joinedPlayers.isNotEmpty()) {
            this.currentGame = game
            this.isStarted = true
            game.setup(joinedPlayers)
            println("Le jeu ${game.title} commence dans la salle $id !")
        }
    }
}