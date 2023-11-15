//package global.camomile.libresensor
//
//import au.com.origin.snapshots.Expect
//import au.com.origin.snapshots.junit5.SnapshotExtension
//import org.junit.jupiter.api.extension.ExtendWith
//import org.junit.jupiter.params.ParameterizedTest
//import org.junit.jupiter.params.provider.ValueSource
//
//@ExtendWith(SnapshotExtension::class)
//class ReadingTest() {
//
//    private lateinit var expect: Expect
//
//    @ParameterizedTest
//    @ValueSource(ints = [0, 1, 2, 3, 4, 5])
//    fun `trends should match snapshot`(tag: Int){
//        val tagA = RawTag(RawTagReadings.RawTagsArray[tag], "51974c0a00a007e0", 1657733813801)
//        val reading = Reading(tagA)
//        expect.serializer("json").scenario("$tag").toMatchSnapshot(reading)
//    }
//}