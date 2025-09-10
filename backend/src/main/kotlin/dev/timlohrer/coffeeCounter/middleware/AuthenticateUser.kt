package dev.timlohrer.coffeeCounter.middleware

import dev.timlohrer.coffeeCounter.manager.UserManager
import dev.timlohrer.coffeeCounter.model.User
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext

suspend fun RoutingContext.handleUserAuthentication(requiredAdmin: Boolean? = false): User? {
    val token = call.request.headers["Authorization"]?.split(" ")?.getOrNull(1)

    if (token == null) {
        call.respond(HttpStatusCode.Unauthorized, "Missing authorization.")
        return null
    }
    
    val user = UserManager.getByToken(token)
    if (user == null) {
        call.respond(HttpStatusCode.Unauthorized, "Invalid token.")
        return null
    }
    
    if (requiredAdmin == true && !user.isAdmin) {
        call.respond(HttpStatusCode.Forbidden, "You need to be an admin to access this resource.")
        return null
    }
    
    return user
}