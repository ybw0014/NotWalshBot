package io.github.seggan.notwalshbot

import dev.kord.core.event.message.MessageCreateEvent
import io.github.seggan.notwalshbot.filters.MessageFilter
import io.github.seggan.notwalshbot.server.Channels
import io.github.seggan.notwalshbot.server.Roles
import io.github.seggan.notwalshbot.util.replyWith

suspend fun MessageCreateEvent.onMessageSend() {
    if (message.author?.isBot == true) return

    for (filter in MessageFilter.all) {
        if (filter.test(message) && !Roles.isBotAdmin(message.getAuthorAsMember())) {
            val log = filter.act(message)
            if (log != null) {
                Channels.BOT_LOGS.get().createMessage(log)
            }
            return
        }
    }

    val content = message.content
    if (content.startsWith("?")) {
        val tagName = content.removePrefix("?")
        val tag = Tags[tagName]?.content ?: "Tag not found"
        message.replyWith(tag)
    }
}