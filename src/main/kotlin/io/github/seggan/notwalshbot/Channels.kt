package io.github.seggan.notwalshbot

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.GuildMessageChannel

enum class Channels(private val id: Snowflake) {
    BOT_LOGS(932798364737757214),
    BOT_TESTING(809225805322125394);

    constructor(id: Long) : this(Snowflake(id))

    suspend fun get(): GuildMessageChannel = bot.getChannelOf(id)!!
}