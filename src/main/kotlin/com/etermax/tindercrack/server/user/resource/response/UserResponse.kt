package com.etermax.tindercrack.server.user.resource.response

import com.etermax.tindercrack.server.user.model.User

class UserResponse(user: User) {
    val id = user.id
    val password = user.password
}