package global.camomile.libresensor

import global.camomile.libresensor.interfaces.IPredictionStrategy
import global.camomile.libresensor.strategies.LastValue
//import global.camomile.libresensor.strategies.SimpleRegression

data class Reading (
    val tag: RawTag,
    val readingDate: Long = tag.tagDate,
    val timezoneOffsetInMinutes: Int = 0,
    val predictionStrategy : IPredictionStrategy = LastValue()//SimpleRegression()
)
{
    val sensor = Sensor(tag)
    val trend: ArrayList<Glucose> = ArrayList()
    val history: ArrayList<Glucose> = ArrayList()
    val glucose : Glucose?
    val prediction : Prediction?

    constructor(
        data: ByteArray,
        tagId: String = "",
        tagDate: Long = System.currentTimeMillis(),
        readingDate: Long = tagDate,
        timezoneOffsetInMinutes: Int = 0,
        predictionStrategy : IPredictionStrategy = LastValue()//SimpleRegression()
    ) : this(RawTag(data, tagId, tagDate), readingDate, timezoneOffsetInMinutes, predictionStrategy)

    init {
        // read trend values from ring buffer, starting at indexTrend
        val indexTrend = tag.indexTrend

        for (counter in 0 until NUM_TREND_VALUES) {
            val index = (indexTrend + counter) % NUM_TREND_VALUES
            val glucoseLevelRaw = tag.trendValue(index)
            // skip zero values if the sensor has not filled the ring buffer yet completely
            if (glucoseLevelRaw > 0) {
                val ageInSensorMinutes = sensor.ageInMinutes - NUM_TREND_VALUES + counter
                trend +=
                    Glucose(
                        glucoseLevelRaw,
                        ageInSensorMinutes,
                        true,
                    )
            }
        }

        if(trend.isNotEmpty()) {
            prediction = Prediction(trend, predictionStrategy)
            glucose = trend.last()
        }
        else {
            prediction = null
            glucose = null
        }

        val mostRecentHistoryAgeInMinutes = 3 + (sensor.ageInMinutes - 3) % HISTORY_INTERVAL_IN_MINUTES
        val indexHistory: Int = tag.indexHistory.toInt()
        val glucoseLevels = ArrayList<Int>()
        val ageInSensorMinutesList = ArrayList<Int>()

        // read history values from ring buffer, starting at indexHistory (bytes 124-315)
        for (counter in 0 until NUM_HISTORY_VALUES) {
            val index = (indexHistory + counter) % NUM_HISTORY_VALUES
            val glucoseLevelRaw: Int = tag.historyValue(index)
            // skip zero values if the sensor has not filled the ring buffer yet completely
            if (glucoseLevelRaw > 0) {
                val dataAgeInMinutes =
                    mostRecentHistoryAgeInMinutes + (NUM_HISTORY_VALUES - (counter + 1)) * HISTORY_INTERVAL_IN_MINUTES
                val ageInSensorMinutes = sensor.ageInMinutes - dataAgeInMinutes

                // skip the first hour of sensor data as it is faulty
                if (ageInSensorMinutes > Sensor.sensorInitializationInMinutes) {
                    glucoseLevels.add(glucoseLevelRaw)
                    ageInSensorMinutesList.add(ageInSensorMinutes)
                }
            }
        }

        // check if there were actually any valid data points
        if (ageInSensorMinutesList.isNotEmpty()) {
            // create history data point list
            for (i in glucoseLevels.indices) {
                val glucoseLevelRaw = glucoseLevels[i]
                val ageInSensorMinutes = ageInSensorMinutesList[i]
                val glucoseData = Glucose(
                        glucoseLevelRaw,
                        ageInSensorMinutes
                    )
                history.add(glucoseData)
            }
        }
    }

    companion object {
        const val NUM_HISTORY_VALUES = 32
        const val HISTORY_INTERVAL_IN_MINUTES = 15
        const val NUM_TREND_VALUES: Byte = 16
    }
}