package global.camomile.libresensor

import java.util.*
import kotlin.experimental.and
import kotlin.math.floor

@Suppress("ArrayInDataClass")
data class RawTag (val data: ByteArray, val tagId: String = "", val tagDate: Long = System.currentTimeMillis()){

    val indexTrend: Byte = getByte(offsetTrendIndex)
    val indexHistory: Byte = getByte(offsetHistoryIndex)
    val sensorAgeInMinutes: Int = getWord(offsetSensorAge)
    val sensorLifetime: Int = getWord(offsetSensorLifetime)
    val calibrationInfo: CalibrationInfo = readCalibrationInfo(data)

    fun trendValue(index: Byte): Int {
        return getWord(offsetTrendTable + index * tableEntrySize) and tableEntryMask
    }

    // TODO: Now should be the same as trendValue, decide which one to remove
    fun rawTrendValue(index: Byte): Int {
        val offset = offsetTrendTable + index * tableEntrySize
        return readBits(data, offset, 0, 0x0E)
    }

    // TODO: Research how calibration works
    fun calibratedTrendValue(index: Byte): Double {
        val offset = offsetTrendTable + index * tableEntrySize
        val raw = rawTrendValue(index).toDouble()
        val temp = (readBits(data, offset, 0x1a, 0xc) shl 2).toDouble()
        val tempAdj = (readBits(data, offset, 0x26, 0x9) shl 2).toDouble();
        val negTemp = readBits(data, offset, 0x2f, 0x1) != 0
        return calibrationInfo.calibrate(raw, temp, if (negTemp) -tempAdj else tempAdj)
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
            0-15 - unknown (???)
            16 - calibration info i1, i2 (i1 bits [0..2] i2 bits [4..13])
            17-26 - unknown (???)
            27 - history index
            28-123 - trend table (16 values * 6 bytes)
            124-315 - history table (32 values * 6 bytes)
            316-317 - sensor age in minutes
            318-325 - unknown (???)
            326-327 - sensor lifetime
            328-335 - unknown (???)
            336 - calibration info i3
            337-338 - calibration info i4
            360 - max
         */
        private const val offsetTrendTable = 28
        private const val offsetHistoryTable = 124
        private const val offsetTrendIndex = 26
        private const val offsetHistoryIndex = 27
        private const val offsetSensorAge = 316
        private const val offsetSensorLifetime = 326
        private const val tableEntrySize = 6
        private const val tableEntryMask = 0x3FFF

        private fun makeWord(high: Int, low: Int): Int {
            return ((0x100 * high) + (low and 0xFF))
        }

        private fun getWord(data: ByteArray, offset: Int): Int {
            return makeWord(data[offset + 1].toUByte().toInt(), data[offset].toUByte().toInt())
        }

        private fun readCalibrationInfo(data: ByteArray): CalibrationInfo {
            val i1 = readBits(data, 2, 0, 3)
            val i2 = readBits(data, 2, 3, 0x0A)
            val i3 = readBits(data, 0x150, 0, 8)
            val i4 = readBits(data, 0x150, 8, 0xe)
            val negativei3 = readBits(data, 0x150, 0x21, 1) != 0
            val i5 = readBits(data, 0x150, 0x28, 0xc) shl 2
            val i6 = readBits(data, 0x150, 0x34, 0xc) shl 2
            return CalibrationInfo(i1, i2, if (negativei3) -i3 else i3, i4, i5, i6)
        }

        private fun readBits(
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
                val byte1 = floor(totalBitOffset.toDouble() / 8).toInt()
                val bit = totalBitOffset % 8
                if (totalBitOffset >= 0 && ((buffer[byte1].toInt() shr bit) and 0x1) == 1) {
                    res = res or (1 shl i)
                }
            }
            return res
        }
    }
}