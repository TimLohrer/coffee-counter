package dev.timlohrer.coffeeCounter.routes

import dev.timlohrer.coffeeCounter.manager.UserManager
import dev.timlohrer.coffeeCounter.manager.UserManager.verifyPassword
import dev.timlohrer.coffeeCounter.model.UserMinimal
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoginResponse(val user: UserMinimal, val token: String)

object AuthRoutes {
    fun Route.authRoute() {
        route("auth") {
            post("/login") {
                val request = call.receive<LoginRequest>()
                val user = UserManager.getByEmail(request.email) ?: return@post call.respond(HttpStatusCode.BadRequest, "Email does not exist.")
                
                if (!user.verifyPassword(request.password)) {
                    return@post call.respond(HttpStatusCode.Unauthorized, "Incorrect password.")
                }
                
                call.respond(LoginResponse(user.toMinimal(), user.token!!))
            }
        }
    }
}