package dev.timlohrer.coffeeCounter.model

import dev.timlohrer.coffeeCounter.serializer.ObjectIdSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.time.ZonedDateTime

@Serializable
data class User(
    @Contextual
    @SerialName("_id")
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId = ObjectId.get(),
    var token: String? = null,
    var email: String,
    var emailVerified: Boolean = false,
    var firstName: String,
    var lastName: String,
    var passwordHash: String,
    var salt: String,
    var disabled: Boolean = false,
    val isAdmin: Boolean = false,
    val createdAt: Long = ZonedDateTime.now().toEpochSecond() * 1000,
) {
    fun toMinimal(): UserMinimal {
        return UserMinimal(
            id = this.id,
            firstName = this.firstName,
            lastName = this.lastName,
            isAdmin = this.isAdmin,
        )
    }
}
