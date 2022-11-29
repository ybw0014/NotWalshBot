package io.github.seggan.notwalshbot.filters

import dev.kord.core.entity.Message
import io.github.seggan.notwalshbot.filters.SlimeFunFilter

/**
 * Credits to WalshyDev for the original code
 */
interface MessageFilter {

    companion object {
        val allFilters = listOf(SlimeFunFilter, ScamFilter)
    }

    /**
     * If the message should be deleted
     *
     * @return True if the message should be deleted, false otherwise
     */
    val delete: Boolean

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
     */
    suspend fun act(message: Message)
}