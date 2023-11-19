package global.camomile.libresensor

enum class SensorState(val value: Byte) {
    Unknown(0),
    NotYetStarted(1),
    WarmUp(2),
    Active(3),
    Expired(5),
    Invalid(6);

    companion object {
        fun from(value: Byte): SensorState {
            return values().firstOrNull { it.value == value } ?: Unknown
        }
    }
}