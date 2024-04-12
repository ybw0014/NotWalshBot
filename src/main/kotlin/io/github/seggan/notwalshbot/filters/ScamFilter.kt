package io.github.seggan.notwalshbot.filters

import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Message
import io.github.seggan.notwalshbot.httpClient
import io.github.seggan.notwalshbot.log
import io.github.seggan.notwalshbot.util.NEWLINE
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import java.time.Duration

object ScamFilter : MessageFilter {

    override val delete = true

    private val scamCache = mutableSetOf<String>()

    override suspend fun test(message: Message): Boolean {
        if (message.author?.isBot == true) return false
        for (link in scamCache) {
            if (message.content.contains(link)) {
                return true
            }
        }
        return false
    }

    override suspend fun act(message: Message) {
        message.channel.createMessage {
            content = "This message has been deleted because it contains a scam link. Please report this to the mods."
            messageReference = message.id
        }
    }

    suspend fun updateScamCache(scope: CoroutineScope) {
        scope.launch {
            while (true) {
                val response = httpClient.get("https://bad-domains.walshy.dev/domains.txt")
                val body = response.bodyAsText()
                if (response.status == HttpStatusCode.OK) {
                    val oldCache = scamCache.toSet()
                    scamCache.clear()
                    scamCache.addAll(body.split(NEWLINE))
                    val newLinks = scamCache.size - oldCache.size
                    if (newLinks > 0) {
                        log("Updated scam cache with $newLinks new links")
                    }
                } else {
                    log("Failed to update scam cache: ${response.status} $body")
                }
                delay(Duration.ofHours(12))
            }
        }
    }
}