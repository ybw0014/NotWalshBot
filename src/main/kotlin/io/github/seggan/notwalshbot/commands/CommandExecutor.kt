package io.github.seggan.notwalshbot.commands

import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.response.PublicMessageInteractionResponse
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import io.github.seggan.notwalshbot.server.Permission

typealias CommandEvent = GuildChatInputCommandInteractionCreateEvent
typealias CommandBuilder = ChatInputCreateBuilder.() -> Unit

abstract class CommandExecutor(val name: String, val description: String) {

    companion object {
        val all = mutableSetOf(TagCommand, PingCommand)
    }

    abstract val args: CommandBuilder
    open val permission: Permission = Permission.EVERYONE

    abstract suspend fun execute(event: CommandEvent)
}

suspend fun CommandEvent.respondPublic(message: String): PublicMessageInteractionResponse {
    return this.interaction.deferPublicResponse().respond { content = message }
}