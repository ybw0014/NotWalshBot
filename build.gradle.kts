import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
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

    implementation("dev.kord:kord-core:0.8.0-M17")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.beust:klaxon:5.6")

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("io.github.seggan.notwalshbot.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}