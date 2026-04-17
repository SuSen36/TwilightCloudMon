package io.github.yuazer.twilightcloudmon.security

import java.io.File

/**
 * 加密工具
 * 用于手动加密资源文件
 * 
 * 使用方法：
 * 1. 在 IDE 中运行 main 函数
 * 2. 或者通过命令行调用
 */
object EncryptTool {
    
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            println("Usage: EncryptTool <resource-directory> [key] [exclude-dirs...]")
            println("Example: EncryptTool src/main/resources TwilightCloudMon2024 io/github/yuazer")
            return
        }
        
        val resourceDir = File(args[0])
        val key = if (args.size > 1) args[1] else "TwilightCloudMon2024"
        val excludeDirs = if (args.size > 2) args.drop(2) else listOf("io/github/yuazer")
        
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
                algorithm = SecurityFace.S3, // VigenereHuffman
                backup = true,
                excludeDirs = excludeDirs,
                fileExtensions = emptyList() // 加密所有文件
            )
            println("Encryption completed successfully!")
        } catch (e: Exception) {
            println("Error during encryption: ${e.message}")
            e.printStackTrace()
        }
    }
}

