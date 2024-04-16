package io.github.seggan.notwalshbot

import dev.kord.core.event.message.MessageCreateEvent
import io.github.seggan.notwalshbot.filters.MessageFilter
import io.github.seggan.notwalshbot.server.Roles
import io.github.seggan.notwalshbot.server.tags.Tags
import io.github.seggan.notwalshbot.util.replyWith

suspend fun MessageCreateEvent.onMessageSend() {
    val member = message.getAuthorAsMemberOrNull()
    if (member == null || member.isBot) return

    for (filter in MessageFilter.all) {
        if (filter.test(message) && !Roles.isBotAdmin(member)) {
            filter.act(message)
            return
        }
    }

    val content = message.content
    if (content.startsWith("?") && content.length > 1) {
        val tagName = content.removePrefix("?")
        val tag = Tags[tagName]?.content ?: "Tag not found"
        message.replyWith(tag, false)
    }
}