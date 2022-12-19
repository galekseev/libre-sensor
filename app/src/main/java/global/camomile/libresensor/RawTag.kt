package global.camomile.libresensor

import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.experimental.and

@Suppress("ArrayInDataClass")
data class RawTag (val data: ByteArray, val tagId: String = "", val tagDate: Long = System.currentTimeMillis()){
    val indexTrend: Byte = getByte(offsetTrendIndex)
    val indexHistory: Byte = getByte(offsetHistoryIndex)
    val sensorAgeInMinutes: Int = getWord(offsetSensorAge)
    val sensorSerial: ByteArray = byteArrayOf(
        getByte(3),
        getByte(2),
        getByte(1),
        getByte(0),
    )

    fun trendValue(index: Byte): Int {
        return getWord(offsetTrendTable + index * tableEntrySize) and tableEntryMask
    }

    fun historyValue(index: Byte): Int {
        return getWord(offsetHistoryTable + index * tableEntrySize) and tableEntryMask
    }

    private fun getWord(offset: Int): Int {
        return getWord(data, offset)
    }

    private fun getByte(offset: Int): Byte {
        return data[offset] and 0xFF.toByte()
    }

    companion object {
        /*
            Libre v1 data layout
            0-3 - sensor serial number
            4-25 - unknown (???)
            26 - trend index
            27 - history index
            28-123 - trend table (16 values * 6 bytes)
            124-315 - history table (32 values * 6 bytes)
            316-317 - sensor age in minutes
            318-327 - unknown (???)
            328-360 - never changes (???)
         */
        private const val offsetTrendTable = 28
        private const val offsetHistoryTable = 124
        private const val offsetTrendIndex = 26
        private const val offsetHistoryIndex = 27
        private const val offsetSensorAge = 316
        private const val tableEntrySize = 6
        private const val tableEntryMask = 0x3FFF

        private fun makeWord(high: Byte, low: Byte): Int {
            return 0x100 * (high and 0xFF.toByte()) + (low and 0xFF.toByte())
        }

        private fun getWord(data: ByteArray, offset: Int): Int {
            return makeWord(data[offset + 1], data[offset])
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