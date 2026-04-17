package io.github.yuazer.twilightcloudmon.security

import java.io.InputStream

/**
 * 资源加载器
 * 用于在运行时解密并加载加密的 JS 文件
 */
object ResourceLoader {
    
    /**
     * 默认解密密钥（必须与加密时使用的密钥一致）
     */
    private const val DEFAULT_KEY = "TwilightCloudMon2024"
    
    /**
     * 默认使用的解密算法
     */
    private val DEFAULT_ALGORITHM = SecurityFace.S3 // VigenereHuffman
    
    /**
     * 从资源中加载并解密文件内容
     * @param resourcePath 资源路径（相对于 resources 目录）
     * @param key 解密密钥（可选，默认使用 DEFAULT_KEY）
     * @param algorithm 解密算法（可选，默认使用 DEFAULT_ALGORITHM）
     * @return 解密后的文件内容
     */
    fun loadAndDecryptResource(
        resourcePath: String,
        key: String = DEFAULT_KEY,
        algorithm: SecurityFace = DEFAULT_ALGORITHM
    ): String {
        val inputStream = ResourceLoader::class.java.getResourceAsStream("/$resourcePath")
            ?: throw IllegalArgumentException("Resource not found: $resourcePath")
        
        val content = inputStream.bufferedReader().use { it.readText() }
        return try {
            algorithm.decrypt(content, key)
        } catch (e: Exception) {
            // 如果解密失败，可能是未加密的文件，直接返回原内容
            content
        }
    }
    
    /**
     * 从输入流中加载并解密内容
     * @param inputStream 输入流
     * @param key 解密密钥
     * @param algorithm 解密算法
     * @return 解密后的内容
     */
    fun loadAndDecryptFromStream(
        inputStream: InputStream,
        key: String = DEFAULT_KEY,
        algorithm: SecurityFace = DEFAULT_ALGORITHM
    ): String {
        val content = inputStream.bufferedReader().use { it.readText() }
        return try {
            algorithm.decrypt(content, key)
        } catch (e: Exception) {
            // 如果解密失败，可能是未加密的文件，直接返回原内容
            content
        }
    }
}

