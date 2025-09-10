package dev.timlohrer.coffeeCounter.routes

import dev.timlohrer.coffeeCounter.manager.CoffeeManager
import dev.timlohrer.coffeeCounter.manager.CoffeeManager.delete
import dev.timlohrer.coffeeCounter.manager.MachineManager
import dev.timlohrer.coffeeCounter.middleware.handleUserAuthentication
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class CreateCoffeeRequest(
    val machineId: String,
    val price: Double
)

object CoffeesRoute {
    fun Route.coffeesRoute() {
        route("coffees") {
            post {
                val user = handleUserAuthentication() ?: return@post
                val data = call.receive<CreateCoffeeRequest>()

                val machineId = try {
                    ObjectId(data.machineId)
                } catch (e: IllegalArgumentException) {
                    return@post call.respond(HttpStatusCode.BadRequest, "Invalid machine ID format")
                }
                MachineManager.getById(machineId)
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid machine ID")

                val coffee = CoffeeManager.createCoffee(user.id, ObjectId(data.machineId), data.price)
                call.respond(coffee)
            }
            
            delete("/{id}") {
                handleUserAuthentication() ?: return@delete
                val coffeeId = call.parameters["id"]!!
                val coffee = CoffeeManager.getById(ObjectId(coffeeId))
                    ?: return@delete call.respond(HttpStatusCode.NotFound, "Coffee not found")

                coffee.delete()
                call.respondText("Coffee deleted successfully!")
            }
        }
    }
}