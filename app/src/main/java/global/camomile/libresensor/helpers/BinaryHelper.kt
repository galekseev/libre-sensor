package global.camomile.libresensor.helpers

class BinaryHelper {
    companion object{
        fun hexStringToBytes(hexString: String): ByteArray {
            check(hexString.length % 2 == 0) { "Must have an even length" }

            return hexString.chunked(2)
                .map { it.toInt(16).toByte() }
                .toByteArray()
        }

        private fun makeWord(high: Int, low: Int): Int {
            return ((0x100 * high) + (low and 0xFF))
        }

        fun readWord(data: ByteArray, offset: Int): Int {
            return makeWord(data[offset + 1].toUByte().toInt(), data[offset].toUByte().toInt())
        }

        fun readBits(
            buffer: ByteArray,
            byteOffset: Int,
            bitOffset: Int,
            bitCount: Int
        ): Int {
            if (bitCount == 0) {
                return 0
            }
            var res = 0
            for (i in 0 until bitCount) {
                val totalBitOffset = byteOffset * 8 + bitOffset + i
                val byte1 = totalBitOffset / 8
                val bit = totalBitOffset % 8
                if (totalBitOffset >= 0 && ((buffer[byte1].toInt() shr bit) and 0x1) == 1) {
                    res = res or (1 shl i)
                }
            }
            return res
        }
    }
}