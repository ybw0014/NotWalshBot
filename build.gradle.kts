plugins {
    kotlin("jvm") version "1.9.23"
    application
}

group = "io.github.seggan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val sqlVersion = "0.40.1"
dependencies {
    implementation("org.jetbrains.exposed:exposed-core:$sqlVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$sqlVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$sqlVersion")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.1.0")

    implementation("dev.kord:kord-core:0.13.1")
    implementation("io.ktor:ktor-client-java:2.3.10")

    testImplementation(kotlin("test"))
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