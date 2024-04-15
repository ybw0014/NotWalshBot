package io.github.seggan.notwalshbot.commands

import dev.kord.core.entity.interaction.SubCommand
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.subCommand
import io.github.seggan.notwalshbot.Tags
import io.github.seggan.notwalshbot.server.Roles
import io.github.seggan.notwalshbot.server.Tag
import io.github.seggan.notwalshbot.util.respondPublic

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
        subCommand("alias", "Creates an alias to a tag") {
            string("name", "The name of the alias") { required = true }
            string("target", "The name of the target tag") { required = true }
        }
    }

    override val permission = Roles.addonDeveloper

    override suspend fun CommandEvent.execute() {
        val command = interaction.command as SubCommand
        val tname = command.options["name"]!!.value as String
        val tcontent = command.options["content"]?.value as? String

        when (command.name) {
            "create" -> {
                if (tcontent == null) {
                    respondPublic("You need to provide content")
                    return
                }
                Tags[tname] = Tag.Normal(tname, tcontent)
                respondPublic("Tag created")
            }
            "delete" -> {
                val response = if (Tags.remove(tname) != null) "Tag deleted" else "Tag not found"
                respondPublic(response)
            }
            "edit" -> {
                if (tcontent == null) {
                    respondPublic("You need to provide content")
                    return
                }
                if (Tags[tname] == null) {
                    respondPublic("Tag not found")
                    return
                }
                Tags[tname] = Tag.Normal(tname, tcontent)
                respondPublic("Tag edited")
            }
            "raw" -> {
                val tag = Tags[tname]
                if (tag == null) {
                    respondPublic("Tag not found")
                    return
                }
                var content = tag.content
                content = content.replace("#", "\\#")
                content = content.replace("-", "\\-")
                respondPublic(content)
            }
            "list" -> {
                respondPublic(Tags.values.joinToString("\n") { it.name })
            }
            "alias" -> {
                val target = command.options["target"]!!.value as String
                if (Tags[target] == null) {
                    respondPublic("Target tag not found")
                    return
                }
                Tags[tname] = Tag.Alias(tname, target)
                respondPublic("Alias created")
            }
        }
    }
}