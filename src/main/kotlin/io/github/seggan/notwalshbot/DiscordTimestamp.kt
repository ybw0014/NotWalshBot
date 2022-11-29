package io.github.seggan.notwalshbot

import java.util.concurrent.TimeUnit

data class DiscordTimestamp(val epoch: Long, val style: TimestampStyle = TimestampStyle.LONG_DATE_TIME) {

    companion object {
        fun now() = DiscordTimestamp(System.currentTimeMillis())
    }

    override fun toString() = "<t:${TimeUnit.MILLISECONDS.toSeconds(epoch)}:${style.value}>"
}

enum class TimestampStyle(val value: Char) {
    SHORT_TIME('t'),
    LONG_TIME('T'),
    SHORT_DATE('d'),
    LONG_DATE('D'),
    SHORT_DATE_TIME('f'),
    LONG_DATE_TIME('F'),
    RELATIVE('R')
}
