package global.camomile.libresensor

enum class SensorRegion(val value: Int) {
    Unknown(0),
    European(1),
    USA(2),
    Australian(4),
    Eastern(8);

    companion object {
        fun from(value: Int): SensorRegion {
            return values().firstOrNull { it.value == value } ?: Unknown
        }
    }
}