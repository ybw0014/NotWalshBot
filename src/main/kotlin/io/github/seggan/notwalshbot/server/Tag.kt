package io.github.seggan.notwalshbot.server

import kotlinx.serialization.Serializable

@Serializable
sealed interface Tag {

    val name: String

    @Serializable
    data class Normal(override val name: String, val content: String) : Tag

    @Serializable
    data class Alias(override val name: String, val target: String) : Tag
}