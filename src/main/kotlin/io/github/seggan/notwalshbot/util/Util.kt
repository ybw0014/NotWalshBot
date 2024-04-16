package io.github.seggan.notwalshbot.util

import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.interaction.ActionInteractionBehavior
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.response.EphemeralMessageInteractionResponse
import dev.kord.core.entity.interaction.response.PublicMessageInteractionResponse
import dev.kord.core.event.interaction.ComponentInteractionCreateEvent
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.rest.builder.component.ButtonBuilder
import dev.kord.rest.builder.interaction.ModalBuilder
import dev.kord.rest.builder.message.allowedMentions

suspend fun MessageBehavior.replyWith(content: String, ping: Boolean = true): Message = reply {
    this.content = content
    this.allowedMentions {
        repliedUser = ping
    }
}

suspend fun ActionInteractionBehavior.respondPublic(message: String): PublicMessageInteractionResponse {
    return deferPublicResponse().respond { content = message }
}

suspend fun ActionInteractionBehavior.respondEphemeral(message: String): EphemeralMessageInteractionResponse {
    return deferEphemeralResponse().respond { content = message }
}

val componentMap = mutableMapOf<String, suspend ComponentInteractionCreateEvent.() -> Unit>()

fun ButtonBuilder.InteractionButtonBuilder.onClick(action: suspend ComponentInteractionCreateEvent.() -> Unit) {
    componentMap[customId] = action
}

val modalMap = mutableMapOf<String, suspend ModalSubmitInteractionCreateEvent.() -> Unit>()

fun ModalBuilder.onSubmit(action: suspend ModalSubmitInteractionCreateEvent.() -> Unit) {
    modalMap[customId] = action
}