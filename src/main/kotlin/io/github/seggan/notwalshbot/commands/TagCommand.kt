package io.github.seggan.notwalshbot.commands

import dev.kord.rest.builder.interaction.GlobalChatInputCreateBuilder

object TagCommand : CommandExecutor("tag", "Tag management") {
    override val args: CommandBuilder = {

    }

    override suspend fun execute(event: CommandEvent) {
        TODO("Not yet implemented")
    }
}