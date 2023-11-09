package global.camomile.libresensor

import global.camomile.libresensor.interfaces.IPredictionStrategy
import global.camomile.libresensor.strategies.LastValue
//import global.camomile.libresensor.strategies.SimpleRegression

data class Reading (
    val tag: RawTag,
    val readingDate: Long = tag.tagDate,
    val timezoneOffsetInMinutes: Int = 0,
)
{
    val sensor = Sensor(tag)
    val trend: ArrayList<Glucose> = ArrayList()
    val history: ArrayList<Glucose> = ArrayList()
    val glucose : Glucose?

    constructor(
        data: ByteArray,
        tagId: String = "",
        tagDate: Long = System.currentTimeMillis(),
        readingDate: Long = tagDate,
        timezoneOffsetInMinutes: Int = 0
    ) : this(RawTag(data, tagId, tagDate), readingDate, timezoneOffsetInMinutes)

    init {
        // read trend values from ring buffer, starting at indexTrend
        val indexTrend = tag.indexTrend

        for (counter in 0 until NUM_TREND_VALUES) {
            val index = (indexTrend + counter) % NUM_TREND_VALUES
            val glucoseLevelRaw = tag.trendValue(index)
            // skip zero values if the sensor has not filled the ring buffer yet completely
            if (glucoseLevelRaw > 0) {
                val ageInSensorMinutes = tag.sensorAgeInMinutes - NUM_TREND_VALUES + counter
                trend +=
                    Glucose(
                        glucoseLevelRaw,
                        ageInSensorMinutes,
                        true,
                        tag.trendTemperature(index),
                        tag.trendTempAdjustment(index),
                        tag.trendQuality(index),
                        tag.trendQualityFlags(index),
                        tag.trendHasError(index)
                    )
            }
        }

        if(trend.isNotEmpty()) {
            glucose = trend.last()
        }
        else {
            glucose = null
        }

        val mostRecentHistoryAgeInMinutes = 3 + (tag.sensorAgeInMinutes - 3) % HISTORY_INTERVAL_IN_MINUTES
        val indexHistory: Int = tag.indexHistory

        // read history values from ring buffer, starting at indexHistory (bytes 124-315)
        for (counter in 0 until NUM_HISTORY_VALUES) {
            val index = (indexHistory + counter) % NUM_HISTORY_VALUES
            val glucoseLevelRaw: Int = tag.historyValue(index)
            // skip zero values if the sensor has not filled the ring buffer yet completely
            if (glucoseLevelRaw > 0) {
                val dataAgeInMinutes =
                    mostRecentHistoryAgeInMinutes + (NUM_HISTORY_VALUES - (counter + 1)) * HISTORY_INTERVAL_IN_MINUTES
                val ageInSensorMinutes = tag.sensorAgeInMinutes - dataAgeInMinutes

                // skip the first hour of sensor data as it is faulty
                if (ageInSensorMinutes > Sensor.sensorInitializationInMinutes) {
                    val glucoseReading = Glucose(
                        glucoseLevelRaw,
                        ageInSensorMinutes,
                        false,
                        tag.historyTemperature(index),
                        tag.historyTempAdjustment(index),
                        tag.historyQuality(index),
                        tag.historyQualityFlags(index),
                        tag.historyHasError(index)
                    )
                    history.add(glucoseReading)
                }
            }
        }
    }

    companion object {
        const val NUM_HISTORY_VALUES = 32
        const val HISTORY_INTERVAL_IN_MINUTES = 15
        const val NUM_TREND_VALUES: Byte = 16
    }
}