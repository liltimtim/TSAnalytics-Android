# Table of Contents

1. [Introduction](#introduction)
2. [Usage](#usage)
   1. [Installation](#installation)
   2. [Tutorial](#tutorial)
   3. [Registering Package with Manager](#register_with_package_manager)
3. [Sample](#sample)
   1. [TSAppsee](#tsappsee_example)
   2. [TSPackageManager](#tspackage_manager_example)

# Introduction <a name="introduction"></a>

### Purpose

The purpose of this library is to provide a unified way to deal with analytics packages by providing standardized function signatures instead of having to deal with each individual package.

For example: If I wanted to integrate Appcenter, Fabric, Adobe, and Appsee analytics all in the project, without a unified way to access and send off events to each package I would have to do the following...

**this is an example that will not compile**

```kotlin
// will not compile
Appsee.trackEvent(name: "someEvent", properties: myProps)
Adobe.trackAction(name: "someEvent")
Fabric.Answers.TrackActionEvent(name: "someEvent")
```

Scary stuff, look at that code replication...

![alt gross](https://i.imgflip.com/11xlu3.jpg)

### Reasoning

As you can see, there would be tons of code replication. An additional issue is if we want to remove a package that we no longer wish to use for analytics the nightmare and refactoring continues. Also, none of these packages are aware of the PII that may be sending nor is the developer aware.

What if we could have one class that could manage and send all our events to all our packages and also be aware of PII?

This is the aim of TSAnalytics: to allow easy integration of an analytics package without worrying about PII requirements, implementation, and management of each package. This is accomplished by having an overarching class that manages each package, the developer simply dispatches events and actions to the manager and the manager handles the rest. Each package is also assigned what PII it is allowed to track and any event that it is given that it cannot support, it will simply ignore the event however packages that can track the event will not ignore it.

# Installation of TSAnalytics Android <a name="installation"></a>

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
    implementation 'com.github.liltimtim:TSAnalytics-Android:0.0.3'
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
package com.example.tdillman.testimportproject
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

![yasss](https://i.imgflip.com/1u9mvz.jpg)

## Register TSAnalyticsWrapper subclass with TSPackageManager <a name="register_with_package_manager"></a>

Its recommended by multiple analytics platforms that you instantiate the library outside of the normal activity lifecycle.

You should use a subclasses version of `Application` to achieve this.

Below is an example

# Sample <a name="sample"></a>

The following is a full implementation including the use of `TSAnalyticsManager` and a sample analytics package (Appsee)

1. [Application.kt subclassing sample](https://github.com/liltimtim/TSAnalytics-Android/blob/master/examples/Application.kt)
2. [Sample TSAppsee](https://github.com/liltimtim/TSAnalytics-Android/blob/master/examples/TSAppsee.kt)
