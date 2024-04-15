package io.github.seggan.notwalshbot.filters

import dev.kord.core.entity.Message

/**
 * Credits to WalshyDev for the original code
 */
interface MessageFilter {

    companion object {
        val all = listOf(SlimeFunFilter, ScamFilter)
    }

    /**
     * Check the message to see if it should be filtered
     *
     * @param message The message to check
     * @return True if the message should be filtered, false otherwise
     */
    suspend fun test(message: Message): Boolean

    /**
     * What to do when the message is filtered
     *
     * @param message The message that was filtered
     * @return The message to log
     */
    suspend fun act(message: Message): String?
}