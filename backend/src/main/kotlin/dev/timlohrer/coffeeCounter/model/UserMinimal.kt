package dev.timlohrer.coffeeCounter.model

import dev.timlohrer.coffeeCounter.manager.UserManager
import dev.timlohrer.coffeeCounter.serializer.ObjectIdSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class UserMinimal(
    @Contextual
    @SerialName("_id")
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId = ObjectId.get(),
    val firstName: String,
    val lastName: String,
    val isAdmin: Boolean,
) {
    suspend fun toFull(): User {
        return UserManager.getById(this.id)!!
    }
}
