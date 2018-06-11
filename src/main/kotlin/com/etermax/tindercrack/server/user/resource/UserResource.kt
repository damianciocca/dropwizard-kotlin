package com.etermax.tindercrack.server.user.resource

import com.etermax.tindercrack.server.user.repository.UserRepository
import com.etermax.tindercrack.server.user.resource.response.UserResponse
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/user")
class UserResource(private val userRepository: UserRepository) {

    @GET
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    fun newUser(): Response {
        return Response.ok(UserResponse(userRepository.newUser())).build()
    }

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun get(@PathParam("userId") userId: String): Response {
        val user = userRepository.find(userId) ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok(UserResponse(user.blankPassword())).build()
    }

    @GET
    @Path("/{userId}/login")
    @Produces(MediaType.APPLICATION_JSON)
    fun login(@PathParam("userId") userId: String, @QueryParam("password") @DefaultValue("") password: String): Response {
        val user = userRepository.find(userId) ?: return Response.status(Response.Status.NOT_FOUND).build()
        return when (user.password == password) {
            true -> Response.ok(UserResponse(user)).build()
            false -> Response.status(Response.Status.UNAUTHORIZED).build()
        }
    }
}