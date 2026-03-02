package fr.alpha.minigame

class Greeting {
    private val platform = _root_ide_package_.fr.alpha.minigame.getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}