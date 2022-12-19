package global.camomile.libresensor

import java.text.DecimalFormat

data class Glucose(
    val glucoseLevelRaw: Int,
    val ageInSensorMinutes: Int,
    val isTrendReading: Boolean = false
) : Comparable<Glucose?> {
    fun glucose(unitAsMmol: Boolean): Float {
        return if (unitAsMmol) {
            convertRawToMMOL(glucoseLevelRaw.toFloat())
        } else {
            convertRawToMGDL(glucoseLevelRaw.toFloat())
        }
    }

    fun toString(unitAsMmol: Boolean): String {
        return toString(glucose(unitAsMmol), unitAsMmol)
    }

    override operator fun compareTo(other: Glucose?): Int {
        return glucoseLevelRaw - (other?.glucoseLevelRaw ?: 0)
    }

    companion object {
        fun convertMMOLToMGDL(mmol: Float): Float {
            return mmol * 18f
        }

        fun convertMGDLToMMOL(mgdl: Float): Float {
            return mgdl / 18f
        }

        private fun convertRawToMGDL(raw: Float): Float {
            return raw / 10f
        }

        private fun convertRawToMMOL(raw: Float): Float {
            return convertMGDLToMMOL(raw / 10f)
        }

        fun getDisplayUnitString(unitAsMmol: Boolean): String? {
            return if (unitAsMmol) "mmol/l" else "mg/dl"
        }

        fun toString(value: Float, unitAsMmol: Boolean): String {
            return if (unitAsMmol) DecimalFormat("##.0").format(value.toDouble())
                else DecimalFormat("###").format(value.toDouble())
        }
    }
}