package fr.alpha.minigame

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.alpha.minigame.engine.RoomManager
import fr.alpha.minigame.engine.Room

@Composable
fun App() {
    MaterialTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("MiniGame") }) }
        ) { innerPadding ->
            val roomManager = remember { RoomManager() }
            var currentRoom by remember { mutableStateOf<Room?>(null) }

            // --- NOUVEAU : État pour le pseudo ---
            var playerName by remember { mutableStateOf("") }

            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (currentRoom == null) {
                    // --- ÉCRAN D'ACCUEIL ---
                    Text("Configuration du joueur", style = MaterialTheme.typography.h5)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Champ de saisie pour le nom
                    OutlinedTextField(
                        value = playerName,
                        onValueChange = { playerName = it },
                        label = { Text("Ton Pseudo") },
                        singleLine = true,
                        modifier = Modifier.width(280.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            // 1. On crée le joueur (Assure-toi que Player est importé)
                            val host = Player(id = "p1", name = playerName)
                            // 2. On crée la salle avec ce joueur
                            currentRoom = roomManager.createRoom(host)
                        },
                        // Le bouton est désactivé si le nom est trop court
                        enabled = playerName.isNotBlank() && playerName.length > 2,
                        modifier = Modifier.width(280.dp).height(50.dp)
                    ) {
                        Text("CRÉER ET REJOINDRE")
                    }

                } else {
                    // --- ÉCRAN LOBBY ---
                    Text("Salle : ${currentRoom?.id}")
                    Text("Joueur : ${playerName} (Host)")
                    // ... reste du code de la salle
                }
            }
        }
    }
}