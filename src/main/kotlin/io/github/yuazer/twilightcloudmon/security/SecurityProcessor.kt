package io.github.yuazer.twilightcloudmon.security

/**
 * 加密算法接口
 */
interface IWithKeySecurity {
    fun encrypt(plainText: String, key: String): String
    fun decrypt(cipherText: String, key: String): String
}

/**
 * 加密算法枚举
 */
enum class SecurityProcessor : IWithKeySecurity {
    /**
     * 维吉尼亚密码
     */
    Vigenere {
        override fun encrypt(plainText: String, key: String): String {
            return VigenereCipher.encrypt(plainText, key)
        }

        override fun decrypt(cipherText: String, key: String): String {
            return VigenereCipher.decrypt(cipherText, key)
        }
    },
    
    /**
     * 哈夫曼编码，此时key无效
     */
    Huffman {
        override fun encrypt(plainText: String, key: String): String {
            return HuffmanCodingWithEmbeddedTree().encodeWithEmbeddedTree(plainText)
        }

        override fun decrypt(cipherText: String, key: String): String {
            return try {
                HuffmanCodingWithEmbeddedTree().decodeWithEmbeddedTree(cipherText)
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid encrypted text", e)
            }
        }
    },
    
    /**
     * 维吉尼亚密码+哈夫曼编码
     */
    VigenereHuffman {
        override fun encrypt(plainText: String, key: String): String {
            return Huffman.encrypt(Vigenere.encrypt(plainText, key), key)
        }

        override fun decrypt(cipherText: String, key: String): String {
            return Vigenere.decrypt(Huffman.decrypt(cipherText, key), key)
        }
    }
}

/**
 * 使用一个无实际意义命名的类包装实际的策略，更加安全
 */
enum class SecurityFace(private val processor: SecurityProcessor) : IWithKeySecurity {
    S1(SecurityProcessor.Vigenere),
    S2(SecurityProcessor.Huffman),
    S3(SecurityProcessor.VigenereHuffman);

    override fun encrypt(plainText: String, key: String): String {
        return processor.encrypt(plainText, key)
    }

    override fun decrypt(cipherText: String, key: String): String {
        return processor.decrypt(cipherText, key)
    }
}

