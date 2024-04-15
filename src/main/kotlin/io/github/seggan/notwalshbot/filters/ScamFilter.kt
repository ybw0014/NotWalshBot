package io.github.seggan.notwalshbot.filters

import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import io.github.seggan.notwalshbot.httpClient
import io.github.seggan.notwalshbot.log
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import kotlinx.datetime.Clock
import java.time.Duration
import kotlin.time.Duration.Companion.days

object ScamFilter : MessageFilter {

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

    override suspend fun act(message: Message): String? {
        message.delete("Scam link detected")
        val member = message.getAuthorAsMember()
        member.edit {
            communicationDisabledUntil = Clock.System.now() + 7.days
            reason = "Scam link detected"
        }
        return "Deleted scam link from ${member.mention} in ${message.channel.asChannel().data.name.value}. The member has been muted for 7 days until further moderator action."
    }

    suspend fun updateScamCache(scope: CoroutineScope) {
        scope.launch {
            while (true) {
                val response = httpClient.get("https://bad-domains.walshy.dev/domains.txt")
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
                delay(Duration.ofHours(12))
            }
        }
    }
}