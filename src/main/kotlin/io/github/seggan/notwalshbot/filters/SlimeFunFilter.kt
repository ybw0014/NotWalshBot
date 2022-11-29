package io.github.seggan.notwalshbot.filters

import dev.kord.core.entity.Message

object SlimeFunFilter : MessageFilter {

    override val delete = false

    override suspend fun test(message: Message): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun act(message: Message) {
        TODO("Not yet implemented")
    }
}