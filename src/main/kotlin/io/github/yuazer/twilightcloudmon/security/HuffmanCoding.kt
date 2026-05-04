package io.github.yuazer.twilightcloudmon.security

import java.io.StringReader
import java.util.PriorityQueue

class HuffmanCodingWithEmbeddedTree {
    private var huffmanCodes: MutableMap<Char, String>? = null
    private var root: HuffmanNode? = null

    private fun buildTree(text: String) {
        if (text.isEmpty()) {
            throw IllegalArgumentException("Text cannot be empty")
        }

        val frequencyMap = mutableMapOf<Char, Int>()
        for (c in text) {
            frequencyMap[c] = frequencyMap.getOrDefault(c, 0) + 1
        }

        val priorityQueue = PriorityQueue<HuffmanNode>()
        for ((char, freq) in frequencyMap) {
            priorityQueue.add(HuffmanNode(char, freq))
        }

        while (priorityQueue.size > 1) {
            val left = priorityQueue.poll()
            val right = priorityQueue.poll()
            val parent = HuffmanNode(left.frequency + right.frequency, left, right)
            priorityQueue.add(parent)
        }

        root = priorityQueue.poll()
        huffmanCodes = mutableMapOf()
        buildCodeTable(root!!, "")
    }

    private fun buildCodeTable(node: HuffmanNode?, code: String) {
        if (node == null) return
        if (node.isLeaf()) {
            huffmanCodes!![node.data!!] = code
            return
        }
        buildCodeTable(node.left, code + "0")
        buildCodeTable(node.right, code + "1")
    }

    fun encodeWithEmbeddedTree(text: String): String {
        if (huffmanCodes == null || huffmanCodes!!.isEmpty()) {
            buildTree(text)
        }

        val treeString = serializeTreeToString(root!!)
        val encodedText = encode(text)

        return "$treeString|$encodedText"
    }

    private fun serializeTreeToString(node: HuffmanNode): String {
        val sb = StringBuilder()
        serializeNode(node, sb)
        return sb.toString()
    }

    private fun serializeNode(node: HuffmanNode?, sb: StringBuilder) {
        if (node == null) {
            sb.append("N")
            return
        }

        if (node.isLeaf()) {
            sb.append("L")
            if (node.data != null) {
                sb.append(escapeCharacter(node.data))
            }
            sb.append(",")
            sb.append(node.frequency)
            sb.append(",")
        } else {
            sb.append("B")
            sb.append(node.frequency)
            sb.append(",")
            serializeNode(node.left, sb)
            serializeNode(node.right, sb)
        }
    }

    private fun escapeCharacter(c: Char): String = when (c) {
        '\\', ',' -> "\\$c"
        else -> c.toString()
    }

    private fun deserializeTreeFromString(reader: StringReader): HuffmanNode? {
        val c = reader.read()
        if (c == -1) return null

        val type = c.toChar()
        if (type == 'N') {
            return null
        }

        if (type == 'L') {
            val charBuilder = StringBuilder()
            var escape = false
            var charC: Int
            while (reader.read().also { charC = it } != -1) {
                val ch = charC.toChar()
                if (escape) {
                    charBuilder.append(ch)
                    escape = false
                } else if (ch == '\\') {
                    escape = true
                } else if (ch == ',') {
                    break
                } else {
                    charBuilder.append(ch)
                }
            }
            val data = if (charBuilder.isNotEmpty()) charBuilder[0] else 0.toChar()

            val numBuilder = StringBuilder()
            while (reader.read().also { charC = it } != -1 && charC.toChar() != ',') {
                numBuilder.append(charC.toChar())
            }
            val frequency = numBuilder.toString().toInt()

            return HuffmanNode(data, frequency)
        } else if (type == 'B') {
            val numBuilder = StringBuilder()
            var charC: Int
            while (reader.read().also { charC = it } != -1 && charC.toChar() != ',') {
                numBuilder.append(charC.toChar())
            }
            val frequency = numBuilder.toString().toInt()

            val left = deserializeTreeFromString(reader)
            val right = deserializeTreeFromString(reader)
            return HuffmanNode(frequency, left, right)
        } else {
            throw IllegalArgumentException("Invalid node type: $type")
        }
    }

    fun decodeWithEmbeddedTree(encodedTextWithTree: String): String {
        val parts = encodedTextWithTree.split("|", limit = 2)
        if (parts.size != 2) {
            throw IllegalArgumentException("Invalid encoded text format")
        }

        val treeString = parts[0]
        val encodedText = parts[1]

        val reader = StringReader(treeString)
        root = deserializeTreeFromString(reader)

        huffmanCodes = mutableMapOf()
        buildCodeTable(root!!, "")

        return decode(encodedText)
    }

    private fun encode(text: String): String {
        if (huffmanCodes == null || huffmanCodes!!.isEmpty()) {
            throw IllegalStateException("Huffman codes not initialized")
        }

        val encodedText = StringBuilder()
        for (c in text) {
            encodedText.append(huffmanCodes!![c])
        }
        return encodedText.toString()
    }

    private fun decode(encodedText: String): String {
        if (root == null) {
            throw IllegalStateException("Huffman tree not initialized")
        }

        val decodedText = StringBuilder()
        var current = root

        for (bit in encodedText) {
            current = if (bit == '0') {
                current!!.left
            } else {
                current!!.right
            }

            if (current!!.isLeaf()) {
                decodedText.append(current.data)
                current = root
            }
        }

        if (current != root) {
            throw IllegalArgumentException("Invalid encoded text")
        }

        return decodedText.toString()
    }

    data class HuffmanNode(
        val data: Char?,
        val frequency: Int,
        val left: HuffmanNode? = null,
        val right: HuffmanNode? = null
    ) : Comparable<HuffmanNode> {
        constructor(frequency: Int, left: HuffmanNode?, right: HuffmanNode?) : this(null, frequency, left, right)

        fun isLeaf(): Boolean = left == null && right == null

        override fun compareTo(other: HuffmanNode): Int {
            return frequency.compareTo(other.frequency)
        }
    }
}

