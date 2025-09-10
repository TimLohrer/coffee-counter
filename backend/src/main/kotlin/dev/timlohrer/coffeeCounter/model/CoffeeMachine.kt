package dev.timlohrer.coffeeCounter.model

import dev.timlohrer.coffeeCounter.serializer.ObjectIdSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.time.ZonedDateTime

@Serializable
data class CoffeeMachine(
    @SerialName("_id")
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId = ObjectId(),
    val name: String,
    val imageData: String?,
    val isFree: Boolean,
    val createdAt: Long = ZonedDateTime.now().toEpochSecond() * 1000
)