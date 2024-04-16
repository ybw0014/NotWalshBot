package io.github.seggan.notwalshbot.server.warnings

import dev.kord.common.entity.Snowflake
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import java.util.*

@Serializable
data class Warning(
    val userId: Snowflake,
    val reason: String,
    val timestamp: Instant,
    @Serializable(with = UUIDSerializer::class)
    val id: UUID
)

@OptIn(ExperimentalSerializationApi::class)
private object UUIDSerializer : KSerializer<UUID> {

    private val delegate = serializer<LongArray>()
    override val descriptor = SerialDescriptor("java.util.UUID", delegate.descriptor)

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeSerializableValue(delegate, longArrayOf(value.mostSignificantBits, value.leastSignificantBits))
    }

    override fun deserialize(decoder: Decoder): UUID {
        val (most, least) = decoder.decodeSerializableValue(delegate)
        return UUID(most, least)
    }
}
