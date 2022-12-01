package io.github.seggan.notwalshbot.commands

import dev.kord.core.entity.interaction.SubCommand
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.subCommand
import io.github.seggan.notwalshbot.db.Tag
import io.github.seggan.notwalshbot.db.Tags
import io.github.seggan.notwalshbot.server.Permission
import io.github.seggan.notwalshbot.util.NEWLINE
import org.jetbrains.exposed.sql.transactions.transaction

object TagCommand : CommandExecutor("tag", "Tag management") {

    override val args: CommandBuilder = {
        subCommand("create", "Creates a tag") {
            string("name", "The name of the tag")
            string("content", "The content of the tag")
        }
        subCommand("delete", "Deletes a tag") {
            string("name", "The name of the tag")
        }
        subCommand("edit", "Edits a tag") {
            string("name", "The name of the tag")
            string("content", "The content of the tag")
        }
        subCommand("raw", "Gets the raw content of a tag") {
            string("name", "The name of the tag")
        }
    }

    override val permission = Permission.STAFF

    private val slashN = "\\\\n".toRegex()

    override suspend fun execute(event: CommandEvent): Unit = with(event) {
        val command = interaction.command as SubCommand
        val tname = command.options["name"]!!.value as String
        val tcontent = (command.options["content"]?.value as? String)?.replace(slashN, "\n")

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
        }
    }
}