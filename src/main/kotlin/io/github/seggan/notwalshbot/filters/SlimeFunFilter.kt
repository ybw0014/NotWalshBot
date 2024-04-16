package io.github.seggan.notwalshbot.filters

import dev.kord.core.entity.Message
import io.github.seggan.notwalshbot.util.replyWith

object SlimeFunFilter : MessageFilter {

    private val pattern = "[Ss]lime(?:F|( [Ff]))un".toRegex()

    override suspend fun test(message: Message): Boolean {
        return pattern.containsMatchIn(message.content)
    }

    override suspend fun act(message: Message) {
        message.replyWith("It's Slimefun, not " + pattern.find(message.content)!!.value)
    }
}