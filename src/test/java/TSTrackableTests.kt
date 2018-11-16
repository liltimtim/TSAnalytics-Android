
import org.junit.Test as Test
import org.junit.Assert.*
import java.util.*
import kotlin.collections.HashMap

class Tests {
    val vSensitivePoint = TSTrackableItem(key = "ssn", value = "1234567890" as Any, level = TSPIILevel.VERY_SENSITIVE)
    val notSensitive = TSTrackableItem(key = "other", value = "someOtherValue" as Any, level = TSPIILevel.NONE)
    @Test
    fun generateTrackedValuesFromArrayOfTrackables() {

        val data = TSTrackable(eventName = "didChangeSSN", values = mutableListOf(vSensitivePoint))
        val pack = TestPackage(mutableListOf(TSPIILevel.VERY_SENSITIVE, TSPIILevel.NONE), null)
        val gen = pack.generate(data)
        assertEquals(gen.keys.size, 1)
    }

    @Test
    fun excludingPointsPackageNotAbleToTrackSensitiveData() {

        val data = TSTrackable(eventName = "didChangeSSN", values = mutableListOf(vSensitivePoint))
        val pack = TestPackage(mutableListOf(TSPIILevel.NONE), null)
        val gen = pack.generate(item = data)
        assertEquals(gen.keys.count(), 0)
    }

    @Test
    fun excludingPointsPackageNotAbleToTrackSensitiveDataRetainsOtherData() {


        val data = TSTrackable(eventName = "didChangeSSN", values = mutableListOf(vSensitivePoint, notSensitive))
        val pack = TestPackage(mutableListOf(TSPIILevel.NONE), null)
        val gen = pack.generate(item = data)
        assertEquals(gen.keys.count(), 1)
        assertFalse(gen.keys.contains("ssn"))
        assertTrue(gen.keys.contains("other"))
    }

    @Test
    fun packageCanTrack() {


        val pack = TestPackage(handlesLevels = mutableListOf(TSPIILevel.NONE), byspassesLevels = null)
        assertFalse(pack.canTrack(item = vSensitivePoint))
        assertTrue(pack.canTrack(item = notSensitive))
    }

    @Test
    fun determinePackageSensitivityLevel() {
        for (i in 0..1000 step 1) {
            val point = TSTrackable(eventName = "verySensitive", values = mutableListOf(notSensitive, notSensitive, vSensitivePoint))
            // determine if given a list, the trackable automatically upgraded its capabilities to the highest PII level.
            assertEquals(point.level, TSPIILevel.VERY_SENSITIVE)
        }
    }

    /**
     * Test whether having multiple packages with the same PII level doesn't modify the overall PII tracking
     * capabilities of the point.
     */
    @Test
    fun determineEqualLevelSensitivityPackages() {
        val notSensitivePoint = TSTrackableItem(key = "ssn", value = "0123456789" as Any, level = TSPIILevel.NONE)
        val point = TSTrackable(eventName = "verySensitive", values = mutableListOf(notSensitive, notSensitive, notSensitivePoint))
        assertEquals(point.level, TSPIILevel.NONE)
    }

    @Test
    fun bypassLevelTracking() {
        val pack = TestPackage(handlesLevels = mutableListOf(TSPIILevel.NONE), byspassesLevels = mutableListOf(TSPIILevel.NONE, TSPIILevel.VERY_SENSITIVE))
        assertTrue(pack.canTrack(item = vSensitivePoint))
    }

    @Test
    fun doesNotBypassLevelTracking() {
        val pack = TestPackage(handlesLevels = mutableListOf(TSPIILevel.NONE), byspassesLevels = mutableListOf(TSPIILevel.DIAGNOSTIC))
        assertFalse(pack.canTrack(vSensitivePoint))
    }

    @Test
    fun doesNotHandleItemButBypassesCheck() {
        val pack = TestPackage(handlesLevels = mutableListOf(), byspassesLevels = mutableListOf(TSPIILevel.NONE, TSPIILevel.VERY_SENSITIVE))
        assertTrue(pack.canTrack(vSensitivePoint))
    }
}

private class TestPackage(override var handlesLevels: MutableList<TSPIILevel>, override var byspassesLevels: MutableList<TSPIILevel>?) : TSHandlesSensitive, TSRecordable, TSUserTrackable {
    override fun canTrack(item: TSTrackable): Boolean {
        return handlesLevels.contains(item.level) || byspassesLevels?.contains(item.level) ?: false
    }

    override fun canTrack(item: TSTrackDataPoint): Boolean {
        return handlesLevels.contains(item.level) || byspassesLevels?.contains(item.level) ?: false
    }

    override fun generate(item: TSTrackableData): Map<String, Any> {
        var dictionary: MutableMap<String, Any?> = mutableMapOf<String, Any?>()
        item.values?.forEach {
            if(canTrack(it)) {
                if(it.value != null) {
                    dictionary[it.key] = it.value
                }
            }
        }
        return dictionary as Map<String, Any>
    }

    override fun startRecording(resuming: Boolean) {

    }

    override fun pauseRecording() {

    }

    override fun markView(asSensitive: Boolean, view: Any) {

    }

    override fun set(userId: TSTrackDataPoint) {

    }

    override fun setCurrent(screen: TSTrackDataPoint) {

    }

}