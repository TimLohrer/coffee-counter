package dev.timlohrer.coffeeCounter.database

import com.mongodb.client.model.Filters
import dev.timlohrer.coffeeCounter.database.MongoUtils.EMPTY_BSON
import dev.timlohrer.coffeeCounter.database.MongoUtils.fieldName
import org.bson.BsonDocument
import org.bson.Document
import org.bson.conversions.Bson
import java.util.regex.Pattern
import kotlin.reflect.KProperty

// CREDITS TO https://github.com/Litote/kmongo

infix fun <T> KProperty<T>.eq(value: T?): Bson {
    return Filters.eq(fieldName(), value)
}

infix fun <T> KProperty<Iterable<T?>?>.contains(value: T?): Bson = Filters.eq(fieldName(), value)

infix fun <T> KProperty<T>.ne(value: T?): Bson {
    return Filters.ne(fieldName(), value)
}

infix fun <T> KProperty<T>.gt(value: T & Any): Bson {
    return Filters.gt(fieldName(), value)
}

infix fun <T> KProperty<T>.lt(value: T & Any): Bson {
    return Filters.lt(fieldName(), value)
}

infix fun <T> KProperty<T>.gte(value: T & Any): Bson {
    return Filters.gte(fieldName(), value)
}

infix fun <T> KProperty<T>.lte(value: T & Any): Bson {
    return Filters.lte(fieldName(), value)
}

infix fun <T> KProperty<T>.`in`(value: Iterable<T>): Bson {
    return Filters.`in`(fieldName(), value)
}

infix fun <T> KProperty<T>.nin(value: Iterable<T>): Bson {
    return Filters.nin(fieldName(), value)
}

infix fun KProperty<String?>.regex(regex: String): Bson = Filters.regex(fieldName(), regex)
infix fun KProperty<String?>.regex(regex: Pattern): Bson = Filters.regex(fieldName(), regex)
infix fun KProperty<String?>.regex(regex: Regex): Bson = Filters.regex(fieldName(), regex.toPattern())

fun and(filters: Iterable<Bson?>): Bson = combineFilters(Filters::and, filters)
fun and(vararg filters: Bson?): Bson = and(filters.toList())

fun or(filters: Iterable<Bson?>): Bson = combineFilters(Filters::or, filters)
fun or(vararg filters: Bson?): Bson = or(filters.toList())

fun not(filter: Bson): Bson = Filters.not(filter)

fun nor(vararg filters: Bson): Bson = Filters.nor(*filters)
fun nor(filters: Iterable<Bson>): Bson = Filters.nor(filters)

fun <T> KProperty<T>.exists(): Bson = Filters.exists(fieldName())
infix fun <T> KProperty<T>.exists(exists: Boolean): Bson = Filters.exists(fieldName(), exists)

internal fun combineFilters(combiner: (List<Bson>) -> Bson, filters: Iterable<Bson?>): Bson =
    filters
        .filterNotNull()
        .filterNot { it is Document && it.isEmpty() }
        .filterNot { it is BsonDocument && it.isEmpty() }
        .run {
            if (isEmpty()) EMPTY_BSON
            else if (size == 1) first()
            else combiner(this)
        }