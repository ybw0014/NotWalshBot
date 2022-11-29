package io.github.seggan.notwalshbot

import dev.kord.core.event.message.MessageCreateEvent
import io.github.seggan.notwalshbot.filters.MessageFilter

suspend fun MessageCreateEvent.onMessageSend() {
    if (message.author == null || message.author!!.isBot) return

    for (filter in MessageFilter.allFilters) {
        if (filter.test(message)) {
            filter.act(message)
            if (filter.delete) {
                message.delete()
            }
            break
        }
    }
}