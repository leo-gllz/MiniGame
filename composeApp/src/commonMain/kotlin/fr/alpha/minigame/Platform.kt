package fr.alpha.minigame

interface Platform {
    val name: String
}

expect fun getPlatform(): fr.alpha.minigame.Platform