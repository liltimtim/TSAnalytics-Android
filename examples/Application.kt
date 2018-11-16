import android.app.Application
import TSAnalyticsManager

class SampleApp: Application() {
    override fun onCreate() {
        super.onCreate()
        TSAnalyticsManager.addPackage(TSAppsee("123SampleAPIKEY", mutableListOf(TSPIILevel.NOT_SENSITIVE), null))
        // create an item to track with a PII level
        val point = TSTrackableItem(key = "sampleItem", value = "someValue" as Any, level = TSPIILevel.NOT_SENSITIVE)
        // create a trackable item with a set of values to track as an event
        val trackable = TSTrackable(eventName = "sampleEventName", values = mutableListOf(point))
        // pass the trackable item to the package manager and let all the packages know an event needs to be recorded
        TSAnalyticsManager.track(trackable)
    }
}