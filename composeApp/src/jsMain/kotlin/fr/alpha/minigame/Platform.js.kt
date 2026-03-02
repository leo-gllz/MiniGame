package fr.alpha.minigame

class JsPlatform: fr.alpha.minigame.Platform {
    override val name: String = "Web with Kotlin/JS"
}

actual fun getPlatform(): fr.alpha.minigame.Platform = _root_ide_package_.fr.alpha.minigame.JsPlatform()