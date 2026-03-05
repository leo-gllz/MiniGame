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

fun main() {
    // On lance le serveur sur le port 8080 de ton PC
    embeddedServer(CIO, port = 8081, host = "0.0.0.0") {
        install(CORS) {
            anyHost()
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post) // Utile pour plus tard
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
        }
        routing {
            // Route de test simple
            // Dans Main.kt (Serveur)

// Simulation d'une base de données de pseudos déjà pris
            var pseudosExistants = mutableListOf("Admin", "Lg", "Zelda")

// ... dans ton routing ...
            get("/check-pseudo/{pseudo}") {
                val startTime = System.currentTimeMillis()
                val name = call.parameters["pseudo"] ?: ""

                // Logique de vérification
                val isAlreadyTaken = pseudosExistants.contains(name)
                val isValid = name.length in 3..15 && !isAlreadyTaken

                val message = when {
                    isAlreadyTaken -> "Ce pseudo est déjà utilisé."
                    name.length < 3 -> "Trop court (min 3)."
                    name.length > 15 -> "Trop long (max 15)."
                    else -> "Pseudo disponible !"
                }

                val endTime = System.currentTimeMillis()
                val executionTime = endTime - startTime

                // Pour l'instant on renvoie un texte formaté simple (ou du JSON si tu es prêt)
                // Format : VALIDATION | PSEUDO | HEURE | TEMPS_EXEC
                val heure = java.time.LocalTime.now().toString().substring(0, 8)
                if (isValid) {
                    pseudosExistants.add(name)
                    println("Pseudo ajouter : $name, liste des pseudos : $pseudosExistants ")
                }
                call.respondText("$isValid|$name|$message|$heure|${executionTime}ms")
            }
        }
    }.start(wait = true)
}