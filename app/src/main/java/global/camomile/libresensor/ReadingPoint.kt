package global.camomile.libresensor

data class ReadingPoint(val value: Int, val calibratedValue: Int, val temperature: Int, val tempAdjustment: Int) {
}