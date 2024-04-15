plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    application
}

group = "io.github.seggan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val sqlVersion = "0.40.1"
dependencies {
    implementation("dev.kord:kord-core:0.13.1")
    implementation("io.ktor:ktor-client-java:2.3.10")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
}

application {
    mainClass.set("io.github.seggan.notwalshbot.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}