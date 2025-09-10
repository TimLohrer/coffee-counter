package dev.timlohrer.coffeeCounter.routes

import dev.timlohrer.coffeeCounter.manager.MachineManager
import dev.timlohrer.coffeeCounter.manager.MachineManager.delete
import dev.timlohrer.coffeeCounter.manager.MachineManager.update
import dev.timlohrer.coffeeCounter.middleware.handleUserAuthentication
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class CreateMachineRequest(
    val name: String,
    val imageData: String?,
    val isFree: Boolean
)

@Serializable
data class UpdateMachineRequest(
    val name: String?,
    val imageData: String?,
    val isFree: Boolean?
)

object CoffeeMachineRoute {
    fun Route.coffeeMachinesRoute() {
        route("machines") {
            post {
                handleUserAuthentication(true) ?: return@post
                val data = call.receive<CreateMachineRequest>()

                if (MachineManager.getByName(data.name) != null) {
                    return@post call.respond(HttpStatusCode.Forbidden, "Machine with this name already exists!")
                }

                val machine = MachineManager.createMachine(data.name, data.imageData, data.isFree)
                call.respond(machine)
            }

            patch("/{id}") {
                handleUserAuthentication(true) ?: return@patch
                val data = call.receive<UpdateMachineRequest>()

                val machineId = try {
                    ObjectId(call.parameters["id"]!!)
                } catch (e: IllegalArgumentException) {
                    return@patch call.respond(HttpStatusCode.BadRequest, "Invalid machine ID format")
                }
                val machine = MachineManager.getById(machineId)
                    ?: return@patch call.respond(HttpStatusCode.NotFound, "Machine not found")

                val updatedMachine = machine.copy(
                    name = data.name ?: machine.name,
                    imageData = data.imageData ?: machine.imageData,
                    isFree = data.isFree ?: machine.isFree
                )

                updatedMachine.update()

                call.respond(updatedMachine)
            }
            
            get { 
                handleUserAuthentication() ?: return@get
                val machines = MachineManager.machines.find().toList()
                call.respond(machines)
            }
            
            delete("/{id}") {
                handleUserAuthentication(true) ?: return@delete

                val machineId = try {
                    ObjectId(call.parameters["id"]!!)
                } catch (e: IllegalArgumentException) {
                    return@delete call.respond(HttpStatusCode.BadRequest, "Invalid machine ID format")
                }
                val machine = MachineManager.getById(machineId)
                    ?: return@delete call.respond(HttpStatusCode.NotFound, "Machine not found")

                machine.delete()
                call.respondText("Machine deleted successfully!")
            }
        }
    }
}