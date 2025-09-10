package dev.timlohrer.coffeeCounter.database

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.kotlin.client.coroutine.MongoClient
import dev.timlohrer.coffeeCounter.isLocal
import org.bson.UuidRepresentation

object DatabaseManager {
    val mongoClient = MongoClient.create(
        if (isLocal && System.getenv("MONGODB_HOST").isNullOrEmpty()) {
            MongoClientSettings.builder().uuidRepresentation(UuidRepresentation.STANDARD).build()
        } else if (System.getenv("MONGODB_USER").isNullOrEmpty()) {
            MongoClientSettings.builder().uuidRepresentation(UuidRepresentation.STANDARD)
                .applyToClusterSettings {
                    it.hosts(
                        listOf(
                            ServerAddress(
                                System.getenv("MONGODB_HOST"), System.getenv("MONGODB_PORT").toIntOrNull() ?: 27017
                            )
                        )
                    )
                }.build()
        } else {
            MongoClientSettings.builder().uuidRepresentation(UuidRepresentation.STANDARD)
                .credential(
                    MongoCredential.createCredential(
                        System.getenv("MONGODB_USER"),
                        "admin",
                        System.getenv("MONGODB_PASSWORD").toCharArray()
                    )
                ).applyToClusterSettings {
                    it.hosts(
                        listOf(
                            ServerAddress(
                                System.getenv("MONGODB_HOST"), System.getenv("MONGODB_PORT").toIntOrNull() ?: 27017
                            )
                        )
                    )
                }.build()
        }
    )
    
    val database = mongoClient.getDatabase(System.getenv("MONGODB_DATABASE"))
}
