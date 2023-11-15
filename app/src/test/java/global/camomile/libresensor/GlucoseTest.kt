package global.camomile.libresensor

import au.com.origin.snapshots.junit5.SnapshotExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SnapshotExtension::class)
class GlucoseTest {

    private val glucose = Glucose(
        glucoseLevelRaw = 1800,
        ageInSensorMinutes = 600,
        isTrendReading = true,
        temperature = 6671,
        tempAdjustment = -4,
        quality = 288,
        qualityFlags = 1,
        hasError = false
    )

    @Test
    fun convertMMOLToMGDL(){
        assertEquals(180.0F, Glucose.convertMMOLToMGDL(10.0F))
    }

    @Test
    fun convertMGDLToMMOL(){
        assertEquals(10.0F, Glucose.convertMGDLToMMOL(180.0F))
    }

    @Test
    fun convertRawToMMOL(){
        assertEquals(10.0F, Glucose.convertRawToMMOL(1800.0F))
    }

    @Test
    fun convertRawToMGDL(){
        assertEquals(180.0F, Glucose.convertRawToMGDL(1800.0F))
    }

    @Test
    fun glucoseMMOL(){
        assertEquals(10.0F, glucose.glucoseMMOL())
    }

    @Test
    fun glucoseMGDL(){
        assertEquals(180.0F, glucose.glucoseMGDL())
    }

    @Test
    fun glucose(){
        assertEquals(10.0F, glucose.glucose(true))
        assertEquals(180.0F, glucose.glucose(false))
    }

    @Test
    fun tempCelcius(){
        println(glucose.tempCelcius)
        assertEquals(28.0, glucose.tempCelcius)
    }
}