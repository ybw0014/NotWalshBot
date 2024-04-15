package io.github.seggan.notwalshbot.util

import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.response.EphemeralMessageInteractionResponse
import dev.kord.core.entity.interaction.response.PublicMessageInteractionResponse
import io.github.seggan.notwalshbot.commands.CommandEvent

suspend fun MessageBehavior.replyWith(content: String): Message = reply { this.content = content }
suspend fun CommandEvent.respondPublic(message: String): PublicMessageInteractionResponse {
    return this.interaction.deferPublicResponse().respond { content = message }
}

suspend fun CommandEvent.respondEphemeral(message: String): EphemeralMessageInteractionResponse {
    return this.interaction.deferEphemeralResponse().respond { content = message }
}