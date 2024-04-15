package io.github.seggan.notwalshbot.filters

import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.allowedMentions

object SlimeFunFilter : MessageFilter {

    private val pattern = "[Ss]lime(?:F|( [Ff]))un".toRegex()

    override suspend fun test(message: Message): Boolean {
        return pattern.containsMatchIn(message.content)
    }

    override suspend fun act(message: Message): String? {
        message.channel.createMessage {
            content = "It's Slimefun, not " + pattern.find(message.content)!!.value
            messageReference = message.id
            allowedMentions {
                repliedUser = true
            }
        }
        return null
    }
}