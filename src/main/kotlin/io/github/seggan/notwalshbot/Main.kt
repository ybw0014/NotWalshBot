package io.github.seggan.notwalshbot

import dev.kord.core.Kord
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intents
import dev.kord.gateway.MessageCreate
import dev.kord.gateway.PrivilegedIntent
import io.github.seggan.notwalshbot.filters.ScamFilter
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.jetbrains.exposed.sql.Database
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path
import kotlin.io.path.readText

fun main() = runBlocking {
    bot = Kord(Path("token.txt").readText())

    bot.on<ReadyEvent> {
        log("Hello, World!")
        log("Started with session ID `$sessionId` at ${DiscordTimestamp.now()}")
        ScamFilter.updateScamCache(this@runBlocking)
    }
    bot.on(consumer = MessageCreateEvent::onMessageSend)

    Database.connect(
        "jdbc:mariadb://localhost:3306/notwalshbot",
        user = "root",
        password = Path("password.txt").readText()
    )

    bot.login {
        @OptIn(PrivilegedIntent::class)
        intents = Intents.all
    }
}

lateinit var bot: Kord
val httpClient = OkHttpClient()

val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm:ss a z")
    .withZone(ZoneId.of("America/New_York"))

suspend fun log(message: String) {
    Channels.BOT_TESTING.get().createMessage(message)
}