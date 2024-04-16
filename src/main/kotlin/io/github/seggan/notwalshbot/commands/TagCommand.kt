package io.github.seggan.notwalshbot.commands

import dev.kord.common.entity.TextInputStyle
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.entity.interaction.SubCommand
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.interaction.subCommand
import io.github.seggan.notwalshbot.server.Roles
import io.github.seggan.notwalshbot.server.tags.Tag
import io.github.seggan.notwalshbot.server.tags.Tags
import io.github.seggan.notwalshbot.util.onSubmit
import io.github.seggan.notwalshbot.util.respondPublic

object TagCommand : CommandExecutor("tag", "Tag management") {

    override val args: CommandBuilder = {
        subCommand("create", "Creates a tag") {
            string("name", "The name of the tag")
        }
        subCommand("delete", "Deletes a tag") {
            string("name", "The name of the tag") { required = true }
        }
        subCommand("edit", "Edits a tag") {
            string("name", "The name of the tag") {
                required = true
                for (tag in Tags) {
                    choice(tag.name, tag.name)
                }
            }
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
        val name = command.strings["name"]

        when (command.name) {
            "create" -> {
                interaction.modal("Create Tag", "create_tag") {
                    actionRow {
                        textInput(TextInputStyle.Short, "name", "Name") {
                            required = true
                            value = name
                            allowedLength = 1..32
                        }
                    }
                    actionRow {
                        textInput(TextInputStyle.Paragraph, "content", "Content") {
                            required = true
                        }
                    }
                    onSubmit {
                        val newName = interaction.textInputs["name"]!!.value!!
                        val content = interaction.textInputs["content"]!!.value!!
                        Tags[newName] = Tag.Normal(newName, content)
                        Tags.save()
                        interaction.respondPublic("Tag `$newName` created")
                    }
                }
            }

            "delete" -> {
                val tag = Tags.remove(name!!)
                if (tag == null) {
                    interaction.respondPublic("Tag `$name` does not exist")
                    return
                }
                for (t in Tags) {
                    if (t is Tag.Alias && t.target == name) {
                        Tags.remove(t.name)
                    }
                }
                Tags.save()
                interaction.respondPublic("Tag `$name` and aliases deleted")
            }

            "edit" -> {
                interaction.modal("Edit Tag", "edit_tag") {
                    actionRow {
                        textInput(TextInputStyle.Short, "name", "Name") {
                            required = true
                            value = name
                            allowedLength = 1..32
                        }
                    }
                    actionRow {
                        textInput(TextInputStyle.Paragraph, "content", "Content") {
                            required = true
                            value = Tags[name!!]?.content
                        }
                    }
                    onSubmit {
                        val newName = interaction.textInputs["name"]!!.value!!
                        val content = interaction.textInputs["content"]!!.value!!
                        Tags.remove(name!!)
                        Tags[newName] = Tag.Normal(newName, content)
                        Tags.save()
                        interaction.respondPublic("Tag `$name` edited to `$newName`")
                    }
                }
            }

            "list" -> {
                val sb = StringBuilder()
                for (tag in Tags) {
                    if (tag is Tag.Normal) {
                        sb.append("- `").append(tag.name).append("`")
                        val aliases = Tags.filterIsInstance<Tag.Alias>().filter { it.target == tag.name }
                        if (aliases.isNotEmpty()) {
                            sb.append(" (aliases: ")
                            sb.append(aliases.joinToString(", ") { "`${it.name}`" })
                            sb.append(')')
                        }
                        sb.appendLine()
                    }
                }
                interaction.respondPublic(sb.toString())
            }

            "alias" -> {
                val target = command.strings["target"]!!
                Tags[name!!] = Tag.Alias(name, target)
                Tags.save()
                interaction.respondPublic("Alias `$name` created for `$target`")
            }
        }
    }
}