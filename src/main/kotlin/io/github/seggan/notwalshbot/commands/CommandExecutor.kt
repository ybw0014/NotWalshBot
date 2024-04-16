package io.github.seggan.notwalshbot.commands

import dev.kord.common.entity.Snowflake
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder

typealias CommandEvent = GuildChatInputCommandInteractionCreateEvent
typealias CommandBuilder = ChatInputCreateBuilder.() -> Unit

abstract class CommandExecutor(val name: String, val description: String) {

    companion object {
        val all = mutableSetOf(TagCommand, PingCommand, ReportCommand, WarnCommand)
    }

    abstract val args: CommandBuilder
    open val permission: Snowflake? = null

    abstract suspend fun CommandEvent.execute()
}