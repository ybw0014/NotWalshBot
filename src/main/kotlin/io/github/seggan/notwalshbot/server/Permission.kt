package io.github.seggan.notwalshbot.server

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Member

enum class Permission(val level: Int, val check: (Member) -> Boolean) {

    /**
     * The user is a bot owner
     */
    INTRINSIC(0, { it.id in intrinsicIds }),

    /**
     * The user is an admin
     */
    ADMIN(1, Roles.admin),

    /**
     * The user is staff
     */
    STAFF(2, Roles.staff),

    /**
     * The user is an addon developer
     */
    ADDON_DEVELOPER(3, Roles.addonDeveloper),

    /**
     * The user is a guinea pig
     */
    GUINEA_PIG(4, Roles.guineaPig),

    /**
     * Everyone else
     */
    EVERYONE(5, { true });

    constructor(level: Int, roleId: Snowflake) : this(level, { roleId in it.roleIds })
}

fun Member.isAtLeast(permission: Permission): Boolean {
    return Permission.values().filter { it.level <= permission.level }.any { it.check(this) }
}

object Roles {
    val admin = Snowflake(809219516411805747)
    val staff = Snowflake(809182558796906507)
    val addonDeveloper = Snowflake(809182521316343818)
    val guineaPig = Snowflake(809185800927445012)
}

/**
 * The IDs of the users who are bot owners
 */
private val intrinsicIds = setOf(
    Snowflake(516651203661266958), // Seggan#8111
    Snowflake(331473194441506816), // Sefiraat#3526
    Snowflake(312246160133980163)  // TheBusyBiscuit#0851
)