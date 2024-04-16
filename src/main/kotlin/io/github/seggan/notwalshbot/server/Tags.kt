package io.github.seggan.notwalshbot.server

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import kotlin.io.path.*

@OptIn(ExperimentalSerializationApi::class)
object Tags : MutableIterable<Tag> {

    private val tagsFile = Path("data/tags.json")
    private val tags: MutableMap<String, Tag>

    init {
        if (!tagsFile.exists()) {
            tagsFile.parent.createDirectories()
            tagsFile.writeText("[]")
        }
        val loaded: List<Tag> = tagsFile.inputStream().use(Json::decodeFromStream)
        tags = loaded.associateBy { it.name }.toMutableMap()
    }

    fun save() {
        tagsFile.outputStream().use { Json.encodeToStream(tags.values.toList(), it) }
    }

    operator fun get(key: String): Tag.Normal? {
        val tag = tags[key] ?: return null
        return when (tag) {
            is Tag.Alias -> get(tag.target)
            is Tag.Normal -> tag
        }
    }

    operator fun set(key: String, value: Tag) {
        tags[key] = value
    }

    fun remove(key: String): Tag? = tags.remove(key)

    override fun iterator(): MutableIterator<Tag> = tags.values.iterator()
}