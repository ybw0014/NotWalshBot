package io.github.seggan.notwalshbot.filters

import dev.kord.common.entity.ButtonStyle
import dev.kord.core.behavior.ban
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.actionRow
import io.github.seggan.notwalshbot.httpClient
import io.github.seggan.notwalshbot.server.Channels
import io.github.seggan.notwalshbot.server.Roles
import io.github.seggan.notwalshbot.util.onClick
import io.github.seggan.notwalshbot.util.respondPublic
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

object InviteFilter : MessageFilter {

    private val inviteRegex = Regex("discord(?:app\\.com/invite|\\.gg)/([a-zA-Z0-9]+)", RegexOption.IGNORE_CASE)

    private val badWords = mutableSetOf<Regex>()

    override suspend fun test(message: Message): Boolean {
        return inviteRegex.findAll(message.content).map { it.groupValues[1] }
            .any { it != "slimefun" && it != "SqD3gg5SAU" }
    }

    override suspend fun act(message: Message) {
        val member = message.getAuthorAsMember()
        member.edit {
            communicationDisabledUntil = Clock.System.now() + 7.days
        }
        val links = inviteRegex.findAll(message.content).map { it.groupValues[1] }
            .filter { it != "slimefun" && it != "SqD3gg5SAU" }
        message.delete("Invite links are not allowed")
        for (link in links) {
            val serverTitle = getServerTitle(link)
            val lower = serverTitle.lowercase()
            if (badWords.any { it in lower }) {
                member.edit {
                    communicationDisabledUntil = null
                }
                member.ban {
                    reason = "NSFW server invite"
                    deleteMessageDuration = 0.seconds
                }
                Channels.BOT_LOGS.get()
                    .createMessage("Banned ${message.author?.mention} for sending NSFW server invite")
                return
            }
            Channels.BOT_LOGS.get().createMessage {
                content = """
                    ${message.author?.mention} sent an invite to "$serverTitle": $link
                    <@&${Roles.staff}> <@&${Roles.moderator}> please review this manually
                    """.trimIndent()
                actionRow {
                    interactionButton(ButtonStyle.Danger, "ban") {
                        label = "Ban"
                        onClick {
                            member.ban {
                                reason = "NSFW server invite"
                                deleteMessageDuration = 0.seconds
                            }
                            member.getDmChannel().createMessage("You have been banned for sending an NSFW server invite")
                            interaction.respondPublic("Banned ${message.author?.mention} for sending NSFW server invite")
                        }
                    }
                    interactionButton(ButtonStyle.Success, "allow") {
                        label = "False alarm"
                        onClick {
                            member.edit {
                                communicationDisabledUntil = null
                            }
                            member.getDmChannel().createMessage("Your mute has been lifted")
                            interaction.respondPublic("Allowed invite to $serverTitle")
                        }
                    }
                }
            }
            member.getDmChannel().createMessage("""
                Hey, it looks like you sent an invite to a server. Invites are not allowed here. You are temporarily muted for 7 days while the staff review the invite.
                If the server is not NSFW, you will be cleared shortly.
                """.trimIndent())
        }
    }

    private suspend fun getServerTitle(link: String): String {
        val response = httpClient.get("https://discord.com/api/v9/invites/$link")
        if (response.status.isSuccess()) {
            val json = Json.decodeFromString<JsonObject>(response.bodyAsText())
            return json["guild"]?.jsonObject?.get("name")?.jsonPrimitive?.content ?: "Unknown"
        }
        return "Unknown"
    }

    suspend fun updateBadWords(scope: CoroutineScope) {
        scope.launch {
            val response = httpClient.get(BAD_WORDS_URL)
            if (response.status.isSuccess()) {
                badWords.clear()
                badWords.addAll(response.bodyAsText()
                    .split('\n')
                    .filter { it.isNotBlank() }
                    .map { """\b$it\b""".toRegex() })
            }
            delay(1.days)
        }
    }
}

private const val BAD_WORDS_URL =
    "https://raw.githubusercontent.com/LDNOOBW/List-of-Dirty-Naughty-Obscene-and-Otherwise-Bad-Words/master/en"