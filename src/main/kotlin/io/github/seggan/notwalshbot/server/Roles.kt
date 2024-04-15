package io.github.seggan.notwalshbot.server

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Member
import io.github.seggan.notwalshbot.SERVER_ID
import io.github.seggan.notwalshbot.bot

object Roles {
    val admin = Snowflake(809219516411805747)
    val staff = Snowflake(809182558796906507)
    val moderator = Snowflake(1079857231555723385)
    val addonDeveloper = Snowflake(809182521316343818)
    val guineaPig = Snowflake(809185800927445012)

    /**
     * The IDs of the users who are bot owners
     */
    private val intrinsicIds = setOf(
        Snowflake(516651203661266958), // Seggan#8111
        Snowflake(331473194441506816), // Sefiraat#3526
        Snowflake(312246160133980163)  // TheBusyBiscuit#0851
    )

    fun isBotAdmin(member: Member): Boolean {
        return member.id in intrinsicIds || admin in member.roleIds || staff in member.roleIds || moderator in member.roleIds
    }
}

suspend fun Member.isAtLeast(role: Snowflake): Boolean {
    val serverRole = bot.getGuild(SERVER_ID).getRole(role)
    return roleBehaviors.any { it.getPosition() >= serverRole.rawPosition }
}
