package global.camomile.libresensor

import androidx.core.location.LocationRequestCompat.Quality
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RawTagTest() {

    private val rawTag : ByteArray = RawTagReadings.DorianScholz
    private val tag : RawTag = RawTag(rawTag, "id")

    @Test
    fun trend(){
        val index = tag.indexTrend
        val trend = tag.trendValue(index);

        assertEquals(7, index)
        assertEquals(1805, trend)
    }

    @Test
    fun trendVStrend2(){
        println("trend index ${tag.indexTrend}")
        for (index in 0 until 16) {
            val trend = tag.trendValue(index)
            val calTrend = tag.calibratedTrendValue(index)
            val mmolTrend = Glucose.convertMGDLToMMOL(calTrend.toFloat())
            val glucose = Glucose(trend, 120, false)
            if (index == tag.indexTrend) print("LAST ")
            println("trend:$trend glucose:${glucose.glucose(true)} cal trend:$calTrend mmol:$mmolTrend")
        }
    }

    @Test
    fun viewTrend(){
        for (index in 0 until 16) {
            val byteIndex = 28 + index * 6
            val record = tag.data.sliceArray(byteIndex until byteIndex + 7)
            printRecord( record, index, byteIndex,
                tag.trendValue(index),
                tag.trendQuality(index),
                tag.trendQualityFlags(index),
                tag.trendHasError(index),
                tag.trendTemperature(index),
                tag.trendTempAdjustment(index)
            )
        }
    }

    @Test
    fun viewHistory(){
        for (index in 0 until 32) {
            val byteIndex = 124 + index * 6
            val record = tag.data.sliceArray(byteIndex until byteIndex + 7)
            printRecord( record, index, byteIndex,
                tag.trendValue(index),
                tag.trendQuality(index),
                tag.trendQualityFlags(index),
                tag.trendHasError(index),
                tag.trendTemperature(index),
                tag.trendTempAdjustment(index)
            )
        }
    }

    @Test
    fun viewCalibration(){
//        val i1 = RawTag.readBits(data, 2, 0, 3)
//        val i2 = RawTag.readBits(data, 2, 3, 0x0A)
//        val i3 = RawTag.readBits(data, 0x150, 0, 8)
//        val i4 = RawTag.readBits(data, 0x150, 8, 0xe)
//        val negativei3 = RawTag.readBits(data, 0x150, 0x21, 1) != 0
//        val i5 = RawTag.readBits(data, 0x150, 0x28, 0xc) shl 2
//        val i6 = RawTag.readBits(data, 0x150, 0x34, 0xc) shl 2

        val i1 = tag.calibrationInfo.i1
        val i1bin = TestUtils.toBinString(tag.data[2], 0x7)
        val i2 = tag.calibrationInfo.i2
        val i2bin = TestUtils.toBinString(
            byteArrayOf(tag.data[3], tag.data[2]),
            byteArrayOf( 0x1F.toByte(), 0xF8.toByte())
        )
        val i3 = tag.calibrationInfo.i3
        val i3bin = TestUtils.toBinString(tag.data[0x150].toInt(), 8, 0xFF)
        val i4 = tag.calibrationInfo.i4
        val i4bin = TestUtils.toBinString(
            byteArrayOf(tag.data[0x150+2], tag.data[0x150+1]),
            byteArrayOf( 0x3F.toByte(), 0xFF.toByte())
        )
        val i3neg = if (tag.calibrationInfo.i3 >= 0) 0 else 1;
        val i3negbin = TestUtils.toBinString(tag.data[0x150+4].toInt(), 8, 0x2)

        val i5 = tag.calibrationInfo.i5 shr 2
        val i5bin = TestUtils.toBinString(
            byteArrayOf(tag.data[0x150+6], tag.data[0x150+5]),
            byteArrayOf( 0xF.toByte(), 0xFF.toByte())
        )
        val i6 = tag.calibrationInfo.i6 shr 2
        val i6bin = TestUtils.toBinString(
            byteArrayOf(tag.data[0x150+7], tag.data[0x150+6]),
            byteArrayOf( 0xFF.toByte(), 0xF0.toByte())
        )

        val int1 = TestUtils.bytesToInt(tag.data[0x153], tag.data[0x152], tag.data[0x151], tag.data[0x150])
        val int1bin = TestUtils.toBinString(int1, 32, 0x3FFFFF)

        val int2 = TestUtils.bytesToInt(tag.data[0x157], tag.data[0x156], tag.data[0x155], tag.data[0x154])
        val int2bin = TestUtils.toBinString(int2, 32, 0xFFFFFF02)

        val int0 = TestUtils.bytesToInt(0, 0, tag.data[3], tag.data[2])
        val int0bin = TestUtils.toBinString(int0, 16, 0x001FFF)


        println("bytes[  3-  2]: $int0bin")
        println("bytes[157-150]: $int2bin $int1bin")

        println("offset: 0x2   i1mask: $i1bin i1bin: ${Integer.toBinaryString(i1)} i1: $i1")
        println("offset: 0x2   i2mask: $i2bin i2bin: ${Integer.toBinaryString(i2)} i2: $i2")
        println("offset: 0x150 i3mask: $i3bin i3bin: ${Integer.toBinaryString(i3)} i3: $i3")
        println("offset: 0x151 i4mask: $i4bin i4bin: ${Integer.toBinaryString(i4)} i3: $i4")
        println("offset: 0x154 inmask: $i3negbin inbin: ${Integer.toBinaryString(i3neg)} in: $i3neg")
        println("offset: 0x155 inmask: $i5bin i5bin: ${Integer.toBinaryString(i5)} i5: $i5")
        println("offset: 0x156 inmask: $i6bin i6bin: ${Integer.toBinaryString(i6)} i6: $i6")
        println()
    }

    @Test
    fun history() {
        val index = tag.indexHistory
        val history = tag.historyValue(index)

        assertEquals(6.toByte(), index)
        assertEquals(1576, history)
    }

    @Test
    fun sensorAge() {
        val age = tag.sensorAgeInMinutes
        assertEquals(16424, age)
    }

    @Test
    fun sensorLifetime(){
        val lifetime = tag.sensorLifetime
        assertEquals(20757, lifetime)
    }

    private fun printRecord(
        record: ByteArray, index: Int, offset: Int,
        trend: Int, quality: Int, qualityFlags: Int, hasError: Int, temp: Int, tempAdj: Int
    ){
        val trendString = TestUtils.binary2bPrettyPrint(
            "trend", trend, 16, 0x3FFF,
            record[1], record[0], 0x3FFF
        )

        val qualityString = TestUtils.binary2bPrettyPrint(
            "quality", quality, 16, 0x01FF,
            record[2], record[1], 0x7FC0
        )

        val qualityFlagsString = TestUtils.binary2bPrettyPrint(
            "q_flags", qualityFlags, 16, 0x0007,
            record[3], record[2], 0x0180
        )

        val hasErrorString = TestUtils.binary2bPrettyPrint(
            "error", hasError, 16, 0x1,
            record[4], record[3], 0x0002
        )

        val temperatureString = TestUtils.binary2bPrettyPrint(
            "temp", temp, 16, 0x0FFF,
            record[4], record[3], 0x3FFC, temp shr 2
        )

        val rawTempAdj = if (tempAdj < 0) -tempAdj else tempAdj
        val tempAdjSign = if (tempAdj < 0) 1 else 0
        val tempAdjString = TestUtils.binary2bPrettyPrint(
            "tadj", tempAdj, 16, 0x01FF,
            record[5], record[4], 0x7FC0, rawTempAdj shr 2
        )

        val readingPointBin = TestUtils.toBinString(
            TestUtils.bytesToLong(0, 0,
                record[5],
                record[4],
                record[3],
                record[2],
                record[1],
                record[0],
            ),
            48,
            arrayOf(0x3FFF, 0x3FFC000000, 0x7FC000000000, 0x03800000, 0x800000000000 , 0x7FC000)
        )

        println("${if (tag.indexTrend == index) "ACTUAL " else ""}offset: $offset")
        println("raw: $readingPointBin")
        println(trendString)
        println(qualityString)
        println(qualityFlagsString)
        println(hasErrorString)
        println(temperatureString)
        println(tempAdjString)
        println()
    }
//    @Test
//    fun calibrationInfo() {
//        val calibrationInfo = tag.calibrationInfo
//        assertEquals(0, calibrationInfo.i1)
//        assertEquals(580, calibrationInfo.i2)
//        assertEquals(6, calibrationInfo.i3)
//        assertEquals(6716, calibrationInfo.i4)
//        assertEquals(10256, calibrationInfo.i5)
//        assertEquals(7068, calibrationInfo.i6)
//    }

//    @Test
//    fun sensorSerial(){
//        val tagId = "fbf1056000a007e0"
//        println(RawTag.toHexString(tag.sensorSerial))
//        println(tagId)
//    }
//
//    @Test
//    fun tostring(){
//        println(tag.sensorSerial)
//    }

}
