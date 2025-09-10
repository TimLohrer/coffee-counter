package dev.timlohrer.coffeeCounter.routes

import dev.timlohrer.coffeeCounter.manager.CoffeeManager
import dev.timlohrer.coffeeCounter.manager.UserManager
import dev.timlohrer.coffeeCounter.manager.UserManager.delete
import dev.timlohrer.coffeeCounter.manager.UserManager.update
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
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class CreateUserRequest(
    val email: String,
    val password: String,
    val name: String
)

@Serializable
data class UpdateUserRequest(
    val email: String?,
    val name: String?,
)

object UserRouter {
    fun Route.usersRoute() {
        route("users") {
            post { 
                val data = call.receive<CreateUserRequest>()
                
                if (UserManager.getByEmail(data.email) != null) {
                    return@post call.respond(HttpStatusCode.Forbidden, "User with this email already exists!")
                }
                
                val user = UserManager.createUser(data.email, data.password, data.name)
                call.respond(user.toMinimal())
            }
            
            patch("/{id}") {
                val user = handleUserAuthentication() ?: return@patch
                val data = call.receive<UpdateUserRequest>()
                val userId = call.parameters["id"]!!
                
                if (user.id.toHexString() != userId && userId != "@me") {
                    return@patch call.respond(HttpStatusCode.Forbidden, "You can only update your own user.")
                }
                
                val updatedUser = user.copy(
                    name = data.name ?: user.name,
                    email = data.email ?: user.email,
                )

                updatedUser.update()
                
                call.respond(updatedUser.toMinimal())
            }
            
            get("/{id}") {
                val reqUser = handleUserAuthentication() ?: return@get
                var userId = call.parameters["id"]!!
                
                val user = if (userId == "@me") {
                    reqUser
                } else {
                    userId = try {
                        ObjectId(userId).toHexString()
                    } catch (e: IllegalArgumentException) {
                        return@get call.respond(HttpStatusCode.BadRequest, "Invalid user ID format")
                    }
                    UserManager.getById(ObjectId(userId)) ?: return@get call.respond(
                        HttpStatusCode.NotFound,
                        "User not found"
                    )
                }
                call.respond(user.toMinimal())
            }
            
            get("/{id}/coffees") {
                val user = handleUserAuthentication() ?: return@get
                val userId = call.parameters["id"]!!
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                
                if (user.id.toHexString() != userId && userId != "@me") {
                    return@get call.respond(HttpStatusCode.Forbidden, "You can only view your own coffees.")
                }
                
                val coffees = CoffeeManager.getPaged(user.id, page)
                
                call.respond(coffees)
            }
            
            delete("/{id}") {
                val reqUser = handleUserAuthentication() ?: return@delete
                val userId = call.parameters["id"]!!
                
                if (userId != reqUser.id.toHexString() && userId != "@me") {
                    return@delete call.respond(HttpStatusCode.Forbidden, "You can only delete your own user.")
                }
                
                reqUser.delete()
                call.respondText("User deleted successfully")
            }
        }
    }
}