package global.camomile.libresensor

class TestUtils {
    companion object{
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
                if (bit % 7 == 0) builder.append(" ")
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
    }
}