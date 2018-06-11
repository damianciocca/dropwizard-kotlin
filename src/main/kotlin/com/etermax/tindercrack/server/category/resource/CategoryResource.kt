package com.etermax.tindercrack.server.category.resource

import com.etermax.tindercrack.server.category.repository.CategoryRepository
import com.etermax.tindercrack.server.category.resource.response.CategoryResponse
import java.util.concurrent.ThreadLocalRandom
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


@Path("/category")
class CategoryResource(private val categoryRepository: CategoryRepository) {

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    fun list(): Response {
        return Response.ok(categoryRepository.getCategories().map { category ->
            CategoryResponse(
                    category
            )
        }).build()
    }

    @GET
    @Path("/list_names")
    @Produces(MediaType.TEXT_PLAIN)
    fun listNames(): Response {
        return Response.ok(categoryRepository.getCategories().map { category ->
            category.name
        }.reduce { acc, s -> acc + "\n" + s }).build()
    }

    @GET
    @Path("/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    fun refresh(): Response {
        categoryRepository.loadCategories()
        return Response.ok().build()
    }

    @GET
    @Path("/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun listImages(@PathParam("categoryId") categoryId: String): Response {
        val category = categoryRepository.getCategory(categoryId) ?: return Response.status(Response.Status.NOT_FOUND).build()
        return Response.ok((0 until category.images.size).toList()).build()
    }

    @GET
    @Path("/{categoryId}/random")
    @Produces("image/jpeg")
    fun randomImage(@PathParam("categoryId") categoryId: String): Response {
        val category = categoryRepository.getCategory(categoryId) ?: return Response.status(Response.Status.NOT_FOUND).build()
        val randomImage = category.images[ThreadLocalRandom.current().nextInt(category.images.size)]
        return Response.ok(randomImage.readBytes()).build()
    }

    @GET
    @Path("/{categoryId}/{imageNumber}")
    @Produces("image/jpeg")
    fun image(@PathParam("categoryId") categoryId: String, @PathParam("imageNumber") imageNumber: Int): Response {
        val category = categoryRepository.getCategory(categoryId) ?: return Response.status(Response.Status.NOT_FOUND).build()

        if (imageNumber < 0 || imageNumber >= category.images.size)
            return Response.noContent().build()

        val image = category.images[imageNumber]
        return Response.ok(image.readBytes()).build()
    }
}
