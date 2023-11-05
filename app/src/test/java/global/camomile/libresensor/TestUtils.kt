package global.camomile.libresensor

class TestUtils {
    companion object{
        val colors = arrayOf(
            "\u001B[31m", //red
            "\u001B[36m", //cyan
            "\u001B[33m", //yellow
            "\u001B[35m", //magenta
            "\u001B[34m", //blue
            "\u001B[32m", //green
            "\u001B[37m", //white
            "\u001B[30m" //black
        )
        fun bytesToInt(b0: Byte, b1: Byte, b2: Byte, b3: Byte): Int {
            return (b0.toInt() and 0xFF shl 24) or
                    (b1.toInt() and 0xFF shl 16) or
                    (b2.toInt() and 0xFF shl 8) or
                    (b3.toInt() and 0xFF)
        }

        fun bytesToLong(b0: Byte, b1: Byte, b2: Byte, b3: Byte, b4: Byte, b5: Byte, b6:Byte, b7: Byte): Long {
            return (b0.toLong() and 0xFF shl 56) or
                    (b1.toLong() and 0xFF shl 48) or
                    (b2.toLong() and 0xFF shl 40) or
                    (b3.toLong() and 0xFF shl 32) or
                    (b4.toLong() and 0xFF shl 24) or
                    (b5.toLong() and 0xFF shl 16) or
                    (b6.toLong() and 0xFF shl 8) or
                    (b7.toLong() and 0xFF)
        }
        fun toBinString(byte: Byte): String {
            val builder = StringBuilder("")
            for (bit in 7 downTo 0) {
                builder.append((byte.toInt() shr bit) and 0x1)
            }
            return builder.toString()
        }

        fun toBinString(byte: Int, bits: Int = 32): String {
            val size = if (bits > 32) 31 else if (bits < 0) 0 else bits-1
            val builder = StringBuilder("")
            for (bit in size downTo 0) {
                builder.append((byte shr bit) and 0x1)
                if (bit % 8 == 0) builder.append(" ")
            }
            return builder.toString()
        }

        fun toBinString(byte: Int, bits: Int = 32, mask: Long = 0xFFFFFFFF): String {
            val size = if (bits > 32) 31 else if (bits < 0) 0 else bits-1
            val builder = StringBuilder("")
            for (bit in size downTo 0) {
                if ((mask shr bit).toInt() and 0x1 == 0x1)
                    builder.append("\u001b[31m")
                else
                    builder.append("\u001b[0m")
                builder.append((byte shr bit) and 0x1)
                if (bit % 8 == 0) builder.append(" ")
            }
            builder.append("\u001b[0m")
            return builder.toString()
        }

        fun toBinString(bytes: Long, bits: Int = 64, mask: Long = 0xFFFFFFFFFFFFFFF): String {
            val size = if (bits > 64) 63 else if (bits < 0) 0 else bits-1
            val builder = StringBuilder("")
            for (bit in size downTo 0) {
                if ((mask shr bit).toInt() and 0x1 == 0x1)
                    builder.append("\u001b[31m")
                else
                    builder.append("\u001b[0m")
                builder.append((bytes shr bit) and 0x1)
                if (bit % 8 == 0) builder.append(" ")
            }
            builder.append("\u001b[0m")
            return builder.toString()
        }

        fun toBinString(bytes: Long, bits: Int = 64, masks: Array<Long>): String {
            val size = if (bits > 64) 63 else if (bits < 0) 0 else bits-1
            val builder = StringBuilder("")
            for (bit in size downTo 0) {
                var colorApplied = false
                for ((colorIndex, mask) in masks.withIndex()) {
                    if (((mask shr bit) and 0x1) == 1L) {
                        builder.append(colors[colorIndex % colors.size])
                        colorApplied = true
                        break
                    }
                }

                if (!colorApplied) {
                    builder.append("\u001B[0m") // Reset to default color if no mask applies
                }

                builder.append((bytes shr bit) and 0x1)
                if (bit % 8 == 0) builder.append(" ")
            }
            builder.append("\u001b[0m")
            return builder.toString()
        }

        fun toBinString(byte: Byte, mask: Byte): String {
            val builder = StringBuilder("")
            for (bit in 7 downTo  0) {
                if (mask.toInt() shr bit and 0x1 == 0x1)
                    builder.append("\u001b[31m")
                else
                    builder.append("\u001b[0m")
                builder.append((byte.toInt() shr bit) and 0x1)
            }
            builder.append("\u001b[0m")
            return builder.toString()
        }

        fun toBinString(bytes: ByteArray): String {
            val builder = StringBuilder("")
            for (element in bytes) {
                builder.append(toBinString(element))
                builder.append(" ")
            }
            return builder.toString()
        }

        fun toBinString(bytes: ByteArray, mask: ByteArray): String {
            val builder = StringBuilder("")
            for (index in bytes.indices) {
                builder.append(toBinString(bytes[index], mask[index]))
                builder.append(" ")
            }
            return builder.toString()
        }

        fun toHexString(src: ByteArray): String {
            val builder = StringBuilder("")
            val buffer = CharArray(2)
            for (b in src) {
                buffer[0] = Character.forDigit(b.toInt() ushr 4 and 0x0F, 16)
                buffer[1] = Character.forDigit(b.toInt() and 0x0F, 16)
                builder.append(buffer)
            }
            return builder.toString()
        }

        fun binary2bPrettyPrint(
            argName: String, number: Int, numberBits: Int, numberMask: Long,
            byteHigh: Byte, byteLow: Byte, bytesMask: Long, rawNumber: Int = number
        ): String {
            val numberInt = toBinString(rawNumber, numberBits, numberMask)
            val numberBin = toBinString(bytesToInt(0, 0, byteHigh, byteLow), numberBits, bytesMask)
            return if (number == rawNumber)
                "bytes: $numberBin int: $numberInt $argName: $number"
            else
                "bytes: $numberBin int: $numberInt raw: $rawNumber $argName: $number"
        }

    }
}