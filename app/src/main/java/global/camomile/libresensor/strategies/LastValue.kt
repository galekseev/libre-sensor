package global.camomile.libresensor.strategies

import global.camomile.libresensor.Glucose
import global.camomile.libresensor.interfaces.IPredictionStrategy

class LastValue : IPredictionStrategy {

    override fun makePrediction(trendList: List<Glucose>) : Triple<Glucose?, Double, Double> {
        if (trendList.isEmpty()) {
            return Triple(null, -1.0, -1.0)
        }

        val ageInSensorMinutes: Int =
            trendList[trendList.size - 1].ageInSensorMinutes + Companion.PREDICTION_TIME
        val glucoseLevelRaw = trendList[trendList.size - 1].glucoseLevelRaw

        return Triple(
            Glucose(
                glucoseLevelRaw,
                ageInSensorMinutes,
                true
            ),
            0.0,
            0.0
        )
    }

    companion object{
        private const val PREDICTION_TIME = 15 // in minutes
        private const val MAX_CONFIDENCE_INTERVAL = 2.0
    }
}