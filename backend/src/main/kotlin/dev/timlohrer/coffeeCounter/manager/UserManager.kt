package dev.timlohrer.coffeeCounter.manager

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.timlohrer.coffeeCounter.database.DatabaseManager
import dev.timlohrer.coffeeCounter.database.eq
import dev.timlohrer.coffeeCounter.model.User
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId
import org.mindrot.jbcrypt.BCrypt

object UserManager {
    val users = DatabaseManager.database.getCollection<User>("users")

    private fun User.generateToken() {
        this.token = JWT.create()
            .withSubject(this.id.toHexString())
            .sign(Algorithm.HMAC256(this.salt))
            .toString()
    }
    
    fun User.verifyPassword(password: String): Boolean {
        return BCrypt.checkpw(password, this.passwordHash)
    }
    
    suspend fun createUser(email: String, password: String, firtName: String, lastName: String): User {
        val salt = BCrypt.gensalt(10)
        val user = User(
            email = email,
            firstName = firtName,
            lastName = lastName,
            passwordHash = BCrypt.hashpw(password, salt),
            salt = salt,
        )
        
        user.generateToken()
        
        users.insertOne(user)
        return user
    }
    
    suspend fun getById(id: ObjectId): User? {
        return users.find(User::id eq id).firstOrNull()
    }
    
    suspend fun getByEmail(email: String): User? {
        return users.find(User::email eq email).firstOrNull()
    }
    
    suspend fun getByToken(token: String): User? {
        val decoded = JWT.decode(token)
        val userId = decoded.subject ?: return null
        val user = getById(ObjectId(userId)) ?: return null
        
        if (JWT.require(Algorithm.HMAC256(user.salt)).build().verify(token).subject != user.id.toHexString()) {
            return null
        }
        
        return user
    }
    
    suspend fun User.update() {
        users.replaceOne(User::id eq this.id, this)
    }
    
    suspend fun User.delete() {
        users.findOneAndDelete(User::id eq this.id)
    }
}