package io.github.yuazer.twilightcloudmon.security

object VigenereCipher {

    private const val ALPHABET_SIZE = 26

    private fun charToKeyValue(c: Char): Int = when {
        c in 'A'..'Z' -> c - 'A'
        c in 'a'..'z' -> c - 'a'
        c in '0'..'9' -> c - '0'
        else -> throw IllegalArgumentException("Invalid character: $c")
    }

    private fun prepareKey(key: String): String =
        key.filter { it.isLetterOrDigit() }.uppercase()

    fun encrypt(plaintext: String, key: String): String =
        transform(plaintext, prepareKey(key)) { charBase, keyValue -> (charBase + keyValue) % ALPHABET_SIZE }

    fun decrypt(ciphertext: String, key: String): String =
        transform(ciphertext, prepareKey(key)) { charBase, keyValue -> (charBase - keyValue + ALPHABET_SIZE) % ALPHABET_SIZE }

    private inline fun transform(
        text: String,
        effectiveKey: String,
        shift: (charBase: Int, keyValue: Int) -> Int
    ): String {
        val result = StringBuilder(text.length)
        var keyIndex = 0

        for (c in text) {
            when {
                c in 'a'..'z' -> {
                    val keyValue = charToKeyValue(effectiveKey[keyIndex % effectiveKey.length]) % ALPHABET_SIZE
                    result.append('a' + shift(c - 'a', keyValue))
                    keyIndex++
                }
                c in 'A'..'Z' -> {
                    val keyValue = charToKeyValue(effectiveKey[keyIndex % effectiveKey.length]) % ALPHABET_SIZE
                    result.append('A' + shift(c - 'A', keyValue))
                    keyIndex++
                }
                else -> result.append(c)
            }
        }
        return result.toString()
    }
}
