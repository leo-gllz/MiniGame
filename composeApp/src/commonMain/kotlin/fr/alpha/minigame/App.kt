package fr.alpha.minigame

import androidx.compose.foundation.layout.*
import androidx.compose.material3.* // On utilise bien le "3" ici
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    // MaterialTheme de Material 3
    MaterialTheme {
        // Surface permet de mettre un fond de couleur propre (standard en M3)
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "MiniGame en Ligne !",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(onClick = { println("Clic détecté !") }) {
                    Text("C'EST EN LIGNE !")
                }
            }
        }
    }
}