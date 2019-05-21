public enum class TSPIILevel {
    /** Cases where the data is not classified as PII */
    NONE,
    /** Data is classified as not sensitive on its own such as age range, race, etc. */
    NOT_SENSITIVE,
    /** Classification of PII that can directly be tied to a user such as SSN, email, etc. */
    VERY_SENSITIVE,
    /** Any data that is not PII but can be tied to a user, such as GPS oordinates, behavior habits, etc. */
    PERSONAL_DATA,
    /** Any data that is related to network requests but does not contain the JSON body, the request url is an example */
    NETWORK,
    /** Any data that is diangostic related but does not contain PII or Personal Data */
    DIAGNOSTIC
}
/**
 * Useful for tracking events that may not have any data associated with them
 */
public class TSTrackable(override var eventName: String, override var values: MutableList<TSTrackDataPoint>?) : TSTrackableData {
    public var level: TSPIILevel = TSPIILevel.NONE

    init {
        // filter through all level values and determine the highest level of PII and set the trackable object to this
        values?.forEach {
            if (it.level >= level) {
                level = it.level
            }
        }
    }
}

public class TSTrackableItem(override var key: String,
                             override var value: Any?,
                             override var level: TSPIILevel): TSTrackDataPoint { }

/**
 * Use TSEventName to unify common events across all applications
 */
public enum class TSEventName(val eventName: String) {
    USER_SIGNED_UP("user_signed_up"),
    USER_LOGGED_IN("user_logged_in"),
    USER_COMPLETED_ONBOARDING("user_completed_onboarding")
}