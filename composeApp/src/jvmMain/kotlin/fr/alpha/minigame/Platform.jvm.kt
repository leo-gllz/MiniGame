package fr.alpha.minigame

class JVMPlatform: fr.alpha.minigame.Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): fr.alpha.minigame.Platform = _root_ide_package_.fr.alpha.minigame.JVMPlatform()