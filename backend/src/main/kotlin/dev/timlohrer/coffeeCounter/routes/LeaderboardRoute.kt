package dev.timlohrer.coffeeCounter.routes

import dev.timlohrer.coffeeCounter.config.CoffeeCounterConstants
import dev.timlohrer.coffeeCounter.manager.CoffeeManager
import dev.timlohrer.coffeeCounter.middleware.handleUserAuthentication
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.bson.types.ObjectId
import java.time.ZonedDateTime

object LeaderboardRoute {
    fun Route.leaderboardRoute() {
        route("/leaderboard") {
            get { 
                handleUserAuthentication() ?: return@get
                val from = call.request.queryParameters["from"]?.toLongOrNull() ?: 0L
                val to = call.request.queryParameters["to"]?.toLongOrNull() ?: (ZonedDateTime.now().toEpochSecond() * 1000)
                
                val coffees = CoffeeManager.getInTimeRange(from, to)
                    .groupBy { it.userId }
                    .map { (userId, coffees) -> userId.toHexString() to coffees.size }
                    .sortedBy { (_, coffees) -> coffees }
                    .take(CoffeeCounterConstants.DEFAULT_LEADERBOARD_SIZE)
                
                call.respond(coffees)
            }
            
            get("/{userId}/position") {
                val user = handleUserAuthentication() ?: return@get
                val userId = call.parameters["userId"] ?: return@get call.respond("Missing userId")
                
                val from = call.request.queryParameters["from"]?.toLongOrNull() ?: 0L
                val to = call.request.queryParameters["to"]?.toLongOrNull() ?: (ZonedDateTime.now().toEpochSecond() * 1000)
                
                val coffees = CoffeeManager.getInTimeRange(from, to)
                    .groupBy { it.userId }
                    .map { (userId, coffees) -> userId.toHexString() to coffees.size }
                    .sortedBy { (_, coffees) -> coffees }
                
                val position = coffees.indexOfFirst { (id, _) -> id == (if (userId == "@me") user.id.toHexString() else userId) } + 1
                
                call.respond(position)
            }
        }
    }
}