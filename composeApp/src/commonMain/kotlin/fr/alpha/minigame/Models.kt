package fr.alpha.minigame

import kotlinx.serialization.Serializable
import minigame.composeapp.generated.resources.Res

@Serializable
data class AuthRequests(
    val username: String,
    val password: String,
    val isCreation: Boolean
)

@Serializable
data class AuthResponse(
    val isAccepted: Boolean,
    val username: String,
    val serverMessage: String
)

@Serializable
data class MessageToServer (
    val username: String,
    val text: String
)

@Serializable
data class MessageToUsers(
    val outputMessages: String
) {}