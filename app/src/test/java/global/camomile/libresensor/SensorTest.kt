package global.camomile.libresensor

import au.com.origin.snapshots.Expect
import au.com.origin.snapshots.junit5.SnapshotExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.concurrent.TimeUnit

@ExtendWith(SnapshotExtension::class)
class SensorTest() {

    private lateinit var expect: Expect
    
    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5])
    fun `sensor should match snapshot`(tag: Int){
        val sensor = Sensor(RawTag(RawTagReadings.RawTagsArray[0]))
        expect.serializer("json").scenario("$tag").toMatchSnapshot(sensor)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3])
    fun `sensorSerialNumber should match snapshot`(tag: Int){
        val data = RawTagReadings.RawTagsArray[tag]
        val tagId = RawTagReadings.RawTagsIds[tag]
        val sensor = Sensor(RawTag(data, tagId))
        expect.serializer("json").scenario("$tag").toMatchSnapshot(sensor.serialNumber)
    }

    @Test
    fun `readyInMinutes should be equal 30 after half an hour`(){
        val ageInMinutes = 30
        assertEquals(30, Sensor.readyInMinutes(ageInMinutes))
    }

    @Test
    fun `readyInMinutes should be equal 0 after more than an hour`(){
        val ageInMinutes = 120
        assertEquals(0, Sensor.readyInMinutes(ageInMinutes))
    }

    @Test
    fun `getStartTimestamp should be 2 hours less than tag date`(){
        val tagDate = 1677679200000  // 2023-03-01T14:00:00.000Z
        val sensorAgeInMinutes = 120
        val expected = tagDate - sensorAgeInMinutes * 60 * 1000 // 2023-03-01T12:00:00.000Z
        assertEquals(expected, Sensor.getStartTimestamp(tagDate, sensorAgeInMinutes))
    }

    @Test
    fun `timeLeft should be 2 days after 12 days`(){
        val startDate = 1677679200000  // 2023-03-01T14:00:00.000Z
        val daysPassed = 12
        val sensorAgeInMinutes = daysPassed * 24 * 60
        val minutesLeft = Sensor.maxSensorAgeInMinutes - sensorAgeInMinutes
        val expected = minutesLeft * 60 * 1000 // 2023-03-03T14:00:00.000Z
        assertEquals(expected, Sensor.timeLeft(startDate, sensorAgeInMinutes))
    }

    @Test
    fun `timeLeft should be 0 after sensor expiration`(){
        val startDate = 1677679200000  // 2023-03-01T14:00:00.000Z
        val daysPassed = 15
        val sensorAgeInMinutes = daysPassed * 24 * 60
        assertEquals(0, Sensor.timeLeft(startDate, sensorAgeInMinutes))
    }

    @Test
    fun `timeLeftAt should be 2 days after 12 days`(){
        val startDate = 1677679200000  // 2023-03-01T14:00:00.000Z
        val daysPassed = 12
        val sensorAgeInMinutes = daysPassed * 24 * 60
        val futureDate = startDate + sensorAgeInMinutes * 60 * 1000 // 2023-03-12T14:00:00.000Z
        val minutesLeft = Sensor.maxSensorAgeInMinutes - sensorAgeInMinutes
        val expected = minutesLeft * 60 * 1000 // 2023-03-03T14:00:00.000Z
        assertEquals(expected, Sensor.timeLeftAt(startDate, futureDate))
    }

    @Test
    fun `timeLeftAt should be 0 after sensor expiration date`(){
        val tagDate = 1677679200000  // 2023-03-01T14:00:00.000Z
        val expiredDate = 1680357600000 // 2023-04-01T14:00:00.000Z
        assertEquals(0, Sensor.timeLeftAt(tagDate, expiredDate))
    }
}