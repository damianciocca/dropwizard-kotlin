package com.etermax.tindercrack.server.user.repository

import com.etermax.tindercrack.server.user.model.User
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils.createMissingTablesAndColumns
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.absoluteValue

class UserRepository {

    object UsersDAO : IntIdTable() {
        val password = varchar("password", 50)
    }

    class UserDAO(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<UserDAO>(UsersDAO)

        var password by UsersDAO.password

        fun mapToUser() = User(id.toString(), password)
    }

    init {

        transaction {
            createMissingTablesAndColumns(UsersDAO)
        }

    }

    fun newUser(): User {

        lateinit var user: UserDAO

        transaction {
            user = UserDAO.new {
                password = ThreadLocalRandom.current().nextLong().absoluteValue.toString()
            }
        }

        return user.mapToUser()
    }

    fun find(id: String): User? {

        var user: UserDAO? = null

        transaction {
            user = UserDAO.findById(id.toInt())
        }

        return user?.mapToUser()
    }
}
