package dev.timlohrer.coffeeCounter

import dev.timlohrer.coffeeCounter.routes.AuthRoutes.authRoute
import dev.timlohrer.coffeeCounter.routes.CoffeeMachineRoute.coffeeMachinesRoute
import dev.timlohrer.coffeeCounter.routes.CoffeesRoute.coffeesRoute
import dev.timlohrer.coffeeCounter.routes.LeaderboardRoute.leaderboardRoute
import dev.timlohrer.coffeeCounter.routes.UserRouter.usersRoute
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

val isLocal = System.getenv("IS_LOCAL").toBoolean()
val API_VERSION = 1

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::main)
        .start(wait = true)
}

fun Application.main() {
    install(CORS) {
        allowCredentials = true
        allowSameOrigin = true
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
    }
    install (ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        })
    }
    
    routing {
        route("/api/v${API_VERSION}") {
            usersRoute()
            authRoute()
            coffeeMachinesRoute()
            coffeesRoute()
            leaderboardRoute()
            
            get { 
                call.respondText("Hello, World!")
            }
        }
    }
}
