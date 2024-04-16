package io.github.seggan.notwalshbot.filters

import dev.kord.core.behavior.ban
import dev.kord.core.entity.Message
import io.github.seggan.notwalshbot.httpClient
import io.github.seggan.notwalshbot.log
import io.github.seggan.notwalshbot.server.Channels
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

object ScamFilter : MessageFilter {

    private val scamCache = mutableSetOf<String>()

    override suspend fun test(message: Message): Boolean {
        if (message.author?.isBot == true) return false
        return scamCache.any { it in message.content }
    }

    override suspend fun act(message: Message) {
        message.delete("Scam link detected")
        message.getAuthorAsMember().ban {
            reason = "Scam link"
            deleteMessageDuration = 1.hours
        }
        Channels.BOT_LOGS.get().createMessage("Banned ${message.author?.mention} for sending scam link")
    }

    suspend fun updateScamCache(scope: CoroutineScope) {
        scope.launch {
            while (true) {
                val response = httpClient.get(SCAM_DOMAINS_URL)
                val body = response.bodyAsText()
                if (response.status == HttpStatusCode.OK) {
                    val oldCache = scamCache.toSet()
                    scamCache.clear()
                    scamCache.addAll(body.split('\n'))
                    val newLinks = scamCache.size - oldCache.size
                    if (newLinks > 0) {
                        log("Updated scam cache with $newLinks new links")
                    }
                } else {
                    log("Failed to update scam cache: ${response.status} $body")
                }
                delay(1.days)
            }
        }
    }
}

private const val SCAM_DOMAINS_URL = "https://bad-domains.walshy.dev/domains.txt"