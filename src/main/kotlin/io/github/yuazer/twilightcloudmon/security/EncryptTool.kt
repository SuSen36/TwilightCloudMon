package io.github.yuazer.twilightcloudmon.security

import java.io.File

object EncryptTool {

    private const val DEFAULT_KEY = "TwilightCloudMon2024"
    private val DEFAULT_EXCLUDE = listOf("io/github/yuazer")

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            println("Usage: EncryptTool <resource-directory> [key] [exclude-dirs...]")
            println("Example: EncryptTool src/main/resources $DEFAULT_KEY io/github/yuazer")
            return
        }

        val resourceDir = File(args[0])
        val key = args.getOrElse(1) { DEFAULT_KEY }
        val excludeDirs = if (args.size > 2) args.drop(2) else DEFAULT_EXCLUDE

        if (!resourceDir.exists() || !resourceDir.isDirectory) {
            println("Error: Directory does not exist: ${resourceDir.absolutePath}")
            return
        }

        println("Encrypting all files in: ${resourceDir.absolutePath}")
        println("Using key: $key")
        println("Excluding directories: $excludeDirs")

        try {
            ResourceEncryptor.encryptDirectory(
                directory = resourceDir,
                key = key,
                algorithm = SecurityFace.S3,
                backup = true,
                excludeDirs = excludeDirs
            )
            println("Encryption completed successfully!")
        } catch (e: Exception) {
            println("Error during encryption: ${e.message}")
            e.printStackTrace()
        }
    }
}
