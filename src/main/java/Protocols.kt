import java.util.*

public interface TSHandlesSensitive {
    /** Denotes which levels of PII the package is able to handle safely */
    var handlesLevels: MutableList<TSPIILevel>
    /** Denotes which levels of PII checks are bypassed by the framework - THIS IS DANGEROUS if improperly set */
    var byspassesLevels: MutableList<TSPIILevel>?
    /** Determines if the item to be saved is trackable by the package */
    fun canTrack(item: TSTrackDataPoint): Boolean
    fun canTrack(item: TSTrackable): Boolean
    /**
     * Generates a key value dictionary that the package can use to save / track the data.
     * Determines if the package can handle a data point with the set PII level
     */
    fun generate(item: TSTrackableData): Map<String, Any>
}

public interface TSTrackableData {
    /** Denotes the event name */
    var eventName: String
    /** Denotes additional properties to be added with this event */
    var values: MutableList<TSTrackDataPoint>?
}

public interface TSTrackDataPoint {
    /** Name of the event or data point */
    var key: String
    /** Additional information about the event or data point */
    var value: Any?
    /** Denotes what level of PII this data point is */
    var level: TSPIILevel
}

public interface TSRecordable {
    /**
     * Starts or resumes recording. Specifying 'resume' true should hint that the caller wishes to resume
     * recording and not start
     */
    fun startRecording(resuming: Boolean)

    /**
     * Pauses recording session
     */
    fun pauseRecording()

    /**
     * Marks a view as being sensitive. For most analytics frameworks that support it, will 'black out' a view
     */
    fun markView(asSensitive: Boolean, view: Any)

    /**
     * Sets the currently viewed screen.  Useful when the underlying view is not automatically detected by the
     * analytics framework or if the framework does not support automatic view tagging.
     */
    fun setCurrent(screen: TSTrackDataPoint)
}

public interface TSActionTrackable {
    /**
     * Tracks an event with a custom name
     */
    fun track(eventTrackable: TSTrackDataPoint)
    /**
     * Tracks an event with additional parameters
     */
    fun track(eventTrackable: TSTrackableData)
    /**
     * Specialty tracking for Adobe Mobile.  Tracks a state with context data.
     */
    fun trackState(state: TSTrackableData)

}

public interface TSUserTrackable {
    fun set(userId: TSTrackDataPoint)
}

public interface TSAnalyticsWrapper: TSHandlesSensitive {
    /** Internal APIKey help in memory for later use */
    var _APIKey: String
}

