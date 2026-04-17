package io.github.yuazer.twilightcloudmon.security

/**
 * 维吉尼亚密码加密/解密工具
 */
object VigenereCipher {
    
    /**
     * 将字符转换为密钥数值（字母:0-25，数字:0-9）
     */
    private fun charToKeyValue(c: Char): Int {
        return when {
            c in 'A'..'Z' -> c - 'A'
            c in 'a'..'z' -> c - 'a'
            c in '0'..'9' -> c - '0'
            else -> throw IllegalArgumentException("Invalid character: $c")
        }
    }

    /**
     * 加密方法（支持密钥中的数字）
     * @param plaintext 明文
     * @param key 密钥（可包含字母和数字）
     * @return 加密后的密文
     */
    fun encrypt(plaintext: String, key: String): String {
        val ciphertext = StringBuilder()
        
        // 预处理密钥：只保留字母和数字，并转换为大写（数字保持不变）
        val processedKey = StringBuilder()
        for (c in key) {
            if ((c in 'A'..'Z') || (c in 'a'..'z') || (c in '0'..'9')) {
                processedKey.append(c.uppercaseChar())
            }
        }
        val effectiveKey = processedKey.toString()

        var j = 0
        for (c in plaintext) {
            try {
                when {
                    c in 'a'..'z' -> {
                        // 小写字母处理 - 统一使用模26的密钥值
                        val keyValue = charToKeyValue(effectiveKey[j % effectiveKey.length]) % 26
                        val shifted = (c - 'a' + keyValue) % 26
                        ciphertext.append('a' + shifted)
                        j++
                    }
                    c in 'A'..'Z' -> {
                        // 大写字母处理 - 统一使用模26的密钥值
                        val keyValue = charToKeyValue(effectiveKey[j % effectiveKey.length]) % 26
                        val shifted = (c - 'A' + keyValue) % 26
                        ciphertext.append('A' + shifted)
                        j++
                    }
                    else -> {
                        // 非字母字符直接添加
                        ciphertext.append(c)
                    }
                }
            } catch (e: Exception) {
                // 如果密钥用完且没有循环（理论上不会发生，因为j % effectiveKey.length）
                ciphertext.append(c)
            }
        }

        return ciphertext.toString()
    }

    /**
     * 解密方法（支持密钥中的数字）
     * @param ciphertext 密文
     * @param key 密钥（可包含字母和数字）
     * @return 解密后的明文
     */
    fun decrypt(ciphertext: String, key: String): String {
        val plaintext = StringBuilder()
        
        // 预处理密钥：只保留字母和数字，并转换为大写（数字保持不变）
        val processedKey = StringBuilder()
        for (c in key) {
            if ((c in 'A'..'Z') || (c in 'a'..'z') || (c in '0'..'9')) {
                processedKey.append(c.uppercaseChar())
            }
        }
        val effectiveKey = processedKey.toString()

        var j = 0
        for (c in ciphertext) {
            try {
                when {
                    c in 'a'..'z' -> {
                        // 小写字母处理 - 统一使用模26的密钥值
                        val keyValue = charToKeyValue(effectiveKey[j % effectiveKey.length]) % 26
                        val shifted = (c - 'a' - keyValue + 26) % 26
                        plaintext.append('a' + shifted)
                        j++
                    }
                    c in 'A'..'Z' -> {
                        // 大写字母处理 - 统一使用模26的密钥值
                        val keyValue = charToKeyValue(effectiveKey[j % effectiveKey.length]) % 26
                        val shifted = (c - 'A' - keyValue + 26) % 26
                        plaintext.append('A' + shifted)
                        j++
                    }
                    else -> {
                        // 非字母字符直接添加
                        plaintext.append(c)
                    }
                }
            } catch (e: Exception) {
                // 如果密钥用完且没有循环（理论上不会发生，因为j % effectiveKey.length）
                plaintext.append(c)
            }
        }

        return plaintext.toString()
    }
}

