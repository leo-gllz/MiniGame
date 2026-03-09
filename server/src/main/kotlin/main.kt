import Users.createdAt
import Users.password
import Users.username
import fr.alpha.minigame.AuthRequests
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.http.*
import io.ktor.server.routing.routing
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.* // <-- Doit être celui-là précisément
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import fr.alpha.minigame.AuthResponse
import fr.alpha.minigame.MessageToServer
import fr.alpha.minigame.MessageToUsers
import io.ktor.http.cio.parseResponse
import io.ktor.server.http.content.resources
import io.ktor.server.request.receive
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import java.util.Collections
import java.util.LinkedHashSet
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

var allMessages: String = ""

fun main() {
    initDatabase()

    // On lance le serveur sur le port 8081 de ton PC
    embeddedServer(CIO, port = 8081, host = "0.0.0.0") {
        install(CORS) {
            allowHost("leo-gllz.github.io", schemes = listOf("https"))
            allowHeader(HttpHeaders.ContentType)
            allowMethod(HttpMethod.Options)
            anyHost()
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post) // Utile pour plus tard
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
        }

        install(ContentNegotiation) {
            json()
        }

        install(WebSockets) {
            pingPeriod = 15.seconds
            timeout = 15.seconds
            maxFrameSize = Long.MAX_VALUE
            masking = false

        }

        routing {
            post("/auth") {
                val req = call.receive<AuthRequests>()

                println("${req.username},${req.password},${req.isCreation}")

                val response = transaction {
                    val user = Users.selectAll()
                        .where { Users.username eq req.username}.singleOrNull()

                    val isAlreadyTaken = user != null

                    val isValid = req.username.length in 3..15 && !isAlreadyTaken

                    println("$isValid,${req.username.length}")

                    val errorMessage = when {
                        isAlreadyTaken -> "Ce pseudo est déjà utilisé."
                        req.username.length < 3 -> "Pseudo trop court (min 3)."
                        req.username.length > 15 -> "Pseudo trop long (max 15)."
                        else -> "else du error message donc autre erreure"
                    }

                    if (req.isCreation) {
                        if (!isValid) {
                            AuthResponse(false, req.username, errorMessage)
                        }else {
                            Users.insert {
                                it[username] = req.username
                                it[password] = req.password
                                it[createdAt] = System.currentTimeMillis()
                            }
                            println("Pseudo inséré en base: $req.username")
                            println(Users.selectAll().toList())

                            AuthResponse(true,req.username,"Compte créé !")
                        }
                    } else{
                        if (user != null && user[Users.password] == req.password) {
                            AuthResponse(true, req.username, "Ravi de vous revoir !")
                        }else {
                            AuthResponse(false, req.username, "Identifiants incorrects")
                        }
                    }

                }

                call.respond(response)
            }
        }

        routing {
            val connections = Collections.synchronizedSet<DefaultWebSocketServerSession>(LinkedHashSet())

            webSocket("/msg") {
                println("connection etablie")
                connections += this

                try {
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val jsonText = frame.readText()

                            val inputMessage = Json.decodeFromString<MessageToServer>(jsonText)
                            println("Message de ${inputMessage.username}: ${inputMessage.text}")

                            val response = transaction {
                                allMessages += "${inputMessage.username} - ${inputMessage.text}\n"
                                MessageToUsers(outputMessages = allMessages)
                            }

                            val responseJson = Json.encodeToString(response)

                            val currentSessions = connections.toList()
                            currentSessions.forEach { session ->
                                try {
                                    session.send(Frame.Text(responseJson))
                                } catch (e: Exception) {
                                    // Si l'envoi échoue, on retire cette session morte
                                    connections.remove(session)
                                    println("session mort retiré")
                                }
                            }
                        println("Message diffusé à tous")
                        }
                    }
                }catch (e: Exception) {
                    println("Erreur ou déconnexion : ${e.localizedMessage}")
                } finally {
                    connections -= this
                    println("Connexion fermée et retirée proprement")
                }
            }
        }

    }.start(wait = true)
}