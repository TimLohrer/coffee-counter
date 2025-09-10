package dev.timlohrer.coffeeCounter.manager

import dev.timlohrer.coffeeCounter.database.DatabaseManager
import dev.timlohrer.coffeeCounter.database.eq
import dev.timlohrer.coffeeCounter.model.CoffeeMachine
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId

object MachineManager {
    val machines = DatabaseManager.database.getCollection<CoffeeMachine>("machines")
    
    suspend fun createMachine(name: String, imageData: String?, isFree: Boolean): CoffeeMachine {
        val machine = CoffeeMachine(name = name, imageData = imageData, isFree = isFree)
        
        machines.insertOne(machine)
        return machine
    }
    
    suspend fun getById(id: ObjectId): CoffeeMachine? {
        return machines.find(CoffeeMachine::id eq id).firstOrNull()
    }

    suspend fun getByName(name: String): CoffeeMachine? {
        return machines.find(CoffeeMachine::name eq name).firstOrNull()
    }
    
    suspend fun CoffeeMachine.update() {
        machines.replaceOne(CoffeeMachine::id eq this.id, this)
    }
    
    suspend fun CoffeeMachine.delete() {
        machines.findOneAndDelete(CoffeeMachine::id eq this.id)
    }
}