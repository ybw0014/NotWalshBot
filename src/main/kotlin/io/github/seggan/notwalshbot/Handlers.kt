package io.github.seggan.notwalshbot

import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.event.message.MessageCreateEvent
import io.github.seggan.notwalshbot.db.Tag
import io.github.seggan.notwalshbot.db.Tags
import io.github.seggan.notwalshbot.filters.MessageFilter
import io.github.seggan.notwalshbot.util.parseMd
import org.jetbrains.exposed.sql.transactions.transaction

suspend fun MessageCreateEvent.onMessageSend() {
    if (message.author?.isBot == true) return

    for (filter in MessageFilter.all) {
        if (filter.test(message)) {
            filter.act(message)
            if (filter.delete) {
                message.delete()
            }
            return
        }
    }

    val content = message.content
    if (content.startsWith("?")) {
        val tagName = content.removePrefix("?")
        val result = transaction {
            Tag.find { Tags.name eq tagName }.firstOrNull()?.content
        }
        if (result != null) {
            message.channel.createMessage {
                embeds.add(parseMd(result))
            }
        } else {
            message.channel.createMessage("Tag not found")
        }
    }
}