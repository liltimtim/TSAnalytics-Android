import android.app.Application
import TSAnalyticsManager

class SampleApp: Application() {
    override fun onCreate() {
        super.onCreate()
        TSAnalyticsManager.addPackage(TSAppsee("123SampleAPIKEY", mutableListOf(TSPIILevel.NOT_SENSITIVE), null))
    }
}