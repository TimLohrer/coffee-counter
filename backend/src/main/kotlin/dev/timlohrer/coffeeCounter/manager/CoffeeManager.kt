package dev.timlohrer.coffeeCounter.manager

import dev.timlohrer.coffeeCounter.config.CoffeeCounterConstants
import dev.timlohrer.coffeeCounter.database.DatabaseManager
import dev.timlohrer.coffeeCounter.database.eq
import dev.timlohrer.coffeeCounter.model.Coffee
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId

object CoffeeManager {
    val coffees = DatabaseManager.database.getCollection<Coffee>("coffees")
    
    suspend fun createCoffee(userId: ObjectId, machineId: ObjectId, price: Double): Coffee {
        val coffee = Coffee(userId = userId, machineId = machineId, price = price)
        
        coffees.insertOne(coffee)
        return coffee
    }
    
    suspend fun getById(id: ObjectId): Coffee? {
        return coffees.find(Coffee::id eq id).firstOrNull()
    }
    
    suspend fun getPaged(userId: ObjectId, page: Int): List<Coffee> {
        return coffees.find(Coffee::userId eq userId)
            .skip((page - 1) * CoffeeCounterConstants.DEFAULT_COFFEES_PAGE_SIZE)
            .limit(CoffeeCounterConstants.DEFAULT_COFFEES_PAGE_SIZE)
            .toList()
    }
    
    suspend fun getInTimeRange(from: Long, to: Long): List<Coffee> {
        return coffees.find()
            .filter { it.createdAt in from..to }
            .toList()
    }
    
    suspend fun Coffee.update() {
        coffees.replaceOne(Coffee::id eq this.id, this)
    }
    
    suspend fun Coffee.delete() {
        coffees.findOneAndDelete(Coffee::id eq this.id)
    }
}