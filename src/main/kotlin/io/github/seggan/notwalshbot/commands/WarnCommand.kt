package io.github.seggan.notwalshbot.commands

import dev.kord.core.entity.interaction.SubCommand
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.subCommand
import dev.kord.rest.builder.interaction.user
import io.github.seggan.notwalshbot.server.Roles
import io.github.seggan.notwalshbot.server.warnings.Warnings
import io.github.seggan.notwalshbot.util.respondPublic
import java.util.*

object WarnCommand : CommandExecutor("warn", "Warn a user") {

    override val args: CommandBuilder = {
        subCommand("add", "Add a warning to a user") {
            user("user", "The user to warn") { required = true }
            string("reason", "The reason for the warning") { required = true }
        }
        subCommand("remove", "Remove a warning from a user") {
            user("user", "The user to remove the warning from") { required = true }
            string("id", "The ID of the warning to remove") { required = true }
        }
        subCommand("clear", "Clear all warnings from a user") {
            user("user", "The user to clear the warnings from") { required = true }
        }
        subCommand("list", "List all warnings from a user") {
            user("user", "The user to list the warnings from") { required = true }
        }
    }
    override val permission = Roles.moderator

    override suspend fun CommandEvent.execute() {
        val command = interaction.command as SubCommand
        val user = command.users["user"]!!

        when (command.name) {
            "add" -> {
                val reason = command.strings["reason"]
                if (reason == null) {
                    interaction.respondPublic("You must provide a reason for the warning.")
                    return
                }
                val warning = Warnings.add(user.id, reason)
                Warnings.save()
                interaction.respondPublic("User ${user.mention} has been warned: ${warning.reason} (warning ID: `${warning.id}`)")
            }

            "remove" -> {
                val id = command.strings["id"]?.let {
                    try {
                        UUID.fromString(it)
                    } catch (_: IllegalArgumentException) {
                        null
                    }
                }
                if (id == null) {
                    interaction.respondPublic("You must provide a valid warning ID.")
                    return
                }
                Warnings.remove(user.id, id)
                Warnings.save()
                interaction.respondPublic("Warning with ID `$id` has been removed from ${user.mention}.")
            }

            "clear" -> {
                Warnings.clear(user.id)
                Warnings.save()
                interaction.respondPublic("All warnings have been cleared from ${user.mention}.")
            }

            "list" -> {
                val warnings = Warnings[user.id]
                if (warnings.isEmpty()) {
                    interaction.respondPublic("No warnings found for ${user.mention}.")
                    return
                }
                interaction.respondPublic("Warnings for ${user.mention}:\n" + warnings.joinToString("\n") {
                    "`${it.id}`: ${it.reason} (warned at <t:${it.timestamp.epochSeconds}:F>)"
                })
            }
        }
    }
}