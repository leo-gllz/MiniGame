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
        // Scaffold crée la structure de base (barre de titre + contenu)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("MiniGame - Pendu") },
                    elevation = 4.dp
                )
            }
        ) { innerPadding ->
            // On initialise le moteur de jeu
            val roomManager = remember { RoomManager() }

            // État pour savoir si on affiche le menu ou la salle
            var currentRoom by remember { mutableStateOf<Room?>(null) }

            // Conteneur principal qui utilise le padding du Scaffold
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (currentRoom == null) {
                    // --- ÉCRAN : MENU PRINCIPAL ---
                    Text(
                        text = "Bienvenue dans le Lobby",
                        style = MaterialTheme.typography.h4
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            // Appel de ton moteur pour créer une salle
                            currentRoom = roomManager.createRoom()
                        },
                        modifier = Modifier.width(250.dp).height(50.dp)
                    ) {
                        Text("CRÉER UNE NOUVELLE SALLE")
                    }

                } else {
                    // --- ÉCRAN : DANS LA SALLE ---
                    Card(
                        elevation = 8.dp,
                        modifier = Modifier.padding(16.dp).widthIn(max = 400.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Code de la salle :", style = MaterialTheme.typography.overline)

                            // Affiche l'ID généré par ton RoomManager
                            Text(
                                text = currentRoom?.id ?: "Erreur",
                                style = MaterialTheme.typography.h2,
                                color = MaterialTheme.colors.primary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            Text(
                                "Attente d'un adversaire...",
                                modifier = Modifier.padding(top = 8.dp),
                                style = MaterialTheme.typography.caption
                            )

                            Spacer(modifier = Modifier.height(40.dp))

                            TextButton(onClick = { currentRoom = null }) {
                                Text("RETOUR AU MENU", color = MaterialTheme.colors.error)
                            }
                        }
                    }
                }
            }
        }
    }
}