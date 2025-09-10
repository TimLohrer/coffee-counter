package dev.timlohrer.coffeeCounter.database

import kotlinx.serialization.SerialName
import org.bson.RawBsonDocument
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.conversions.Bson
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

object MongoUtils {
    private const val EMPTY_JSON: String = "{}"
    val EMPTY_BSON: Bson = RawBsonDocument.parse(EMPTY_JSON)

    fun <T> KProperty<T>.fieldName(): String {
        // Check if the property is marked with @BsonId and return "_id" for MongoDB
        if (this.findAnnotation<BsonId>() != null) {
            return "_id"
        }

        // Check if the property has a @SerialName annotation
        this.findAnnotation<SerialName>()?.let { return it.value }

        // Otherwise, return the actual property name
        return this.name
    }

}