package io.github.seggan.notwalshbot.server

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.channel.GuildMessageChannel
import io.github.seggan.notwalshbot.bot

enum class Channels(id: Long) {
    BOT_LOGS(932798364737757214),
    BOT_TESTING(809225805322125394);

    private val id = Snowflake(id)

    suspend fun get(): GuildMessageChannel = bot.getChannelOf(id)!!
}