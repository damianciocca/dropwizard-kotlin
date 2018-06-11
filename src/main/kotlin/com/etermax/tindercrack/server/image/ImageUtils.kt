package com.etermax.tindercrack.server.image

import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.urllib.Urls
import java.awt.Color
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class ImageUtils(val svgToPngConverter: SvgToPngConverter) {

    fun emptyImage(): BufferedImage {
        val bi = BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB)
        val g = bi.graphics
        g.color = Color.WHITE
        g.fillRect(0, 0, 64, 64)
        g.dispose()
        return bi
    }

    fun downloadImage(url: String): BufferedImage? {
        val uri = Urls.createURI(url)
        val httpclient = HttpClients.createDefault()
        val httpGet = HttpGet(uri)
        val response = httpclient.execute(httpGet)

        return downloadImage(url, response)
    }

    private fun downloadImage(url: String, response: CloseableHttpResponse): BufferedImage? {
        var image: BufferedImage? = null

        try {
            response.use { res ->
                if (isSVG(url)) {
                    image = svgToPngConverter.downloadSVGAndConvert(res)
                } else {
                    image = ImageIO.read(res.entity.content)
                }
            }
        } catch (ex: Exception) {
            ImageResource.logger.error(ex, { "Image conversion failed for $url" })
        }
        return image
    }

    private fun isSVG(url: String) = url.contains(".svg", true)

    fun resizeImageToMax(image: BufferedImage, maxSize: Int): BufferedImage {
        val max = Math.max(image.width, image.height)

        var scale = maxSize.toFloat() / max.toFloat()

        if (scale > 1.0f)
            scale = 1.0f

        return resize(image, scale.toDouble())
    }


    private fun resize(inputImage: BufferedImage, scaledWidth: Int, scaledHeight: Int): BufferedImage {
        // creates output image
        val outputImage = BufferedImage(scaledWidth, scaledHeight, ColorSpace.TYPE_RGB)

        // scales the input image to the output image
        val g2d = outputImage.createGraphics()
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, Color.WHITE, null)
        g2d.dispose()

        return outputImage
    }

    private fun resize(inputImage: BufferedImage, percent: Double): BufferedImage {
        val scaledWidth = (inputImage.width * percent).toInt()
        val scaledHeight = (inputImage.height * percent).toInt()
        return resize(inputImage, scaledWidth, scaledHeight)
    }

    fun getImageBytesAsJPG(resizedImage: BufferedImage): ByteArray {
        val baos = ByteArrayOutputStream()
        ImageIO.write(resizedImage, "jpg", baos)
        baos.flush()
        val imageInByte = baos.toByteArray()
        baos.close()
        return imageInByte
    }


}