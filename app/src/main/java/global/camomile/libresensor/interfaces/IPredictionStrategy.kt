package global.camomile.libresensor.interfaces

import global.camomile.libresensor.Glucose

interface IPredictionStrategy {
    fun makePrediction(trendList: List<Glucose>): Triple<Glucose?, Double, Double>
}