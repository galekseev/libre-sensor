package global.camomile.libresensor.helpers

import global.camomile.libresensor.Sensor
import java.util.concurrent.TimeUnit

class TimeHelper {
    companion object{
        fun minutesLeft(ageInMinutes: Int, maxAgeInMinutes: Int): Int {
            return (maxAgeInMinutes - ageInMinutes).coerceAtLeast(0)
        }

        fun millisLeft(ageInMinutes: Int, maxAgeInMinutes: Int): Long {
            return TimeUnit.MINUTES.toMillis((maxAgeInMinutes - ageInMinutes).toLong())
                .coerceAtLeast(0)
        }

        fun millisLeftAt(startDate: Long, atTime: Long, maxAgeInMinutes: Int): Long {
            return (startDate + TimeUnit.MINUTES.toMillis(maxAgeInMinutes.toLong()) - atTime)
                .coerceAtLeast(0)
        }

        fun startTimestamp(initialDate: Long, ageInMinutes: Int): Long {
            return initialDate - initialDate % TimeUnit.MINUTES.toMillis(1) -
                    TimeUnit.MINUTES.toMillis(ageInMinutes.toLong())
        }
    }
}