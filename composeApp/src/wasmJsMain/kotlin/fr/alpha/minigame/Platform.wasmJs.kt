package fr.alpha.minigame

class WasmPlatform: fr.alpha.minigame.Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): fr.alpha.minigame.Platform = _root_ide_package_.fr.alpha.minigame.WasmPlatform()