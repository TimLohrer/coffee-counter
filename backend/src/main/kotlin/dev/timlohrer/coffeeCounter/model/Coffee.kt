package dev.timlohrer.coffeeCounter.model

import dev.timlohrer.coffeeCounter.serializer.ObjectIdSerializer
import org.bson.types.ObjectId
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.time.ZonedDateTime

@Serializable
data class Coffee(
    @SerialName("_id")
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId = ObjectId(),
    @Serializable(with = ObjectIdSerializer::class)
    val userId: ObjectId,
    @Serializable(with = ObjectIdSerializer::class)
    val machineId: ObjectId,
    val price: Double = 0.0,
    val createdAt: Long = ZonedDateTime.now().toEpochSecond() * 1000
)
