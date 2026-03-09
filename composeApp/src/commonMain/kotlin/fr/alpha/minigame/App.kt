package fr.alpha.minigame

import androidx.compose.foundation.layout.*
import androidx.compose.material3.* // On utilise bien le "3" ici
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.http.*
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString

val client = HttpClient {
    install(ContentNegotiation) {
        json()
    }

    install(WebSockets)
}

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf("AuthScreen") }
    var username by remember { mutableStateOf("") }

    when (currentScreen) {
        "AuthScreen" -> {
            AuthScreen(onAuthSuccess = {
                name -> username = name
                currentScreen = "TestValidation"
            })
        }

        "TestValidation" -> {
            TestMessages(username)
        }
    }
}

@Composable
fun AuthScreen(
    onAuthSuccess: (String) -> Unit
) {
    // État pour savoir si on est en mode "Création" ou "Connexion"
    var isRegisterMode by remember { mutableStateOf(false) }

    // Champs de saisie
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Message d'erreur ou de succès
    var feedbackMessage by remember { mutableStateOf("Default") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isRegisterMode) "Créer un compte" else "Connexion",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Champ Pseudo
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Pseudo") },
            singleLine = true,
            modifier = Modifier.width(300.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Champ Mot de passe
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.width(300.dp)
        )

        // CHAMP DYNAMIQUE : Apparaît seulement en mode Inscription
        if (isRegisterMode) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmer le mot de passe") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.width(300.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // BOUTON PRINCIPAL (Action)
        Button(
            enabled = username.isNotBlank()
                    && password.isNotBlank()
                    && (!isRegisterMode || password == confirmPassword),
            onClick = {

                if (isRegisterMode && password != confirmPassword) {
                    feedbackMessage = "Les mots de passe ne correspondent pas !"
                } else {
                    scope.launch {
                        try {
                            // Envoi de la requête POST
                            val response: AuthResponse = client.post("${GameConfig.SERVER_URL}/auth") {
                                contentType(ContentType.Application.Json)
                                setBody(
                                    AuthRequests(
                                        username = username,
                                        password = password,
                                        isCreation = isRegisterMode // Envoie true si on est en mode inscription
                                    )
                                )
                            }.body()

                            // Traitement de la réponse
                            feedbackMessage = response.serverMessage

                            if (response.isAccepted) {
                                // Si c'est bon, on appelle la fonction pour changer d'écran
                                onAuthSuccess(username)
                            }

                        } catch (e: Exception) {
                            feedbackMessage = "Erreur réseau : ${e.message}"
                        }
                    }
                }
            },
            modifier = Modifier.width(300.dp)
        ) {
            Text(if (isRegisterMode) "S'inscrire" else "Se connecter")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // BOUTON SECONDAIRE (Switch de mode)
        TextButton(
            onClick = {
                isRegisterMode = !isRegisterMode
                feedbackMessage = "DefaultSec" // On reset l'erreur au switch
            }
        ) {
            Text(
                if (isRegisterMode) "Déjà un compte ? Se connecter"
                else "Pas de compte ? Créer un compte"
            )
        }

        if (feedbackMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(feedbackMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun TestMessages(username: String = "") {
    var inputMessage by remember { mutableStateOf("") }
    var messagesDisplay by remember { mutableStateOf("Connexion en cours...") }

    var session by remember { mutableStateOf<DefaultClientWebSocketSession?>(null) }
    val scope = rememberCoroutineScope()

LaunchedEffect(Unit) {
    client.webSocket(method = HttpMethod.Get, host = "${GameConfig.IP}", port = 8081, path = "/msg") {
        session = this
        try {
            for (frame in incoming) {
                try { // <--- DEUXIÈME TRY ICI
                    if (frame is Frame.Text) {
                        val response = Json.decodeFromString<MessageToUsers>(frame.readText())
                        messagesDisplay = response.outputMessages
                    }
                } catch (e: Exception) {
                    println("Erreur sur UN message, mais on continue d'écouter")
                    messagesDisplay = "frame is Frame.txt qui a foiré"
                }
            }
        } catch (e: Exception) {
            messagesDisplay = "Connexion perdue : ${e.message}"
            session = null
        }
    }
}


    Column (
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Messages tester",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = username,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = inputMessage,
            onValueChange = { inputMessage = it },
            label = { Text("Message") },
            //singleLine = true,
            modifier = Modifier.width(300.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val sessionActive = session
                        if (sessionActive != null && sessionActive.outgoing.isClosedForSend.not()) {
                            val msgJson = Json.encodeToString(MessageToServer(username, inputMessage))
                            session?.send(Frame.Text(msgJson))
                            inputMessage = "" // On vide le champ après envoi
                        }else{
                            messagesDisplay = "\"Erreur : Non connecté au serveur."
                        }
                    } catch (e: Exception) {
                        messagesDisplay = "Erreur d'envoi : ${e.message}"
                    }
                }
            },
            enabled = session != null && inputMessage.isNotBlank(),
            modifier = Modifier.width(300.dp)
        ){
            Text("Envoyer")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(messagesDisplay)
    }
}

    /*
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
}*/