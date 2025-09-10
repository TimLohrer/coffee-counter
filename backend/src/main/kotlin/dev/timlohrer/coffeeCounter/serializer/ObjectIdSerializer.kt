package dev.timlohrer.coffeeCounter.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import org.bson.codecs.kotlinx.BsonDecoder
import org.bson.codecs.kotlinx.BsonEncoder
import org.bson.codecs.kotlinx.ObjectIdSerializer
import org.bson.types.ObjectId

object ObjectIdSerializer : KSerializer<ObjectId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ObjectIdSerializer", PrimitiveKind.STRING)

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: ObjectId) {
        if (encoder is BsonEncoder && !SerializationHelper.isNameState(encoder)) {
            // Wenn BSON und kein NAME (wie in einer Map), serialize als binary
            encoder.encodeObjectId(value)
        } else {
            // Wenn nicht BSON, dann serialize als String
            encoder.encodeString(value.toString())
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): ObjectId {
        return if (decoder is BsonDecoder && !SerializationHelper.isNameState(decoder)) {
            // Wenn BSON und kein NAME (wie in einer Map), serialize als binary
            decoder.decodeObjectId()
        } else {
            // Wenn nicht BSON, dann serialize als String
            ObjectId(decoder.decodeString())
        }
    }

    public val serializersModule: SerializersModule = SerializersModule {
        contextual(ObjectId::class, ObjectIdSerializer)
    }
}