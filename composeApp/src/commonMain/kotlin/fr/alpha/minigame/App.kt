package fr.alpha.minigame

import androidx.compose.foundation.layout.*
import androidx.compose.material3.* // On utilise bien le "3" ici
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.alpha.minigame.engine.RoomManager
import fr.alpha.minigame.engine.RoomManager.createRoom
import fr.alpha.minigame.models.Player

@Composable
fun App() {
    var currentScreen by remember {mutableStateOf("LOGIN")}
    var playerName by remember { mutableStateOf("") }
    var roomCode by remember { mutableStateOf("") }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                "LOGIN" -> {
                    LoginScreen(
                        playerName = playerName,
                        roomCode = roomCode,
                        onNameChange = { playerName = it },
                        onCreateRoom = {
                            val newRoom = createRoom()
                            roomCode = newRoom.id
                            newRoom.join(Player(playerName))
                            currentScreen = "ROOM"},
                        onJoinRoom = {
                            val room = RoomManager.getRoom(roomCode)
                            room?.join(Player(playerName))
                            currentScreen = "ROOM"},
                        onCodeChange = { roomCode = it },

                    )
                }

                "ROOM" -> {
                    RoomScreen(
                        playerName = playerName,
                        roomCode = roomCode,
                        onGameLaunch = { currentScreen = "GAME" }
                    )
                }

                "GAME" -> {
                    GameScreen()
                }

            }
        }
    }
}

@Composable
fun LoginScreen(
    playerName: String,
    roomCode: String,
    onNameChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onCreateRoom: () -> Unit,
    onJoinRoom: () -> Unit
){ Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "MiniGame !",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "What's your name ?",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = playerName,
            onValueChange = onNameChange,
            shape = MaterialTheme.shapes.small,
            label = { Text("Name") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onCreateRoom,
            enabled = playerName.isNotBlank()
        ) {
            Text("Create Room")
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = roomCode,
            onValueChange = onCodeChange,
            shape = MaterialTheme.shapes.small,
            label = { Text("Room code") },
            singleLine = true
        )
        Button(
            onClick = onJoinRoom,
            enabled = playerName.isNotBlank() && roomCode.isNotBlank() && roomCode.length == 4 && RoomManager.getRoom(roomCode) != null
        ) {
            Text("Join Room")
        }
    }
}

@Composable
fun RoomScreen(
    playerName: String,
    roomCode: String,
    onGameLaunch: () -> Unit
){
    val room = RoomManager.getRoom(roomCode)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text("Code : $roomCode")

        Spacer(modifier = Modifier.height(20.dp))

        Text("Joueurs présents :", style = MaterialTheme.typography.titleMedium)

        if (room != null) {
            for (player in room.getPlayers()) {
                Text(text = "• $player")
            }
        } else {
            Text("Erreur : Salle introuvable", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = onGameLaunch){
            Text("Lancer le pendu")
        }
    }
}

@Composable
fun GameScreen(){
    TODO()
}