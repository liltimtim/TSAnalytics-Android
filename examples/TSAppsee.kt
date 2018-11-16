package com.example.tdillman.testimportproject // replace this with your own package
import com.appsee.Appsee
import TSAnalyticsWrapper
import TSUserTrackable
import TSRecordable
import TSActionTrackable
import TSPIILevel
import TSTrackable
import TSTrackDataPoint
import TSTrackableData
import android.view.View

class TSAppsee(override var _APIKey: String, override var handlesLevels: MutableList<TSPIILevel>,
               override var byspassesLevels: MutableList<TSPIILevel>?) : TSAnalyticsWrapper, TSUserTrackable, TSRecordable, TSActionTrackable {

    init {
        Appsee.start(_APIKey)
    }

    override fun canTrack(item: TSTrackable): Boolean {
        return handlesLevels.contains(item.level) || byspassesLevels?.contains(item.level) ?: false
    }

    override fun canTrack(item: TSTrackDataPoint): Boolean {
        return handlesLevels.contains(item.level) || byspassesLevels?.contains(item.level) ?: false
    }

    override fun generate(item: TSTrackableData): Map<String, Any> {
        val gen: MutableMap<String, Any> = mutableMapOf<String, Any>()
        item.values?.forEach {
            if (canTrack(it)) {
                if(it.value != null) {
                    gen[it.key] = it.value!!
                }
            }
        }
        return gen
    }

    override fun set(userId: TSTrackDataPoint) {
        Appsee.setUserId(userId.key)
    }

    override fun setCurrent(screen: TSTrackDataPoint) {
        Appsee.startScreen(screen.key)
    }

    override fun markView(asSensitive: Boolean, view: Any) {
        val v = view as? View
        if (v != null) {
            Appsee.markViewAsSensitive(v)
        }
    }

    override fun pauseRecording() {
        Appsee.stop()
    }

    override fun startRecording(resuming: Boolean) {
        if (resuming) {
            Appsee.resume()
        } else {
            Appsee.start()
        }
    }

    override fun track(eventTrackable: TSTrackableData) {
        if (canTrack(eventTrackable as TSTrackable)) {
            Appsee.addEvent(eventTrackable.eventName, generate(eventTrackable))
        }
    }

    override fun track(eventTrackable: TSTrackDataPoint) {
        if (canTrack(eventTrackable)) {
            Appsee.addEvent(eventTrackable.key)
        }
    }

    override fun trackState(state: TSTrackableData) {
        // Current State is not supported for Appsee
        // This was the only specialty function needed for Adobe since Adobe is the only analytics package that
        // has this feature.
    }

}