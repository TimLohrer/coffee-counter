import io.ktor.plugin.KTOR_VERSION

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "dev.timlohrer"
version = "0.0.1"

application {
    mainClass = "dev.timlohrer.coffeeCounter.ApplicationKt"
}

repositories {
    mavenCentral()
    maven { url = uri("https://packages.confluent.io/maven/") }
}

dependencies {
    implementation(libs.ktor.server.task.scheduling.core)
    implementation(libs.ktor.server.task.scheduling.redis)
    implementation(libs.ktor.server.task.scheduling.mongodb)
    implementation(libs.ktor.server.task.scheduling.jdbc)
    implementation(libs.ktor.server.rate.limiting)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.mongodb.driver.core)
    implementation(libs.mongodb.driver.sync)
    implementation(libs.bson)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.apache)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.swagger)
    implementation(libs.ktor.simple.cache)
    implementation(libs.ktor.simple.redis.cache)
    implementation(libs.ktor.server.openapi)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("io.ktor:ktor-server-cors:${KTOR_VERSION}")
}
