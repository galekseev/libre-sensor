package global.camomile.libresensor

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.experimental.and

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

        assertEquals(7.toByte(), index)
        assertEquals(1805, trend)
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
    fun sensorSerial(){
        val tagId = "fbf1056000a007e0"
        println(RawTag.toHexString(tag.sensorSerial))
        println(tagId)
    }

    @Test
    fun tostring(){
        //println(tag.toString())
    }

}
