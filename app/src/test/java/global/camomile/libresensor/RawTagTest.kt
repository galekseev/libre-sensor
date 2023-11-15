package global.camomile.libresensor

import global.camomile.libresensor.RawTagReadings.Companion.RawTagsArray

import au.com.origin.snapshots.Expect
import au.com.origin.snapshots.junit5.SnapshotExtension
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@ExtendWith(SnapshotExtension::class)
internal class RawTagTest() {

    private lateinit var expect: Expect

    private val rawTag : ByteArray = RawTagReadings.DorianScholz
    private val tag : RawTag = RawTag(rawTag, "id")

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5])
    fun `trend should match snapshot`(tag: Int){
        val tagA = RawTag(RawTagsArray[tag], "51974c0a00a007e0", 1657733813801)
        val trend = Array(32) { IntArray(6) }
        for (i in 0 until 16){
            trend[i][0] = tagA.tableValue(i, RawTag.offsetTrendTable)
            trend[i][1] = tagA.temperature(i, RawTag.offsetTrendTable)
            trend[i][2] = tagA.tempAdjustment(i, RawTag.offsetTrendTable)
            trend[i][3] = tagA.quality(i, RawTag.offsetTrendTable)
            trend[i][4] = tagA.qualityFlags(i, RawTag.offsetTrendTable)
            trend[i][5] = if (tagA.hasError(i, RawTag.offsetTrendTable)) 1 else 0
        }
        expect.serializer("json").scenario("$tag").toMatchSnapshot(trend)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5])
    fun `history should match snapshot`(tag: Int){
        val tagA = RawTag(RawTagsArray[tag], "51974c0a00a007e0", 1657733813801)
        val history = Array(32) { IntArray(6) }
        for (i in 0 until 32){
            history[i][0] = tagA.tableValue(i, RawTag.offsetHistoryTable)
            history[i][1] = tagA.temperature(i, RawTag.offsetHistoryTable)
            history[i][2] = tagA.tempAdjustment(i, RawTag.offsetHistoryTable)
            history[i][3] = tagA.quality(i, RawTag.offsetHistoryTable)
            history[i][4] = tagA.qualityFlags(i, RawTag.offsetHistoryTable)
            history[i][5] = if (tagA.hasError(i, RawTag.offsetHistoryTable)) 1 else 0
        }
        expect.serializer("json").scenario("$tag").toMatchSnapshot(history)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5])
    fun `indexTrend should match snapshot`(tag: Int){
        val tagA = RawTag(RawTagsArray[tag], "51974c0a00a007e0", 1657733813801)
        val indexTrend = tagA.indexTrend
        expect.scenario("$tag").toMatchSnapshot(indexTrend)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5])
    fun `indexHistory should match snapshot`(tag: Int){
        val tagA = RawTag(RawTagsArray[tag], "51974c0a00a007e0", 1657733813801)
        val indexHistory = tagA.indexHistory
        expect.scenario("$tag").toMatchSnapshot(indexHistory)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5])
    fun `sensorAgeInMinutes should match snapshot`(tag: Int){
        val tagA = RawTag(RawTagsArray[tag], "51974c0a00a007e0", 1657733813801)
        val sensorAgeInMinutes = tagA.sensorAgeInMinutes
        expect.scenario("$tag").toMatchSnapshot(sensorAgeInMinutes)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5])
    fun `sensorLifetime should match snapshot`(tag: Int){
        val tagA = RawTag(RawTagsArray[tag], "51974c0a00a007e0", 1657733813801)
        val sensorLifetime = tagA.sensorLifetime
        expect.scenario("$tag").toMatchSnapshot(sensorLifetime)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5])
    fun `sensorState should match snapshot`(tag: Int){
        val tagA = RawTag(RawTagsArray[tag], "51974c0a00a007e0", 1657733813801)
        val sensorState = tagA.sensorState
        expect.scenario("$tag").toMatchSnapshot(sensorState)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5])
    fun `sensorRegion should match snapshot`(tag: Int){
        val tagA = RawTag(RawTagsArray[tag], "51974c0a00a007e0", 1657733813801)
        val sensorRegion = tagA.sensorRegion
        expect.scenario("$tag").toMatchSnapshot(sensorRegion)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5])
    fun `calibrationInfo should match snapshot`(tag: Int){
        val tagA = RawTag(RawTagsArray[tag], "51974c0a00a007e0", 1657733813801)
        val sensorRegion = tagA.calibrationInfo
        expect.serializer("json").scenario("$tag").toMatchSnapshot(sensorRegion)
    }

    @Test
    @Disabled("For debugging purposes only")
    fun viewTrend(){
        for (index in 0 until 16) {
            val byteIndex = 28 + index * 6
            val record = tag.data.sliceArray(byteIndex until byteIndex + 7)
            printRecord( record, index, byteIndex,
                tag.tableValue(index, RawTag.offsetTrendTable),
                tag.quality(index, RawTag.offsetTrendTable),
                tag.qualityFlags(index, RawTag.offsetTrendTable),
                tag.hasError(index, RawTag.offsetTrendTable),
                tag.temperature(index, RawTag.offsetTrendTable),
                tag.tempAdjustment(index, RawTag.offsetTrendTable)
            )
        }
    }

    @Test
    @Disabled("For debugging purposes only")
    fun viewHistory(){
        for (index in 0 until 32) {
            val byteIndex = 124 + index * 6
            val record = tag.data.sliceArray(byteIndex until byteIndex + 7)
            printRecord( record, index, byteIndex,
                tag.tableValue(index, RawTag.offsetHistoryTable),
                tag.quality(index, RawTag.offsetHistoryTable),
                tag.qualityFlags(index, RawTag.offsetHistoryTable),
                tag.hasError(index, RawTag.offsetHistoryTable),
                tag.temperature(index, RawTag.offsetHistoryTable),
                tag.tempAdjustment(index, RawTag.offsetHistoryTable)
            )
        }
    }

    @Test
    @Disabled("For debugging purposes only")
    fun viewCalibration(){
        val i1 = tag.calibrationInfo.i1
        val i1bin = TestUtils.toBinString(tag.data[2], 0x7)
        val i2 = tag.calibrationInfo.i2
        val i2bin = TestUtils.toBinString(
            byteArrayOf(tag.data[3], tag.data[2]),
            byteArrayOf( 0x1F.toByte(), 0xF8.toByte())
        )
        val i3 = tag.calibrationInfo.i3
        val i3bin = TestUtils.toBinString(tag.data[0x150].toInt(), 8, 0xFF)
        val i4 = tag.calibrationInfo.i4
        val i4bin = TestUtils.toBinString(
            byteArrayOf(tag.data[0x150+2], tag.data[0x150+1]),
            byteArrayOf( 0x3F.toByte(), 0xFF.toByte())
        )
        val i3neg = if (tag.calibrationInfo.i3 >= 0) 0 else 1;
        val i3negbin = TestUtils.toBinString(tag.data[0x150+4].toInt(), 8, 0x2)

        val i5 = tag.calibrationInfo.i5 shr 2
        val i5bin = TestUtils.toBinString(
            byteArrayOf(tag.data[0x150+6], tag.data[0x150+5]),
            byteArrayOf( 0xF.toByte(), 0xFF.toByte())
        )
        val i6 = tag.calibrationInfo.i6 shr 2
        val i6bin = TestUtils.toBinString(
            byteArrayOf(tag.data[0x150+7], tag.data[0x150+6]),
            byteArrayOf( 0xFF.toByte(), 0xF0.toByte())
        )

        val int1 = TestUtils.bytesToInt(tag.data[0x153], tag.data[0x152], tag.data[0x151], tag.data[0x150])
        val int1bin = TestUtils.toBinString(int1, 32, 0x3FFFFF)

        val int2 = TestUtils.bytesToInt(tag.data[0x157], tag.data[0x156], tag.data[0x155], tag.data[0x154])
        val int2bin = TestUtils.toBinString(int2, 32, 0xFFFFFF02)

        val int0 = TestUtils.bytesToInt(0, 0, tag.data[3], tag.data[2])
        val int0bin = TestUtils.toBinString(int0, 16, 0x001FFF)


        println("bytes[  3-  2]: $int0bin")
        println("bytes[157-150]: $int2bin $int1bin")

        println("offset: 0x2   i1mask: $i1bin i1bin: ${Integer.toBinaryString(i1)} i1: $i1")
        println("offset: 0x2   i2mask: $i2bin i2bin: ${Integer.toBinaryString(i2)} i2: $i2")
        println("offset: 0x150 i3mask: $i3bin i3bin: ${Integer.toBinaryString(i3)} i3: $i3")
        println("offset: 0x151 i4mask: $i4bin i4bin: ${Integer.toBinaryString(i4)} i3: $i4")
        println("offset: 0x154 inmask: $i3negbin inbin: ${Integer.toBinaryString(i3neg)} in: $i3neg")
        println("offset: 0x155 inmask: $i5bin i5bin: ${Integer.toBinaryString(i5)} i5: $i5")
        println("offset: 0x156 inmask: $i6bin i6bin: ${Integer.toBinaryString(i6)} i6: $i6")
        println()
    }

    private fun printRecord(
        record: ByteArray, index: Int, offset: Int,
        trend: Int, quality: Int, qualityFlags: Int, hasError: Boolean, temp: Int, tempAdj: Int
    ){
        val trendString = TestUtils.binary2bPrettyPrint(
            "trend", trend, 16, 0x3FFF,
            record[1], record[0], 0x3FFF
        )

        val qualityString = TestUtils.binary2bPrettyPrint(
            "quality", quality, 16, 0x01FF,
            record[2], record[1], 0x7FC0
        )

        val qualityFlagsString = TestUtils.binary2bPrettyPrint(
            "q_flags", qualityFlags, 16, 0x0007,
            record[3], record[2], 0x0180
        )

        val hasErrorString = TestUtils.binary2bPrettyPrint(
            "error", (if (hasError) 1 else 0), 16, 0x1,
            record[4], record[3], 0x0002
        )

        val temperatureString = TestUtils.binary2bPrettyPrint(
            "temp", temp, 16, 0x0FFF,
            record[4], record[3], 0x3FFC, temp shr 2
        )

        val rawTempAdj = if (tempAdj < 0) -tempAdj else tempAdj
        val tempAdjSign = if (tempAdj < 0) 1 else 0
        val tempAdjString = TestUtils.binary2bPrettyPrint(
            "tadj", tempAdj, 16, 0x01FF,
            record[5], record[4], 0x7FC0, rawTempAdj shr 2
        )

        val readingPointBin = TestUtils.toBinString(
            TestUtils.bytesToLong(0, 0,
                record[5],
                record[4],
                record[3],
                record[2],
                record[1],
                record[0],
            ),
            48,
            arrayOf(0x3FFF, 0x3FFC000000, 0x7FC000000000, 0x03800000, 0x800000000000 , 0x7FC000)
        )

        println("${if (tag.indexTrend == index) "ACTUAL " else ""}offset: $offset")
        println("raw: $readingPointBin")
        println(trendString)
        println(qualityString)
        println(qualityFlagsString)
        println(hasErrorString)
        println(temperatureString)
        println(tempAdjString)
        println()
    }
}
