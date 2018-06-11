package com.etermax.tindercrack.server.user.model

data class User(val id: String, val password: String) {

    fun blankPassword(): User {
        return User(id, "")
    }

}