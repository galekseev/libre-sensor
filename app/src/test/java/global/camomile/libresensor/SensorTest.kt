package global.camomile.libresensor

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

class SensorTest() {

    private val tagId = "1d28c90900a007e0"
    private val rawTag : RawTag = RawTag(RawTagReadings.DorianScholz, tagId)


    @Test
    fun maxSensorAgeInMinutes(){
        //assertEquals(TimeUnit.DAYS.toMinutes(14), Sensor.maxSensorAgeInMinutes)
    }
}