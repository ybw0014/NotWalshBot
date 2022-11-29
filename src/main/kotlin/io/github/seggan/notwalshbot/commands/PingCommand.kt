package io.github.seggan.notwalshbot.commands

import dev.kord.core.behavior.interaction.response.respond

object PingCommand : CommandExecutor("ping", "pong") {

    override val args: CommandBuilder = {}

    override suspend fun execute(event: CommandEvent): Unit = with(event) {
        val response = interaction.deferPublicResponse()
        response.respond {
            content = "Pong!"
        }
    }
}