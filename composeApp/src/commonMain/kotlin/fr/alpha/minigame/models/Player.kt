package fr.alpha.minigame.models

import minigame.composeapp.generated.resources.Res

data class Player (
    val name : String,
    var score : Int = 0
    )