package com.etermax.tindercrack.server.image

import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.ImageTranscoder
import org.apache.batik.transcoder.image.PNGTranscoder
import org.apache.http.client.methods.CloseableHttpResponse
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.*
import javax.imageio.ImageIO

class SvgToPngConverter(val useSvgExport: Boolean) {

    fun downloadSVGAndConvert(response: CloseableHttpResponse): BufferedImage {
        val tmpFile = File.createTempFile("transcoder", "temp.png")

        try {
            if (useSvgExport) {

                val tmpFile2 = File.createTempFile("origin", "temp2.svg")

                try {
                    FileOutputStream(tmpFile2).use { fos ->
                        response.entity.content.use { inputStream ->
                            var inByte = inputStream.read()
                            while (inByte != -1) {
                                fos.write(inByte)
                                inByte = inputStream.read()
                            }
                        }
                    }

                    val commandLine = "svgexport ${tmpFile2.absolutePath} ${tmpFile.absolutePath} png \"svg{background:white;}\""
                    executeCommandLine(commandLine)
                } finally {
                    if (tmpFile2.exists())
                        tmpFile2.delete()
                }

            } else {
                val t = PNGTranscoder()
                t.addTranscodingHint(ImageTranscoder.KEY_BACKGROUND_COLOR, Color.white)
                val input = TranscoderInput(response.entity.content)

                val ostream = FileOutputStream(tmpFile)
                val output = TranscoderOutput(ostream)

                try {
                    // Save the image.
                    t.transcode(input, output)
                } finally {
                    // Flush and close the stream.
                    ostream.flush()
                    ostream.close()
                }
            }
            return ImageIO.read(tmpFile)
        } finally {
            if (tmpFile.exists())
                tmpFile.delete()
        }
    }

    private fun executeCommandLine(commandLine: String) {
        val rt = Runtime.getRuntime()
        val p = rt.exec(commandLine)

        Thread(Runnable {
            val input = BufferedReader(InputStreamReader(p.inputStream))

            try {
                var line = input.readLine()
                while (line != null) {
                    println(line)
                    line = input.readLine()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()

        p.waitFor()
    }

}