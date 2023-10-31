package global.camomile.libresensor

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

class ReadingTest() {

    private val rawTag : RawTag = RawTag(RawTagReadings.DorianScholz)


    @Test
    fun trends(){
        val rd = Reading(RawTag(RawTagReadings.DorianScholz))

        println("Trends: ${rd.trend.size} records")
        rd.trend.forEach{ println("%.1f %s".format(it.glucose(true), it.ageInSensorMinutes)) }
        println()

        println("Prediction %s, slope %s, age %s".format(
            rd.prediction?.glucose?.glucose(true),
            rd.prediction?.slope,
            rd.prediction?.glucose?.ageInSensorMinutes
        ))
        println()

        println("Glucose %s, age %s".format(
            rd.glucose?.toString(true),
            rd.glucose?.ageInSensorMinutes
        ))
        println()


        println("History: ${rd.history.size} records")
        rd.history.forEach{ println("%.1f %s".format(it.glucose(true), it.ageInSensorMinutes)) }
    }
}