# Table of Contents

1. [Introduction](#introduction)
2. [Usage](#usage)
3. [Sample](#sample)

# Introduction <a name="introduction"></a>

# Installation of TSAnalytics Android

Add the following to your Project `build.gradle` file

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Then add the following to your App `build.gradle` file.

```gradle
  dependencies {
    ...
    implementation 'com.github.liltimtim:TSAnalytics-Android:0.0.1'
    ...
  }
```

# Usage <a name="usage"></a>

The library itself does not contain any specific implementation of how to deal with each analytics package. Its purpose is to unify the function signatures across all analytics packages.

In the following example we will be using `AppSee` Analytics as the package to generalize.

**Note** In order to use AppSee you will need to follow their integration guide [here](https://www.appsee.com/docs/android/native)

### Subclassing TSAnalyticsWrapper and Implementing Interfaces

Appsee supports the following capabilities

1. TSUserTrackable
2. TSRecordable
3. TSActionTrackable

These are all interfaces that unify the function signatures of Appsee hence why we are wrapping the framework.

The first step is to subclass `TSAnalyticsWrapper`. Create Kotlin file `TSAppsee.kt`. The class should look as follows

```kotlin
class TSAppsee(override var _APIKey: String, override var handlesLevels: MutableList<TSPIILevel>,
               override var byspassesLevels: MutableList<TSPIILevel>?) : TSAnalyticsWrapper, TSUserTrackable, TSRecordable, TSActionTrackable {

    init {
        Appsee.start(_APIKey)
    }

    override fun canTrack(item: TSTrackable): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canTrack(item: TSTrackDataPoint): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun generate(item: TSTrackableData): Map<String, Any> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun set(userId: TSTrackDataPoint) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCurrent(screen: TSTrackDataPoint) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun markView(asSensitive: Boolean, view: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pauseRecording() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startRecording(resuming: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun track(eventTrackable: TSTrackableData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun track(eventTrackable: TSTrackDataPoint) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun trackState(state: TSTrackableData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
```

Next we will implement tracking features for this particular library using `Appsee` functions.

In this short example, we will fully define the `canTrack(item: TSTrackable): Boolean` along with the `track(eventTrackable: TSTrackableData)` function.

```kotlin
class TSAppsee(override var _APIKey: String, override var handlesLevels: MutableList<TSPIILevel>,
               override var byspassesLevels: MutableList<TSPIILevel>?) : TSAnalyticsWrapper, TSUserTrackable, TSRecordable, TSActionTrackable {

    init {
        Appsee.start(_APIKey)
    }

... abbreviated code ...

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
... abbreviated code ...
}
```

Now your package (if it has permissions to track the PII level) should be able to track data. The only thing left to do now is to implement the `generate(item: TSTrackableData): Map<String, Any>` function

```kotlin
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
```

This now allows the generator to create a key, value pair. The reason why we must implement a generator per package is due to how certain packages handle the data. `Adobe` for example requires a different mapping structure than Appsee however since we want to unify the function signatures, we instead place the burden of implementation on the developer. This provides maximum flexibility in terms of what packages can be supported and prevents creating `one off` functions that are specific to a certain package.

# Sample <a name="sample"></a>
