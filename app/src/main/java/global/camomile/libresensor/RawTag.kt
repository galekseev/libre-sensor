package global.camomile.libresensor

import java.util.*
import kotlin.experimental.and

@Suppress("ArrayInDataClass")
data class RawTag (val data: ByteArray, val tagId: String = "", val tagDate: Long = System.currentTimeMillis()){

    val indexTrend: Int = getByte(offsetTrendIndex).toInt()
    val indexHistory: Int = getByte(offsetHistoryIndex).toInt()
    val sensorAgeInMinutes: Int = getWord(offsetSensorAge)
    val sensorLifetime: Int = getWord(offsetSensorLifetime)
    val calibrationInfo: CalibrationInfo = readCalibrationInfo(data)

    fun trendValue(index: Int): Int {
        return getTableValue(index, offsetTrendTable)
    }

    fun historyValue(index: Int): Int {
        return getTableValue(index, offsetHistoryTable)
    }

    private fun getTableValue(index: Int, offset: Int): Int{
        //return readBits(data, offset + index * tableEntrySize, 0, 0x0E)
        return getWord(offset + index * tableEntrySize) and tableEntryMask
    }

    fun trendTemperature(index: Int): Int {
        return getTemperature(index, offsetTrendTable)
    }

    fun historyTemperature(index: Int): Int {
        return getTemperature(index, offsetHistoryTable)
    }

    private fun getTemperature(index: Int, offset: Int): Int {
        return readBits(data, offset + index * tableEntrySize, 0x1a, 0xc) shl 2
    }

    fun trendTempAdjustment(index: Int): Int {
        return  getTempAdjustment(index, offsetTrendTable)
    }

    fun historyTempAdjustment(index: Int): Int {
        return getTempAdjustment(index, offsetHistoryTable)
    }

    private fun getTempAdjustment(index: Int, offset: Int): Int {
        val temperatureAdjustment = readBits(data,  offset + index * tableEntrySize, 0x26, 0x9) shl 2
        val negAdj = readBits(data, offset + index * tableEntrySize, 0x2f, 0x1) != 0
        return if (negAdj) -temperatureAdjustment else temperatureAdjustment
    }

    fun trendQualityFlags(index: Int): Int {
        return getQualityFlags(index, offsetTrendTable)
    }

    fun historyQualityFags(index: Int): Int {
        return getQualityFlags(index, offsetHistoryTable)
    }

    private fun getQualityFlags(index: Int, offset: Int): Int {
        return (readBits(data, offset + index * tableEntrySize, 0xe, 0xc) and 0x600) shr 9
    }

    fun trendHasError(index: Int): Int {
        return hasError(index, offsetTrendTable)
    }

    fun historyHasError(index: Int): Int {
        return hasError(index, offsetHistoryTable)
    }
    private fun hasError(index: Int, offset: Int): Int {
        return readBits(data, offset + index * tableEntrySize, 0x19, 0x1)
    }

    fun trendQuality(index: Int): Int {
        return getQuality(index, offsetTrendTable)
    }

    fun historyQuality(index: Int): Int {
        return getQuality(index, offsetHistoryTable)
    }

    private fun getQuality(index: Int, offset: Int): Int {
        return readBits(data, offset + index * tableEntrySize, 0xe, 0xb) and 0x1ff
    }

    // TODO: Research how calibration works
    fun calibratedTrendValue(index: Int): Double {
        val raw = trendValue(index).toDouble()
        val temp = trendTemperature(index).toDouble()
        val tempAdj = trendTempAdjustment(index).toDouble();
        return calibrationInfo.calibrate(raw, temp, tempAdj)
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