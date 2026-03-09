import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

// Définition de la table SQL
object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 15).uniqueIndex()
    val password = varchar("password", 50)
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}

// Fonction pour initialiser la DB
fun initDatabase() {
    // Connexion au fichier local
    Database.connect("jdbc:sqlite:./minigame.db", "org.sqlite.JDBC")

    // On crée la table si elle n'existe pas encore
    transaction {
        SchemaUtils.create(Users)
    }
}