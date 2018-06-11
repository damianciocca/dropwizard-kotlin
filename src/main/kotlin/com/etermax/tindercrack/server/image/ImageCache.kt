package com.etermax.tindercrack.server.image

import mu.KLogging
import java.io.File
import java.security.MessageDigest

class ImageCache(private val cacheFilesPath: String) {

    companion object : KLogging()

    init {
        if (!File(cacheFilesPath).exists() || !File(cacheFilesPath).isDirectory()) {
            throw Exception("Invalid cacheFilesPath value '$cacheFilesPath' in config.yml")
        }
    }

    fun add(url: String, bytes: ByteArray) {

        val cacheFilesPath = getCacheFilePathFromUrl(url)

        try {
            File(cacheFilesPath).writeBytes(bytes)
        } catch (ex: Exception) {
            logger.error(ex, { "Exception" })
        }
    }

    fun get(url: String): ByteArray? {

        val cacheFilesPath = getCacheFilePathFromUrl(url)

        val file = File(cacheFilesPath)

        try {
            if (file.exists()) {
                return file.readBytes()
            }
        } catch (ex: Exception) {
            logger.error(ex, { "Exception" })
        }
        return null
    }

    private fun getCacheFilePathFromUrl(url: String): String {
        val bytesOfMessage = url.toByteArray()
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(bytesOfMessage)
        val digestString = bytesToHex(digest)
        return "$cacheFilesPath/$digestString.jpg"
    }

    private val hexArray = "0123456789ABCDEF".toCharArray()
    fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v.ushr(4)]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }
}