package io.github.seggan.notwalshbot

import io.github.seggan.notwalshbot.server.Tag
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import kotlin.io.path.*

@OptIn(ExperimentalSerializationApi::class)
object Tags : MutableMap<String, Tag> {

    private val tagsFile = Path("data/tags.json")
    private val tags: MutableMap<String, Tag>

    init {
        if (!tagsFile.exists()) {
            tagsFile.createFile()
        }
        val loaded: List<Tag> = tagsFile.inputStream().use(Json::decodeFromStream)
        tags = loaded.associateBy { it.name }.toMutableMap()
    }

    fun save() {
        tagsFile.outputStream().use { Json.encodeToStream(tags.values.toList(), it) }
    }

    override fun get(key: String): Tag.Normal? {
        val tag = tags[key] ?: return null
        return when (tag) {
            is Tag.Alias -> get(tag.target)
            is Tag.Normal -> tag
        }
    }

    override fun put(key: String, value: Tag): Tag? {
        return tags.put(key, value).also { save() }
    }

    override val entries = tags.entries
    override val keys = tags.keys
    override val size = tags.size
    override val values= tags.values
    override fun clear() = tags.clear()
    override fun isEmpty() = tags.isEmpty()
    override fun remove(key: String) = tags.remove(key)
    override fun putAll(from: Map<out String, Tag>) = tags.putAll(from)
    override fun containsValue(value: Tag) = tags.containsValue(value)
    override fun containsKey(key: String) = tags.containsKey(key)
}