package io.github.yuazer.twilightcloudmon.security

import java.io.InputStream

object ResourceLoader {

    private const val DEFAULT_KEY = "TwilightCloudMon2024"
    private val DEFAULT_ALGORITHM = SecurityFace.S3

    fun loadAndDecryptResource(
        resourcePath: String,
        key: String = DEFAULT_KEY,
        algorithm: SecurityFace = DEFAULT_ALGORITHM
    ): String {
        val inputStream = ResourceLoader::class.java.getResourceAsStream("/$resourcePath")
            ?: throw IllegalArgumentException("Resource not found: $resourcePath")
        return decryptOrFallback(inputStream, key, algorithm)
    }

    fun loadAndDecryptFromStream(
        inputStream: InputStream,
        key: String = DEFAULT_KEY,
        algorithm: SecurityFace = DEFAULT_ALGORITHM
    ): String = decryptOrFallback(inputStream, key, algorithm)

    private fun decryptOrFallback(
        inputStream: InputStream,
        key: String,
        algorithm: SecurityFace
    ): String {
        val content = inputStream.bufferedReader().use { it.readText() }
        return runCatching { algorithm.decrypt(content, key) }.getOrDefault(content)
    }
}
