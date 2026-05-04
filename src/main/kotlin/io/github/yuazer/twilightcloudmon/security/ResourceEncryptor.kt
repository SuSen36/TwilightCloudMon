package io.github.yuazer.twilightcloudmon.security

import java.io.File

object ResourceEncryptor {

    private const val DEFAULT_KEY = "TwilightCloudMon2024"
    private val DEFAULT_ALGORITHM = SecurityFace.S3

    private val DEFAULT_EXCLUDE_FILES = listOf("fabric.mod.json", "twilightcloudmon.mixins.json")

    fun encryptFile(
        inputFile: File,
        outputFile: File? = null,
        key: String = DEFAULT_KEY,
        algorithm: SecurityFace = DEFAULT_ALGORITHM
    ) {
        require(inputFile.exists()) { "Input file does not exist: ${inputFile.absolutePath}" }
        (outputFile ?: inputFile).writeText(algorithm.encrypt(inputFile.readText(), key))
    }

    fun decryptFile(
        inputFile: File,
        outputFile: File? = null,
        key: String = DEFAULT_KEY,
        algorithm: SecurityFace = DEFAULT_ALGORITHM
    ): String {
        require(inputFile.exists()) { "Input file does not exist: ${inputFile.absolutePath}" }
        val decryptedText = algorithm.decrypt(inputFile.readText(), key)
        outputFile?.writeText(decryptedText)
        return decryptedText
    }

    fun encryptDirectory(
        directory: File,
        key: String = DEFAULT_KEY,
        algorithm: SecurityFace = DEFAULT_ALGORITHM,
        backup: Boolean = true,
        excludeDirs: List<String> = emptyList(),
        fileExtensions: List<String> = emptyList(),
        excludeFiles: List<String> = DEFAULT_EXCLUDE_FILES
    ) {
        require(directory.exists() && directory.isDirectory) {
            "Directory does not exist or is not a directory: ${directory.absolutePath}"
        }

        val excludePaths = excludeDirs.map { File(directory, it).absolutePath }

        directory.walkTopDown()
            .filter { file ->
                file.isFile
                        && file.name !in excludeFiles
                        && excludePaths.none { file.absolutePath.startsWith(it) }
                        && (fileExtensions.isEmpty() || file.extension.lowercase() in fileExtensions)
            }
            .forEach { file ->
                try {
                    if (backup) file.copyTo(File(file.parent, "${file.name}.bak"), overwrite = true)
                    encryptFile(file, null, key, algorithm)
                    println("Encrypted: ${file.absolutePath}")
                } catch (e: Exception) {
                    println("Error encrypting ${file.absolutePath}: ${e.message}")
                }
            }
    }

    fun decryptResource(
        resourcePath: String,
        key: String = DEFAULT_KEY,
        algorithm: SecurityFace = DEFAULT_ALGORITHM
    ): String {
        val inputStream = ResourceEncryptor::class.java.getResourceAsStream(resourcePath)
            ?: throw IllegalArgumentException("Resource not found: $resourcePath")
        return algorithm.decrypt(inputStream.bufferedReader().use { it.readText() }, key)
    }
}
