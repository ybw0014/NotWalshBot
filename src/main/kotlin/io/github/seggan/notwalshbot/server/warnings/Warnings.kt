package io.github.seggan.notwalshbot.server.warnings

import dev.kord.common.entity.Snowflake
import kotlinx.datetime.Clock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.util.*
import kotlin.io.path.*

@OptIn(ExperimentalSerializationApi::class)
object Warnings : Iterable<Warning> {

    private val warningsFile = Path("data/warnings.json")

    private val warnings: MutableMap<Snowflake, MutableList<Warning>>

    init {
        if (!warningsFile.exists()) {
            warningsFile.parent.createDirectories()
            warningsFile.writeText("[]")
        }
        val loaded: List<Warning> = warningsFile.inputStream().use(Json::decodeFromStream)
        warnings = loaded.groupByTo(mutableMapOf(), Warning::userId)
    }

    fun save() {
        warningsFile.outputStream().use { Json.encodeToStream(warnings.values.flatten(), it) }
    }

    operator fun get(key: Snowflake): List<Warning> = warnings[key] ?: emptyList()

    fun add(user: Snowflake, reason: String): Warning {
        val warning = Warning(user, reason, Clock.System.now(), UUID.randomUUID())
        warnings.getOrPut(user, ::mutableListOf).add(warning)
        return warning
    }

    fun remove(key: Snowflake, id: UUID) {
        warnings[key]?.removeIf { it.id == id }
    }

    fun clear(key: Snowflake) {
        warnings.remove(key)
    }

    override fun iterator(): Iterator<Warning> = warnings.values.flatten().iterator()
}