package global.camomile.libresensor

import global.camomile.libresensor.helpers.BinaryHelper
import java.util.*
import kotlin.experimental.and

@Suppress("ArrayInDataClass")
data class RawTag (val data: ByteArray, val tagId: String = "", val tagDate: Long = System.currentTimeMillis()){
    val indexTrend: Int = getByte(offsetTrendIndex).toInt()
    val indexHistory: Int = getByte(offsetHistoryIndex).toInt()
    val sensorAgeInMinutes: Int = getWord(offsetSensorAge)
    val sensorLifetime: Int = getWord(offsetSensorLifetime)
    val calibrationInfo: CalibrationInfo = readCalibrationInfo(data)
    val sensorState: Byte = getByte(0x4)
    val sensorRegion: Int = getByte(0x143).toInt()

    fun tableValue(index: Int, offset: Int): Int{
        //return readBits(data, offset + index * tableEntrySize, 0, 0x0E)
        return getWord(offset + index * tableEntrySize) and tableEntryMask
    }

    fun temperature(index: Int, offset: Int): Int {
        return getBits(offset + index * tableEntrySize, 0x1a, 0xc) shl 2
    }

    fun tempAdjustment(index: Int, offset: Int): Int {
        val tempAdjustment = getBits( offset + index * tableEntrySize, 0x26, 0x9) shl 2
        val negAdj = getBits(offset + index * tableEntrySize, 0x2f, 0x1) != 0
        return if (negAdj) -tempAdjustment else tempAdjustment
    }

    fun quality(index: Int, offset: Int): Int {
        return getBits(offset + index * tableEntrySize, 0xe, 0xb) and 0x1ff
    }

    fun qualityFlags(index: Int, offset: Int): Int {
        return (getBits(offset + index * tableEntrySize, 0xe, 0xc) and 0x600) shr 9
    }

    fun hasError(index: Int, offset: Int): Boolean {
        return getBits(offset + index * tableEntrySize, 0x19, 0x1) == 1
    }

//    TODO: Research how calibration works
//    fun calibratedTrendValue(index: Int): Double {
//        val raw = tableValue(index, offsetTrendTable).toDouble()
//        val temp = temperature(index, offsetTrendTable).toDouble()
//        val tempAdj = tempAdjustment(index, offsetTrendTable).toDouble();
//        return calibrationInfo.calibrate(raw, temp, tempAdj)
//    }

    private fun getWord(offset: Int): Int {
        return BinaryHelper.readWord(data, offset)
    }

    private fun getByte(offset: Int): Byte {
        return data[offset] and 0xFF.toByte()
    }

    private fun getBits(
        byteOffset: Int,
        bitOffset: Int,
        bitCount: Int
    ): Int {
        return BinaryHelper.readBits(data, byteOffset, bitOffset, bitCount)
    }

    companion object {
        /*
            https://github.com/UPetersen/LibreMonitor/wiki
            https://github.com/captainbeeheart/openfreestyle/blob/master/docs/reverse.md
            https://passthesalt.ubicast.tv/protected/videos/v125f57aae7bfmvm2r6cajw3qr9nws/attachments/pts2020_talk_15_pique_curiosity_not_diabetic_fingers_technical_report.pdf
        */
        const val offsetTrendTable = 28
        const val offsetHistoryTable = 124
        private const val offsetTrendIndex = 26
        private const val offsetHistoryIndex = 27
        private const val offsetSensorAge = 316
        private const val offsetSensorLifetime = 326
        private const val tableEntrySize = 6
        private const val tableEntryMask = 0x3FFF

        private fun readCalibrationInfo(data: ByteArray): CalibrationInfo {
            val i1 = BinaryHelper.readBits(data, 2, 0, 3)
            val i2 = BinaryHelper.readBits(data, 2, 3, 0x0A)
            val i3 = BinaryHelper.readBits(data, 0x150, 0, 8)
            val i4 = BinaryHelper.readBits(data, 0x150, 8, 0xe)
            val negativei3 = BinaryHelper.readBits(data, 0x150, 0x21, 1) != 0
            val i5 = BinaryHelper.readBits(data, 0x150, 0x28, 0xc) shl 2
            val i6 = BinaryHelper.readBits(data, 0x150, 0x34, 0xc) shl 2
            return CalibrationInfo(i1, i2, if (negativei3) -i3 else i3, i4, i5, i6)
        }
    }
}