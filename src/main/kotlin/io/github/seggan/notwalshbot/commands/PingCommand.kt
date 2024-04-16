package io.github.seggan.notwalshbot.commands

import io.github.seggan.notwalshbot.util.respondPublic

object PingCommand : CommandExecutor("ping", "pong") {

    override val args: CommandBuilder = {}

    override suspend fun CommandEvent.execute() {
        interaction.respondPublic("Pong!")
    }
}