package global.camomile.libresensor

import androidx.core.location.LocationRequestCompat.Quality
import java.text.DecimalFormat
import kotlin.math.roundToInt

data class Glucose(
    val glucoseLevelRaw: Int,
    val ageInSensorMinutes: Int,
    val isTrendReading: Boolean,
    val temperature: Int = 0,
    val tempAdjustment: Int = 0,
    val quality: Int = 0,
    val qualityFlags: Int = 0,
    val hasError: Boolean = false,
) : Comparable<Glucose?> {
    //https://type1tennis.blogspot.com/2017/09/libre-other-bytes-well-some-of-them-at.html
    //https://github.com/cominixo/OpenLibreNFC/blob/1d097107207edcb2330f4a1ef3e56c36879df42a/app/src/main/java/me/cominixo/openlibrenfc/MainActivity.java#L372
    val tempCelcius: Double get() = ((temperature * 0.0027689 + 9.53) * 100.0).roundToInt() / 100.0

    fun glucose(unitAsMmol: Boolean): Float {
        return if (unitAsMmol) {
            convertRawToMMOL(glucoseLevelRaw.toFloat())
        } else {
            convertRawToMGDL(glucoseLevelRaw.toFloat())
        }
    }

    fun glucoseMMOL(): Float { return glucose(true) }

    fun glucoseMGDL(): Float { return glucose(false) }

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

        fun convertRawToMGDL(raw: Float): Float {
            return raw / 10f
        }

        fun convertRawToMMOL(raw: Float): Float {
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