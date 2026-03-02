package fr.alpha.minigame

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "MiniGame",
    ) {
        _root_ide_package_.fr.alpha.minigame.App()
    }
}