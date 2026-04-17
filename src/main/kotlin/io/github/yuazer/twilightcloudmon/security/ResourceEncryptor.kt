package io.github.yuazer.twilightcloudmon.security

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption

/**
 * 资源加密工具
 * 用于加密 yuazer 目录下的 JS 文件
 */
object ResourceEncryptor {
    
    /**
     * 默认加密密钥（建议在生产环境中使用更复杂的密钥）
     */
    private const val DEFAULT_KEY = "TwilightCloudMon2024"
    
    /**
     * 默认使用的加密算法
     */
    private val DEFAULT_ALGORITHM = SecurityFace.S3 // VigenereHuffman
    
    /**
     * 加密文件
     * @param inputFile 输入文件（明文）
     * @param outputFile 输出文件（密文，可选，如果为null则覆盖原文件）
     * @param key 加密密钥（可选，默认使用 DEFAULT_KEY）
     * @param algorithm 加密算法（可选，默认使用 DEFAULT_ALGORITHM）
     */
    fun encryptFile(
        inputFile: File,
        outputFile: File? = null,
        key: String = DEFAULT_KEY,
        algorithm: SecurityFace = DEFAULT_ALGORITHM
    ) {
        if (!inputFile.exists()) {
            throw IllegalArgumentException("Input file does not exist: ${inputFile.absolutePath}")
        }
        
        val plainText = inputFile.readText()
        val encryptedText = algorithm.encrypt(plainText, key)
        
        val targetFile = outputFile ?: inputFile
        targetFile.writeText(encryptedText)
    }
    
    /**
     * 解密文件
     * @param inputFile 输入文件（密文）
     * @param outputFile 输出文件（明文，可选，如果为null则覆盖原文件）
     * @param key 解密密钥（可选，默认使用 DEFAULT_KEY）
     * @param algorithm 解密算法（可选，默认使用 DEFAULT_ALGORITHM）
     */
    fun decryptFile(
        inputFile: File,
        outputFile: File? = null,
        key: String = DEFAULT_KEY,
        algorithm: SecurityFace = DEFAULT_ALGORITHM
    ): String {
        if (!inputFile.exists()) {
            throw IllegalArgumentException("Input file does not exist: ${inputFile.absolutePath}")
        }
        
        val encryptedText = inputFile.readText()
        val decryptedText = algorithm.decrypt(encryptedText, key)
        
        val targetFile = outputFile
        if (targetFile != null) {
            targetFile.writeText(decryptedText)
        }
        
        return decryptedText
    }
    
    /**
     * 批量加密目录下的所有文件
     * @param directory 目录路径
     * @param key 加密密钥
     * @param algorithm 加密算法
     * @param backup 是否备份原文件（添加 .bak 后缀）
     * @param excludeDirs 要排除的目录路径列表（相对于 directory）
     * @param fileExtensions 要加密的文件扩展名列表（如果为空，则加密所有文件）
     * @param excludeFiles 要排除的文件名列表（如 fabric.mod.json）
     */
    fun encryptDirectory(
        directory: File,
        key: String = DEFAULT_KEY,
        algorithm: SecurityFace = DEFAULT_ALGORITHM,
        backup: Boolean = true,
        excludeDirs: List<String> = emptyList(),
        fileExtensions: List<String> = emptyList(),
        excludeFiles: List<String> = listOf("fabric.mod.json", "twilightcloudmon.mixins.json") // 需要保留的文件
    ) {
        if (!directory.exists() || !directory.isDirectory) {
            throw IllegalArgumentException("Directory does not exist or is not a directory: ${directory.absolutePath}")
        }
        
        val excludePaths = excludeDirs.map { File(directory, it).absolutePath }
        
        directory.walkTopDown()
            .filter { file ->
                // 必须是文件
                if (!file.isFile) return@filter false
                
                // 排除指定文件名
                if (excludeFiles.contains(file.name)) {
                    return@filter false
                }
                
                // 排除指定目录下的文件
                val filePath = file.absolutePath
                if (excludePaths.any { filePath.startsWith(it) }) {
                    return@filter false
                }
                
                // 如果指定了文件扩展名，只加密匹配的文件
                if (fileExtensions.isNotEmpty()) {
                    return@filter fileExtensions.contains(file.extension.lowercase())
                }
                
                // 默认加密所有文件
                true
            }
            .forEach { file ->
                try {
                    if (backup) {
                        val backupFile = File(file.parent, "${file.name}.bak")
                        file.copyTo(backupFile, overwrite = true)
                    }
                    encryptFile(file, null, key, algorithm)
                    println("Encrypted: ${file.absolutePath}")
                } catch (e: Exception) {
                    println("Error encrypting ${file.absolutePath}: ${e.message}")
                }
            }
    }
    
    /**
     * 从资源流中解密内容
     * @param resourcePath 资源路径
     * @param key 解密密钥
     * @param algorithm 解密算法
     * @return 解密后的内容
     */
    fun decryptResource(
        resourcePath: String,
        key: String = DEFAULT_KEY,
        algorithm: SecurityFace = DEFAULT_ALGORITHM
    ): String {
        val inputStream = ResourceEncryptor::class.java.getResourceAsStream(resourcePath)
            ?: throw IllegalArgumentException("Resource not found: $resourcePath")
        
        val encryptedText = inputStream.bufferedReader().use { it.readText() }
        return algorithm.decrypt(encryptedText, key)
    }
}

