package global.camomile.libresensor.strategies

//import org.apache.commons.math3.stat.regression.SimpleRegression
import global.camomile.libresensor.Glucose
import global.camomile.libresensor.interfaces.IPredictionStrategy
import org.nield.kotlinstatistics.simpleRegression
import kotlin.math.roundToInt

class SimpleRegression : IPredictionStrategy {

    override fun makePrediction(trendList: List<Glucose>) : Triple<Glucose?, Double, Double> {
        if (trendList.isEmpty()) {
            return Triple(null, -1.0, -1.0)
        }

        val regression = trendList.simpleRegression(
            xSelector = { it.ageInSensorMinutes },
            ySelector = { it.glucoseLevelRaw}
        )

        val ageInSensorMinutes: Int =
            trendList[trendList.size - 1].ageInSensorMinutes + Companion.PREDICTION_TIME
        val glucoseLevelRaw = regression.predict(ageInSensorMinutes.toDouble())

        return Triple(
            Glucose(
                glucoseLevelRaw.roundToInt(),
                ageInSensorMinutes,
                true
            ),
            regression.slope,
            regression.slopeConfidenceInterval
        )
    }

    companion object{
        private const val PREDICTION_TIME = 15 // in minutes
        private const val MAX_CONFIDENCE_INTERVAL = 2.0
    }
}