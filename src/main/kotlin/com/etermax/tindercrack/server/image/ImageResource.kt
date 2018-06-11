package com.etermax.tindercrack.server.image

import mu.KLogging
import javax.ws.rs.*
import javax.ws.rs.core.Response


@Path("/image")
class ImageResource(private val cache: ImageCache, private val imageUtils: ImageUtils) {

    companion object : KLogging()

    @GET
    @Path("/download")
    @Produces("image/jpeg")
    fun download(@QueryParam("url") @DefaultValue("") url: String): Response {

        var cachedBytes = cache.get(url)

        if (cachedBytes == null) {
            val image = imageUtils.downloadImage(url)

            if (image != null) {
                var resizedImage = imageUtils.resizeImageToMax(image, 512)
                cachedBytes = imageUtils.getImageBytesAsJPG(resizedImage)
                cache.add(url, cachedBytes)
            } else {
                logger.info { "Image conversion failed, returning empty image" }
                return Response.ok(imageUtils.getImageBytesAsJPG(imageUtils.emptyImage())).build()
            }
        }
        return Response.ok(cachedBytes).build()
    }
}
