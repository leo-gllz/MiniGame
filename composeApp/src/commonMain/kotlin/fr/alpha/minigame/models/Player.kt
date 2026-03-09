package fr.alpha.minigame.models

import kotlinx.serialization.Serializable
import minigame.composeapp.generated.resources.Res

@Serializable
data class Player (
    val name : String,
    var score : Int = 0
    )