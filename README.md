# Jointag Proximity SDK for Android

## Table of Contents

1. [Requirements](#user-content-requirements)
2. [Installation](#user-content-installation)
    1. [Add the Maven repository](#user-content-add-the-maven-repository)
    2. [Add the library](#user-content-add-the-library)
    3. [Other dependencies](#user-content-other-dependencies)
3. [Usage](#user-content-usage)
    1. [Initialization](#user-content-initialization)
        1. [Manual Initialization](#user-content-manual-initialization)
        2. [Automatic Initialization](#user-content-automatic-initialization)
    2. [Permissions and hardware requirements](#user-content-permissions-and-hardware-requirements)
    3. [Tracking user identifier](#user-content-tracking-user-identifier)
    4. [Customizing the notifications](#user-content-customizing-the-notifications)
    5. [Receive custom events](#user-content-receive-custom-events)
    6. [Programmatically Disable Advertising](#programmatically-disable-advertising)
    7. [GDPR](#user-content-gdpr)
    8. [Background Jobs ID](#user-content-background-jobs-id)

This library allows you to integrate Jointag Proximity into your Android app.

## Requirements

Minimum API level: `16` (Android 4.1)

> **Note**: to use functionalities that rely on BLE, the minimum API level is
> `18` (Android 4.3). If the device API level is between `16` and `17` the SDK
> won't be able to access BLE and therefore it will be not possible to obtain
> data from BLE devices.

## Installation

### Add the Maven repository

To download the SDK package you can use our Maven repository. To include it, add
the following lines to your build.gradle (Module: app) file:

```gradle
repositories {
    maven { url "https://artifactory.jointag.com/artifactory/jointag" }
}
```

### Add the library

Now add the ProximitySDK dependency (use latest SDK version).

```gradle
dependencies {
    // ProximitySDK SDK
    implementation 'com.jointag:proximitysdk:1.14.+'
}
```

### Other dependencies

Additional dependencies **should automatically be downloaded** and included along
with the library through the previous gradle declaration.

For the sake of clarity, the included dependencies comprise of the following:

- [Kotlin][kotlin] Kotlin Std library (version >= 1.3.72)
- [Google Play Services][google-play-services] Ads and Location libraries (version >= `16.0.0`).
- [Android Support Library][android-support-library] library (version >= `28.0.0`).
- [Android Beacon Library][android-beacon-library] An Android library to interact with beacons (version == `2.16.3`)

If you don't use Gradle to handle project building, or want to manually include the required libraries, add the following to your
dependencies block in the app/build.gradle file.

```gradle
dependencies {
    <...>
    implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72'
    implementation 'org.altbeacon:android-beacon-library:2.16.3'
    implementation 'com.google.android.gms:play-services-ads-identifier:16.0.0'
    implementation 'com.google.android.gms:play-services-location:16.0.0'
    implementation 'com.android.support:appcompat-v7:28.0.0'
    <...>
}
```

## Usage

### Initialization

#### Manual Initialization

Add the following call to `ProximitySDK.init()` to the `onCreate()` method in
your `Application` subclass.

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

#### Automatic Initialization

If you are unable to create or access an Android Application class, you can
opt to automatically initialize the SDK at application launch by adding the
following entries to the application's AndroidManifest.xml file, inside the
`<application>` tag:

```xml
        <meta-data
            android:name="com.jointag.proximity.API_KEY"
            android:value="YOUR_API_KEY" />
        <meta-data
            android:name="com.jointag.proximity.API_SECRET"
            android:value="YOUR_API_SECRET" />
```

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

You can enable the integration to a IAB-compliat CMP with the following entry:

```xml
        <meta-data
            android:name="com.jointag.proximity.CMP_ENABLED"
            android:value="true" />
```

### Permissions and hardware requirements

This SDK uses user-location permissions to function. All required permissions
are declared in the SDK AndroidManifest file, and automatically added to your
application AndroidManifest when the library is included as a Gradle dependency
(see [Add the library](#add-the-library)).

**For application running on Android 6.0 or
later**, the request for [`ACCESS_FINE_LOCATION`][access-fine-location] or
[`ACCESS_COARSE_LOCATION`][access-coarse-location] permission has to be
implemented by the application that includes the SDK. The request can be
implemented in any point of the application, but it's recommended to ask the
user for location permission as soon as possible, because until the permission
is not granted the SDK can't enable proximity features involving GPS and
beacons.

**For applications running Android 10.0 or later**, the additional permission
`ACCESS_BACKGROUND_LOCATION` must be declared in the application
AndroidManifest.xml file and requested to the user for the SDK to work properly.

Add the following to your AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
```

**For applications running Android 11.0 or later**, as per
[official documentation](https://developer.android.com/preview/privacy), the
location and background permission should be requested incrementally in separate
calls.

To implement the permission request dialog in your application follow the
official [Requesting Permissions at Run Time][requesting-permissions]
documentation.

An example of implementation is the following:

```java
if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
    if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
        Toast.makeText(context, "Message explaining why granting the user location permission is usefull to the user", Toast.LENGTH_SHORT).show();
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
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

### Tracking user identifier

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

### Customizing the notifications

It is possibile to to customize the look (icon and title) of the advertising
notifications and the monitoring notification.

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

In order to customize the title for all notifications, include in your project a
string resource named `jointag_notification_title`.

---

> **Note**: with some versions of the android build tool a duplicate resource
> error may arise during the resource merging phase of the build. In this case
> it is sufficient to include the new drawable resources using a version
> qualifier. Eg:
>
```drawable-hdpi-v7/ic_stat_jointag_default.png
drawable-mdpi-v7/ic_stat_jointag_default.png
drawable-xhdpi-v7/ic_stat_jointag_default.png
drawable-xxhdpi-v7/ic_stat_jointag_default.png
drawable-xxxhdpi-v7/ic_stat_jointag_default.png
```

### Receive custom events

You can receive custom advertising events (if configured in the backend) to
integrate application-specific features by registering a `CustomActionListener`
object using the `addCustomActionListener` method of `ProximitySDK`.

When the application user interacts with a custom-action notification, the
`onCustomAction` method is invoked by passing a `payload` string object.

Since the `CustomActionListener` object is retained by `ProximitySDK`, remember
to remove the listener when the owning instance is being deallocated to avoid
unwanted retaining or NullPointerException. It is therefore good practice to use
a long-life object as CustomActionListener, such as the Application object.

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

### GDPR

As a publisher, you should integrate a Consent Management Platform (CMP) and
request for vendor and purpose consents as outlined in IAB Europe’s Mobile
In-App CMP API v2.0: Transparency & Consent Framework.

To ensure that the SDK support the handling of user-consent preferences when a
IAB-compatible CMP library is present, you must enable the feature through the
`ProximitySDK.enabledCmp()` static method.

**This method must be called before the library initialization to guarantee an
error-free process**.

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
[android-support-library]: https://developer.android.com/topic/libraries/support-library/index.html
[android-beacon-library]: https://github.com/AltBeacon/android-beacon-library
[requesting-permissions]: https://developer.android.com/training/permissions/requesting.html
[access-fine-location]: https://developer.android.com/reference/android/manifest.permission.html#access-fine-location
[access-coarse-location]: https://developer.android.com/reference/android/manifest.permission.html#access-coarse-location
[android-asset-studio]: https://romannurik.github.io/AndroidAssetStudio/icons-notification.html
[job-services]: https://developer.android.com/reference/android/app/job/jobscheduler
