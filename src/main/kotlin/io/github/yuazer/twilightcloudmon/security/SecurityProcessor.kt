package io.github.yuazer.twilightcloudmon.security

interface IWithKeySecurity {
    fun encrypt(plainText: String, key: String): String
    fun decrypt(cipherText: String, key: String): String
}

enum class SecurityProcessor : IWithKeySecurity {

    Vigenere {
        override fun encrypt(plainText: String, key: String) = VigenereCipher.encrypt(plainText, key)
        override fun decrypt(cipherText: String, key: String) = VigenereCipher.decrypt(cipherText, key)
    },

    Huffman {
        override fun encrypt(plainText: String, key: String) =
            HuffmanCodingWithEmbeddedTree().encodeWithEmbeddedTree(plainText)

        override fun decrypt(cipherText: String, key: String) = try {
            HuffmanCodingWithEmbeddedTree().decodeWithEmbeddedTree(cipherText)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid encrypted text", e)
        }
    },

    VigenereHuffman {
        override fun encrypt(plainText: String, key: String) =
            Huffman.encrypt(Vigenere.encrypt(plainText, key), key)

        override fun decrypt(cipherText: String, key: String) =
            Vigenere.decrypt(Huffman.decrypt(cipherText, key), key)
    }
}

enum class SecurityFace(private val processor: SecurityProcessor) : IWithKeySecurity {
    S1(SecurityProcessor.Vigenere),
    S2(SecurityProcessor.Huffman),
    S3(SecurityProcessor.VigenereHuffman);

    override fun encrypt(plainText: String, key: String) = processor.encrypt(plainText, key)
    override fun decrypt(cipherText: String, key: String) = processor.decrypt(cipherText, key)
}
