package io.github.seggan.notwalshbot.util

import dev.kord.rest.builder.message.EmbedBuilder

val NEWLINE = "\n".toRegex()

fun parseMd(string: String): EmbedBuilder {
    val lines = string.split(NEWLINE).toMutableList()
    return EmbedBuilder().apply {
        val footerMd = lines.firstOrNull { it.startsWith("###") }
        if (footerMd != null) {
            footer {
                text = footerMd.drop(3)
            }
            lines.remove(footerMd)
        }
        var currentField = EmbedBuilder.Field()
        for (line in lines) {
            if (line.startsWith("##")) {
                currentField.value = currentField.value.trim()
                fields.add(currentField)
                currentField = EmbedBuilder.Field()
                currentField.name = line.drop(2)
            } else if (!line.startsWith('#')) {
                var processed = line
                if (processed.startsWith("- ")) {
                    processed = "•" + processed.drop(1)
                }
                currentField.value = currentField.value + processed + "\n"
            }
        }
        currentField.value = currentField.value.trim()
        if (currentField.value.isNotEmpty()) {
            fields.add(currentField)
        }

        title = lines.firstOrNull { it.startsWith('#') }?.drop(1)
    }
}