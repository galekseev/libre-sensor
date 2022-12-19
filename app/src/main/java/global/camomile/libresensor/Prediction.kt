package global.camomile.libresensor

import global.camomile.libresensor.interfaces.IPredictionStrategy

data class Prediction(private val trendList: List<Glucose>, private val strategy: IPredictionStrategy) {
    val glucose: Glucose
    val slope : Double // mg/dl / 10 minutes
    val confidence : Double

    init {
        val (predictedGlucose, glucoseSlope, confidenceInterval ) = strategy.makePrediction(trendList)
        this.glucose = predictedGlucose!!
        this.slope = glucoseSlope
        this.confidence = confidence(confidenceInterval)
    }

    companion object {
        private const val MAX_CONFIDENCE_INTERVAL = 2.0
        private fun confidence(confidenceInterval: Double): Double {
            return 1.0 - confidenceInterval.coerceAtMost(MAX_CONFIDENCE_INTERVAL) / MAX_CONFIDENCE_INTERVAL
        }
    }
}