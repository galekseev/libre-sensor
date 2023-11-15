package global.camomile.libresensor

import global.camomile.libresensor.helpers.TimeHelper
import au.com.origin.snapshots.Expect
import au.com.origin.snapshots.junit5.SnapshotExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@ExtendWith(SnapshotExtension::class)
class SensorTest() {

    private lateinit var expect: Expect

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5])
    fun `sensor should match snapshot`(tag: Int){
        val tagDate = 1677679200000L
        val sensor = Sensor(RawTag(RawTagReadings.RawTagsArray[0], "", tagDate))
        expect.serializer("json").scenario("$tag").toMatchSnapshot(sensor)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3])
    fun `sensorSerialNumber should match snapshot`(tag: Int){
        val data = RawTagReadings.RawTagsArray[tag]
        val tagId = RawTagReadings.RawTagsIds[tag]
        val tagDate = 1677679200000L
        val sensor = Sensor(RawTag(data, tagId, tagDate))
        expect.serializer("json").scenario("$tag").toMatchSnapshot(sensor.serialNumber)
    }

    @Test
    fun `readyInMinutes should be equal 30 after half an hour`(){
        val ageInMinutes = 30
        assertEquals(30, TimeHelper.minutesLeft(ageInMinutes, Sensor.sensorInitializationInMinutes))
    }

    @Test
    fun `readyInMinutes should be equal 0 after more than an hour`(){
        val ageInMinutes = 120
        assertEquals(0, TimeHelper.minutesLeft(ageInMinutes, Sensor.sensorInitializationInMinutes))
    }

    @Test
    fun `getStartTimestamp should be 2 hours less than tag date`(){
        val tagDate = 1677679200000  // 2023-03-01T14:00:00.000Z
        val sensorAgeInMinutes = 120
        val expected = tagDate - sensorAgeInMinutes * 60 * 1000 // 2023-03-01T12:00:00.000Z
        assertEquals(expected, TimeHelper.startTimestamp(tagDate, sensorAgeInMinutes))
    }

    @Test
    fun `timeLeft should be 2 days after 12 days`(){
        val startDate = 1677679200000  // 2023-03-01T14:00:00.000Z
        val daysPassed = 12
        val sensorAgeInMinutes = daysPassed * 24 * 60
        val minutesLeft = Sensor.maxSensorAgeInMinutes - sensorAgeInMinutes
        val expected = minutesLeft * 60 * 1000 // 2023-03-03T14:00:00.000Z
        assertEquals(expected.toLong(), TimeHelper.millisLeft(sensorAgeInMinutes, Sensor.maxSensorAgeInMinutes))
    }

    @Test
    fun `timeLeft should be 0 after sensor expiration`(){
        val startDate = 1677679200000  // 2023-03-01T14:00:00.000Z
        val daysPassed = 15
        val sensorAgeInMinutes = daysPassed * 24 * 60
        assertEquals(0, TimeHelper.millisLeft(sensorAgeInMinutes, Sensor.maxSensorAgeInMinutes))
    }

    @Test
    fun `timeLeftAt should be 2 days after 12 days`(){
        val startDate = 1677679200000  // 2023-03-01T14:00:00.000Z
        val daysPassed = 12
        val sensorAgeInMinutes = daysPassed * 24 * 60
        val futureDate = startDate + sensorAgeInMinutes * 60 * 1000 // 2023-03-12T14:00:00.000Z
        val minutesLeft = Sensor.maxSensorAgeInMinutes - sensorAgeInMinutes
        val expected = minutesLeft * 60 * 1000 // 2023-03-03T14:00:00.000Z
        assertEquals(expected.toLong(), TimeHelper.millisLeftAt(startDate, futureDate, Sensor.maxSensorAgeInMinutes))
    }

    @Test
    fun `timeLeftAt should be 0 after sensor expiration date`(){
        val tagDate = 1677679200000  // 2023-03-01T14:00:00.000Z
        val expiredDate = 1680357600000 // 2023-04-01T14:00:00.000Z
        assertEquals(0, TimeHelper.millisLeftAt(tagDate, expiredDate, Sensor.maxSensorAgeInMinutes))
    }
}