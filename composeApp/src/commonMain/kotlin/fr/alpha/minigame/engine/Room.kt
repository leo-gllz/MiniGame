package fr.alpha.minigame.engine

import kotlinx.serialization.Serializable
import fr.alpha.minigame.models.Player

@Serializable
data class Room(
    val id: String,
    val isStarted: Boolean = false,
    val joinedPlayers: List<Player> = emptyList()
)