package fr.alpha.minigame

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("MiniGame en Ligne !", style = MaterialTheme.typography.h4)
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { /* Rien pour l'instant */ }) {
                Text("C'EST EN LIGNE !")
            }
        }
    }
}