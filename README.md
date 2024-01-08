# Proximity SDK for Android

## Table of Contents

* [Requirements](#requirements)
* [Installation](#installation)
    + [Add the Maven repository](#add-the-maven-repository)
    + [Add the library](#add-the-library)
    + [Other dependencies](#other-dependencies)
    + [Regarding the JCenter repository](#regarding-the-jcenter-repository)
    + [Supporting Huawei Mobile Services](#supporting-huawei-mobile-services)
    + [For distribution on Huawei AppGallery only](#for-distribution-on-huawei-appgallery-only)
* [Usage](#usage)
    + [Initialization](#initialization)
    + [Automatic Initialization](#automatic-initialization)
    + [Manual Initialization](#manual-initialization)
        * [Create an Application Class](#create-an-application-class)
        * [Add the Required Code](#add-the-required-code)
    + [Permissions and hardware requirements](#permissions-and-hardware-requirements)
        * [For application running on Android 6.0 or later](#for-application-running-on-android-60-or-later)
        * [For applications running Android 10.0 or later](#for-applications-running-android-100-or-later)
        * [For applications running Android 11.0 or later](#for-applications-running-android-110-or-later)
        * [For applications running on Android 13.0 or later](#for-applications-running-on-android-130-or-later)
    + [Tracking user identifier](#tracking-user-identifier)
    + [Advertising ID and Installation ID](#advertising-id-and-installation-id)
    + [External User ID](#external-user-id)
    + [Data Tags](#data-tags)
    + [Customizing the notifications](#customizing-the-notifications)
    + [Receive custom events](#receive-custom-events)
    + [Programmatically Disable Advertising](#programmatically-disable-advertising)
    + [GDPR Consent](#gdpr-consent)
    + [Enabling the Consent Flow support](#enabling-the-consent-flow-support)
    + [Using Consent Management Platform](#using-consent-management-platform)
    + [Implementing a Custom Consent Flow](#implementing-a-custom-consent-flow)
    + [Background Jobs ID](#background-jobs-id)

This library allows you to integrate Jointag Proximity into your Android app.

## Requirements

Minimum API level: `15` (Android 4.0.3)

> **Note**: to use functionalities that rely on BLE, the minimum API level is
> `18` (Android 4.3). If the device API level is between `16` and `17` the SDK
> won't be able to access BLE and therefore it will be not possible to obtain
> data from BLE devices.

## Installation

### Add the Maven repository

To download the SDK package you can use our Maven repository. To include it, add
the following lines to your build.gradle (Module: app) file:

```groovy
repositories {
    jcenter()
    maven { url "https://artifactory.jointag.com/artifactory/jointag" }
}
```

### Enable Java 8 language features and APIs

```groovy
android {
    ...
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    // For Kotlin projects
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
```

### Add the library

Now add the ProximitySDK dependency (use latest SDK version).

```groovy
dependencies {
    // ProximitySDK SDK
    implementation("com.jointag:proximitysdk:1.19.+")
}
```

### Other dependencies (Optional)

Additional dependencies **should automatically be downloaded** and included
along with the library through the previous gradle declaration.

_If you don't use Gradle to handle project building_, or want to manually
include the required libraries, add the following to your dependencies block in
the app/build.gradle file.

```gradle
dependencies {
    <...>
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.preference:preference:1.2.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.21")
    implementation("com.google.android.gms:play-services-ads-identifier:18.0.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("org.altbeacon:android-beacon-library:2.19.5")
    <...>
}
```

### Supporting Huawei Mobile Services

You can enable ProximitySDK support for **Huawei Mobile Services** by following
the official Huawei documentation for integrating [Huawei HMS Core][hms-core] into your
application, then adding the dependencies for [Huawei Ads Kit][huawei-ads-kit]
and [Huawei Location Kit][huawei-location-kit] to your app/build.gradle
file:

```gradle
dependencies {
    <...>
    implementation("com.huawei.hms:ads-identifier:3.4.41.302")
    implementation("com.huawei.hms:location:4.0.0.300")
    <...>
}
```

**Note**: Huawei libraries may have a **higher minimum API level** requirement
than the ProximitySDK library.

#### For distribution on Huawei AppGallery only

If your app will only be available on Huawei AppGallery and you want to avoid
including any Google Play Services dependencies that ProximitySDK includes, you
can specify the group "com.google.android.gms" from the librry transitive
dependencies in your app/build.gradle file:

```gradle
dependencies {
    // ProximitySDK SDK
    implementation("com.jointag:proximitysdk:1.16.+") {
        exclude group: "com.google.android.gms"
    }
}
```

## Usage

### Initialization

#### Automatic Initialization

You can opt to automatically initialize the SDK at application launch by
adding the following entries to the application's AndroidManifest.xml file,
inside the `<application>` tag:

```xml
        <meta-data
            android:name="com.jointag.proximity.API_KEY"
            android:value="YOUR_API_KEY" />
        <meta-data
            android:name="com.jointag.proximity.API_SECRET"
            android:value="YOUR_API_SECRET" />
```

> :warning: **Note** Some features, such as the ability to subscribe to custom
> events, requires their relative initialization code to be execute in the
> `Application` onCreate method, so they are not available when using Automatic
> Initialization

You can also set the SDK **log level** and **log tag** using the following keys:

```xml
        <meta-data
            android:name="com.jointag.proximity.LOG_LEVEL"
            android:value="verbose" />
            <!-- values : verbose|debug|info|warn|error|assert -->
        <meta-data
            android:name="com.jointag.proximity.LOG_TAG"
            android:value="MY_TAG" />

```

You plans to implement a user consent flow manually or using a IAB-compliat CMP,
you must specify the following entry:

```xml
        <meta-data
            android:name="com.jointag.proximity.CMP_ENABLED"
            android:value="true" />
```

#### Manual Initialization

If you want to manually initialize the SDK, you have to include the
initialization code in your app **Application class** onCreate method.

If you don't have an `Application` class read the following section on how to
**Create an Application Class**, otherwise skip to the **Add the Required Code**
*section.

##### Create an Application Class

1. Create a class extending `android.app.Application`

```java
import android.app.Application;

public class MyApplication extends Application {
   @Override
   public void onCreate() {
      super.onCreate();
   }
}
```

2. Update your `AndroidManifest.xml`

Add `android:name=".MyApplication"` to your `<application>` tag

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.app">
   <application
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:name=".MyApplication">
        ...
    </application>
</manifest>
```

##### Add the Required Code

Add the following call to `ProximitySDK.init()` in the `onCreate()` method of
your `Application` class.


```java
import com.jointag.proximity.ProximitySDK;
import com.jointag.proximity.util.Logger;

public class MyApplication extends Application {
    public static final String API_KEY = "YOUR_API_KEY";
    public static final String SECRET = "YOUR_API_SECRET";

    @Override
    public void onCreate() {
        super.onCreate();
        // Set a custom logger tag  
        Logger.setTag("MyApplication");
        // Enable verbose logging
        Logger.setLogLevel(Logger.VERBOSE);
        // Initialize with your ApiKey / ApiSecret
        ProximitySDK.init(this, API_KEY, SECRET);
    }
}
```

---

> :no_entry: **Attention**: the ProximitySDK.init() method **must be** called
> from within the `Application.onCreate` method. Calling the init method from
> any other place may result in an unpredictable SDK behaviour, or a crash in
> the worst case.

### Permissions and hardware requirements

This SDK uses user-location and notifications permissions to function.
All required permissions are declared in the SDK AndroidManifest file, and
automatically added to your application AndroidManifest when the library is
included as a Gradle dependency (see [Add the library](#add-the-library)).

##### For application running on Android 6.0 or later

The request for [`ACCESS_FINE_LOCATION`][access-fine-location] or
[`ACCESS_COARSE_LOCATION`][access-coarse-location] permission has to be
implemented by the application that includes the SDK. The request can be
implemented in any point of the application, but it's recommended to ask the
user for location permission as soon as possible, because until the permission
is not granted the SDK can't enable proximity features involving GPS and
beacons.

##### For applications running Android 10.0 or later

The additional permission
[`ACCESS_BACKGROUND_LOCATION`][access-background-location] should be declared in
the application `AndroidManifest.xml` file and requested to the user for the SDK
to work properly while the application is in background.

Add the following to your AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
```

##### For applications running Android 11.0 or later

As per [official documentation](https://developer.android.com/preview/privacy),
the location and background permission should be requested incrementally in
separate calls.

To implement the permission request dialog in your application follow the
official [Requesting Permissions at Run Time][requesting-permissions]
documentation.

An example of implementation is the following:

```java
if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
    if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
        Toast.makeText(context, "Message explaining why granting the user location permission is usefull to the user", Toast.LENGTH_SHORT).show();
    } else {
        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }
}
```

After having requested the permissions to the user and the user has granted the
required permissions (tipically in the `onRequestPermissionsResult` callback of
the Activity), the monitoring process must be resumed by calling the
`ProximitySDK#checkPendingPermissions` method.

Eg.

```java
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        ProximitySDK.getInstance().checkPendingPermissions();
    }
}
```

##### For applications running on Android 13.0 or later

The request for [`POST_NOTIFICATIONS`][post-notifications] permission has to be
implemented by the application that includes the SDK. The request can be
implemented in any point in the application, but it's recommended to ask the
user for notification permission as soon as possible..

### Tracking user identifier

#### Advertising ID and Installation ID

The SDK associates each tracked request with the *advertisingId*. If the
*advertisingId* is not available due to a user permission denial, the device can
be identified by the *installationId*. The *installationId* is a randomly
generated UUID created during the first initialization that hence identifies a
specific installation of the SDK for that application. If the app containing the
SDK is uninstalled and then installed again the *installationId* will be a
different one. You can retrieve the *installationId* after the initialization of
the SDK anywhere in your code with the following line:

```java
ProximitySDK.getInstance().getInstallationId();
```

#### External User ID

The `externalUserId` is an identifier you set to pair a unique user identifier
of your choice with our` installationId`. Tipically this identifier must be set
after a user has signed in to your application, and must be removed after the
same user decides to sign out of you application.

You can choose any string of 255 characters or less as externalUserId.

Your **externalUserId** can be paired with multiple **installationId**, for example if
the same user uses your app on multiple devices, or if the same user uninstalled
and installed your app multiple times.

On the other hand, the same **installationId** can be associated with one and
only one **externalUserId**, usually the last one sent.

For example, you can use the user record id of your database or your CRM, or the
hash of an email address, or a third party platform identifier.

Use the `setExternalUserId` method to add your unique external user ids:

```java
// Set
ProximitySDK.getInstance().setExternalUserId("SOME ID");
// Unset
ProximitySDK.getInstance().setExternalUserId(null);
```

### Data Tags

Tags are custom key-value pairs of `string`, `number`, `boolean` or `null` type,
that can be sent to our server through the SDK methods and that allow you a more
effective campaigns targeting, or to receive personalized analysis based on the
characteristics of your users.

Tags can be set or unset (with a `null` value) using the following methods:

#### sendTag

The `sendTag` method allow to set or unset a single tag at a time.

The method can be called multiple times. When sending different keys, its
effects are cumulative. If the same key is used, the last value overwrites the
previous ones.

```java
ProximitySDK.getInstance().sendTag("key1", "value");
// -> { "key1" : "value" }
ProximitySDK.getInstance().sendTag("key2", 1);
// -> { "key1" : "value", "key2" : 1 }
ProximitySDK.getInstance().sendTag("key3", true);
// -> { "key1" : "value", "key2" : 1, "key3" : true }
ProximitySDK.getInstance().sendTag("key3", false);
// -> { "key1" : "value", "key2" : 1, "key3" : false }
ProximitySDK.getInstance().sendTag("key2", null);
// -> { "key1" : "value", "key3" : false }
```

#### sendTags

The `sendTags` method allow to set or unset a multiple tags at a time.

The method can be called multiple times. When sending different keys, its
effects are cumulative. If the same key is used, the last value overwrites the
previous ones.

```java
Map<String,Object> tags = new HashMap<>();
tags.put("key1", "value");
tags.put("key2", 1);
tags.put("key3", true);
ProximitySDK.getInstance().sendTags(tags);
// -> { "key1" : "value", "key2" : 1, "key3" : true }

Map<String,Object> tags = new HashMap<>();
tags.put("key2", null);
tags.put("key3", false);
ProximitySDK.getInstance().sendTags(tags);
// -> { "key1" : "value", "key3" : false }

```

### Customizing the notifications

It is possibile to to customize the icon and color of the advertising
notifications.

In order to customize the icon, include in your project a drawable named
`ic_stat_jointag_default`.

We recommend using [Android Asset Studio][android-asset-studio] to quickly and
easily generate small icons with the correct settings.

If you prefer to create your own icons, make sure to generate the icon for the
following densities:

- mdpi
- hdpi
- xhdpi
- xxhdpi
- xxxhdpi

In order to customize the color for all notifications, include in your project a
color resource named `jointag_notification_color`. The default icon color is
`#2576BC`

---

> :warning: **Note**: with some versions of the android build tool a duplicate
> resource error may arise during the resource merging phase of the build. In
> this case it is sufficient to include the new drawable resources using a
> **version qualifier**.

> Eg:
>
> - `drawable-hdpi-v7/ic_stat_jointag_default.png`
> - `drawable-mdpi-v7/ic_stat_jointag_default.png`
> - `drawable-xhdpi-v7/ic_stat_jointag_default.png`
> - `drawable-xxhdpi-v7/ic_stat_jointag_default.png`
> - `drawable-xxxhdpi-v7/ic_stat_jointag_default.png`

### Receive custom events

You can receive custom advertising events (if configured in the backend) to
integrate application-specific features by registering a `CustomActionListener`
object using the `addCustomActionListener` method of `ProximitySDK`.

When the application user interacts with a custom-action notification, the
`onCustomAction` method is invoked by passing a `payload` string object.

> :warning: **Note**: No activity is launched by default, and the application is
> not automatically brought to the foreground. It is up to you to decide whether
> to launch an activity in response to receiving the custom event.

> Furthermore make sure you are adding your CustomActionListener in the
> `onCreate()` method of your Application class, otherwise you will likely loose
> the custom event.

### Programmatically Disable Advertising

It is possible to programmatically disable/enable the advertising delivery by
setting the SDK's `advertisingEnabled` property to `false`. It is useful for
example to disable the delivery of advertising for specific users of the
application. In that case, simply change the property as soon as the user sign
in or out of the application.
The default value for the property is `true`.

```java
// disable advertising delivery
ProximitySDK.getInstance().setAdvertisingEnabled(false);
// enable advertising delivery
ProximitySDK.getInstance().setAdvertisingEnabled(true);
```

---

> :warning: **Note**: disabling the advertising via
> `setAdvertisingEnabled(false)` has always effect regardless of any other
> control method (ie: the user consent)

### GDPR Consent

As a publisher, you should implement a user consent flow either **manually** or
using a **Consent Management Platform** (CMP) and request for vendor and purpose
consents as outlined in IAB Europe’s Mobile In-App CMP API v2.0: Transparency
& Consent Framework.

#### Enabling the Consent Flow support

To ensure that the SDK support the handling of user-consent preferences when a
IAB-compatible CMP library is present, you must enable the feature through the
`ProximitySDK.enabledCmp()` static method or the
`com.jointag.proximity.CMP_ENABLED` meta-data in your AndroidManifest.xml file.

> If you are following the [Manual
> Initialization](#user-content-manual-initialization) procedure, this method
> **must** be called before calling the library init method to guarantee an
> error-free process.

#### Using Consent Management Platform

When configuring a third-party CMP to use with the Jointag Proximity SDK, the
following requirements must be met in order to enable the delivery of
advertising:

- Your CMP **must be** fully compliant with v2.0 of the `IAB Tansparency &
  Consent Framework`. IAB TCF v1.1 is no longer supported.
- A `Custom Publisher Purpose` **must be** configured in the CMP, and it **must
    be** the first custom purpose.

#### Implementing a Custom Consent Flow

If you need to handle the user consent flow manually without the use of a
IAB-compatible CMP library, or if the CMP you are using do not allow the
customization of **custom publisher purpose**, it is possibile to do so by
implementing an in-app consent screen and interacting with the SDK using the
following methods:

```java
// Retrieve or update the manual user profiling consent
ProximitySDK.getInstance().getManualConsent(ManualConsent.Profiling);
ProximitySDK.getInstance().setManualConsent(ManualConsent.Profiling, true);

// Retrieve or update the manual user monitoring consent
ProximitySDK.getInstance().getManualConsent(ManualConsent.Monitoring);
ProximitySDK.getInstance().setManualConsent(ManualConsent.Monitoring, true);

// Retrieve or update the manual user advertising consent
ProximitySDK.getInstance().getManualConsent(ManualConsent.Advertising);
ProximitySDK.getInstance().setManualConsent(ManualConsent.Advertising, true);

// Retrieve or update the manual user advanced tracking consent
ProximitySDK.getInstance().getManualConsent(ManualConsent.AdvancedTracking);
ProximitySDK.getInstance().setManualConsent(ManualConsent.AdvancedTracking, true);
```

---

> :warning: **Note**: When the **manual consent method** is used in the presence
> of a **CMP library**, the choices made using the above methods take precedence
> over the choices made by the user in the CMP library screen.

### Background Jobs ID

On Android 5.0 or later, the SDK use [Job Services][job-services] to perform
scheduled tasks. Since the JobScheduler need to identify each jobs with a unique
ID, the SDK gives each job an identifier starting from a predefined constant
(`182734746`).

If for any reason this identifier should collide with any one used by the
application or by another library, it is possible to change the starting value
for the SDK job identifiers by __calling `Scheduler.setBaseJobId` before the
`ProximitySDK.init` call__.

```java
import com.jointag.proximity.scheduler.Scheduler;

...
int newJobID = 123456;
Scheduler.setBaseJobId(newJobID);

ProximitySDK.init(this, "YOUR_API_KEY", "YOUR_API_SECRET");
...

```

---

[kotlin]: https://kotlinlang.org/
[google-play-services]: https://developers.google.com/android/guides/overview#the_google_play_services_apk
[androidx-support-library]: https://developer.android.com/topic/libraries/support-library/index.html
[android-beacon-library]: https://github.com/AltBeacon/android-beacon-library
[requesting-permissions]: https://developer.android.com/training/permissions/requesting.html
[access-fine-location]: https://developer.android.com/reference/android/Manifest.permission#ACCESS_FINE_LOCATION
[access-coarse-location]: https://developer.android.com/reference/android/Manifest.permission#ACCESS_COARSE_LOCATION
[post-notifications]: https://developer.android.com/reference/android/Manifest.permission#POST_NOTIFICATIONS
[access-background-location]: https://developer.android.com/reference/android/Manifest.permission#ACCESS_BACKGROUND_LOCATION
[android-asset-studio]: https://romannurik.github.io/AndroidAssetStudio/icons-notification.html
[job-services]: https://developer.android.com/reference/android/app/job/jobscheduler
[hms-core]: https://developer.huawei.com/consumer/en/doc/start/integrate-hms-core-0000001053032045
[huawei-ads-kit]: https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/publisher-service-introduction-0000001070671805
[huawei-location-kit]: https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/introduction-0000001050706106
[jcenter-migration]: https://developer.android.com/studio/build/jcenter-migration
