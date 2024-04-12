package io.github.seggan.notwalshbot.commands

import dev.kord.core.entity.interaction.SubCommand
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.subCommand
import io.github.seggan.notwalshbot.db.Tag
import io.github.seggan.notwalshbot.db.Tags
import io.github.seggan.notwalshbot.server.Permission
import io.github.seggan.notwalshbot.util.NEWLINE
import io.github.seggan.notwalshbot.util.SLASH_N
import org.jetbrains.exposed.sql.transactions.transaction

object TagCommand : CommandExecutor("tag", "Tag management") {

    override val args: CommandBuilder = {
        subCommand("create", "Creates a tag") {
            string("name", "The name of the tag") { required = true }
            string("content", "The content of the tag") { required = true }
        }
        subCommand("delete", "Deletes a tag") {
            string("name", "The name of the tag") { required = true }
        }
        subCommand("edit", "Edits a tag") {
            string("name", "The name of the tag") { required = true }
            string("content", "The content of the tag") { required = true }
        }
        subCommand("raw", "Gets the raw content of a tag") {
            string("name", "The name of the tag") { required = true }
        }
        subCommand("list", "Lists all tags")
    }

    override val permission = Permission.ADDON_DEVELOPER

    override suspend fun execute(event: CommandEvent): Unit = with(event) {
        val command = interaction.command as SubCommand
        val tname = command.options["name"]!!.value as String
        val tcontent = (command.options["content"]?.value as? String)?.replace(SLASH_N, "\n")

        when (command.name) {
            "create" -> {
                if (tcontent == null) {
                    respondPublic("You need to provide content")
                    return
                }

                transaction {
                    Tag.new {
                        name = tname
                        content = tcontent
                    }
                }

                respondPublic("Tag created")
            }
            "delete" -> {
                val response = transaction {
                    val tag = Tag.find { Tags.name eq tname }.firstOrNull()
                    tag?.delete()
                    return@transaction if (tag != null) "Tag deleted" else "Tag not found"
                }

                respondPublic(response)
            }
            "edit" -> {
                if (tcontent == null) {
                    respondPublic("You need to provide content")
                    return
                }

                val response = transaction {
                    val tag = Tag.find { Tags.name eq tname }.firstOrNull()
                    tag?.content = tcontent
                    return@transaction if (tag != null) "Tag edited" else "Tag not found"
                }

                respondPublic(response)
            }
            "raw" -> {
                val response = transaction {
                    Tag.find { Tags.name eq tname }.firstOrNull()?.content
                }

                respondPublic(response?.replace(NEWLINE, "\\\\n") ?: "Tag not found")
            }
            "list" -> {
                val response = transaction {
                    Tag.all().joinToString("\n") { it.name }
                }

                respondPublic(response)
            }
        }
    }
}