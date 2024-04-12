package io.github.seggan.notwalshbot.util

import dev.kord.rest.builder.message.EmbedBuilder

val NEWLINE = "\n".toRegex()
val SLASH_N = "\\\\n".toRegex()

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
        var currentField: EmbedBuilder.Field? = null
        val sb = StringBuilder()
        for (line in lines) {
            if (line.startsWith("##")) {
                if (currentField != null) {
                    currentField.value = currentField.value.trim()
                    fields.add(currentField)
                }
                currentField = EmbedBuilder.Field()
                currentField.name = line.drop(2)
                lines.remove(line)
            } else if (!line.startsWith('#')) {
                var processed = line
                if (processed.startsWith("- ")) {
                    processed = "•" + processed.drop(1)
                }
                if (currentField == null) {
                    sb.appendLine(processed)
                } else {
                    currentField.value = currentField.value + processed + "\n"
                }
                lines.remove(line)
            }
        }
        val extraText = sb.toString().trim()
        if (extraText.isNotEmpty()) {
            description = extraText
        }

        title = lines.firstOrNull { it.startsWith('#') }?.drop(1)
    }
}