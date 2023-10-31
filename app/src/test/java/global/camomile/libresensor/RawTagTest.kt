package global.camomile.libresensor

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RawTagTest() {

    private val rawTag : ByteArray = RawTagReadings.CloudReading2
    private val tag : RawTag = RawTag(rawTag, "id")

    @Test
    fun trend(){
        val index = tag.indexTrend
        val trend = tag.trendValue(index);

        assertEquals(7.toByte(), index)
        assertEquals(1805, trend)
    }

    @Test
    fun trendVStrend2(){
        println("trend index ${tag.indexTrend}")
        for (index in 0 until 16) {
            val trend = tag.trendValue(index.toByte())
            val rawTrend = tag.rawTrendValue(index.toByte())
            val calTrend = tag.calibratedTrendValue(index.toByte())
            val mmolTrend = Glucose.convertMGDLToMMOL(calTrend.toFloat())
            val glucose = Glucose(trend, 120, false)
            if (index == tag.indexTrend.toInt()) print("LAST ")
            println("trend:$trend glucose:${glucose.glucose(true)} raw trend:$rawTrend cal trend:$calTrend mmol:$mmolTrend")
        }
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
