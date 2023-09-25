//package global.camomile.libresensor.strategies
//
//inline fun <T> Iterable<T>.simpleRegression(crossinline xSelector: (T) -> Number, crossinline ySelector: (T) -> Number) = asSequence().simpleRegression(xSelector, ySelector)
//
//inline fun <T> Sequence<T>.simpleRegression(crossinline xSelector: (T) -> Number, crossinline ySelector: (T) -> Number): SimpleRegressionModel {
//        val r = mutableListOf<T>()
//        forEach { r.add(xSelector(it).toDouble(), ySelector(it).toDouble()) }
////        return ApacheSimpleRegression(r)
//    return SimpleRegressionModel()
//}
//
//
//class SimpleRegressionModel(independentVariables: List<Double>, dependentVariables: List<Double>) {
//
//}