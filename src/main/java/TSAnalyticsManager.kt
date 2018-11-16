object TSAnalyticsManager: TSRecordable, TSUserTrackable, TSActionTrackable {
    private var packages: MutableList<TSAnalyticsWrapper> = mutableListOf()

    fun addPackage(item: TSAnalyticsWrapper) { packages.add(item) }

    override fun track(eventTrackable: TSTrackableData) {
        packages.forEach { (it as? TSActionTrackable)?.track(eventTrackable) }
    }

    override fun track(eventTrackable: TSTrackDataPoint) {
        packages.forEach { (it as? TSActionTrackable)?.track(eventTrackable) }
    }

    override fun trackState(state: TSTrackableData) {
        packages.forEach { (it as? TSActionTrackable)?.trackState(state) }
    }

    override fun set(userId: TSTrackDataPoint) {
        packages.forEach { (it as? TSUserTrackable)?.set(userId) }
    }

    override fun setCurrent(screen: TSTrackDataPoint) {
        packages.forEach { (it as? TSRecordable)?.setCurrent(screen) }
    }

    override fun startRecording(resuming: Boolean) {
        packages.forEach { (it as? TSRecordable)?.startRecording(resuming) }
    }

    override fun pauseRecording() {
        packages.forEach { (it as? TSRecordable)?.pauseRecording() }
    }

    override fun markView(asSensitive: Boolean, view: Any) {
        packages.forEach { (it as? TSRecordable)?.markView(asSensitive, view) }
    }

}