package io.github.seggan.notwalshbot.filters

import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Message
import io.github.seggan.notwalshbot.httpClient
import io.github.seggan.notwalshbot.log
import io.github.seggan.notwalshbot.util.NEWLINE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import okhttp3.Request
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
                val body = httpClient.newCall(
                    Request.Builder()
                        .url("https://bad-domains.walshy.dev/domains.txt")
                        .method("GET", null)
                        .build()
                ).execute().body
                if (body == null) {
                    log("Failed to get scam domains")
                } else {
                    body.use {
                        scamCache.clear()
                        scamCache.addAll(it.string().split(NEWLINE))
                    }
                    log("Updated scam domain cache")
                }
                delay(Duration.ofHours(12))
            }
        }
    }
}