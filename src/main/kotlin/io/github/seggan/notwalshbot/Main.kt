package io.github.seggan.notwalshbot

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import io.github.seggan.notwalshbot.commands.CommandEvent
import io.github.seggan.notwalshbot.commands.CommandExecutor
import io.github.seggan.notwalshbot.commands.respondPublic
import io.github.seggan.notwalshbot.filters.ScamFilter
import io.github.seggan.notwalshbot.server.Channels
import io.github.seggan.notwalshbot.server.isAtLeast
import io.github.seggan.notwalshbot.util.DiscordTimestamp
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.jetbrains.exposed.sql.Database
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path
import kotlin.io.path.readText

fun main() = runBlocking {
    println("Starting")
    bot = Kord(Path("token.txt").readText())

    bot.on<ReadyEvent> {
        log("Hello, World!")
        log("Started with session ID `$sessionId` at ${DiscordTimestamp.now()}")
        ScamFilter.updateScamCache(this@runBlocking)
        println("Ready")
    }
    bot.on(consumer = MessageCreateEvent::onMessageSend)

    println("Registering commands")
    val commandMap = CommandExecutor.all.associateBy {
        bot.createGuildChatInputCommand(
            Snowflake(809178621424041997),
            it.name,
            it.description,
            it.args
        ).id
    }
    bot.on<CommandEvent> {
        val command = commandMap[interaction.command.rootId] ?: return@on
        val permission = command.permission
        if (interaction.user.isAtLeast(permission)) {
            command.execute(this)
        } else {
            respondPublic(
                "You do not have permission to use this command. You must have a permission " +
                        "level of at least ${permission.level} (`${permission.name}`)"
            )
        }
    }

    println("Connecting to database")
    Database.connect(
        "jdbc:mariadb://localhost:3306/notwalshbot",
        user = "root",
        password = Path("password.txt").readText()
    )

    println("Logging in")
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