plugins {
    // Utilise l'ID complet du plugin sans la version
    id("org.jetbrains.kotlin.jvm")
    id("io.ktor.plugin") version "3.0.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

repositories {
    mavenCentral()
}

ktor {
    fatJar{
        archiveFileName.set("server.jar")
    }
}

dependencies {
    implementation("io.ktor:ktor-server-partial-content:2.3.12")
    val ktor_version = "3.0.0"

    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cors-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cio-jvm:$ktor_version")

    implementation("io.ktor:ktor-server-websockets-jvm:${ktor_version}")

    // Si tu as de la sérialisation JSON
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")

    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation(project(":composeApp"))

    val exposed_version = "0.50.0" // Version stable actuelle
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.xerial:sqlite-jdbc:3.45.2.0")
}

application {
    mainClass.set("MainKt")
}