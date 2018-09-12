# Jointag Proximity SDK for Android

[![build status](https://git.hqhosting.it/jointag/proximity-platform/proximitysdk-android/badges/master/build.svg)](https://git.hqhosting.it/jointag/proximity-platform/proximitysdk-android/commits/master)
[![coverage report](https://git.hqhosting.it/jointag/proximity-platform/proximitysdk-android/badges/master/coverage.svg)](https://git.hqhosting.it/jointag/proximity-platform/proximitysdk-android/commits/master)

## Table of Contents

1. [Requirements](#user-content-requirements)
2. [Installation](#user-content-installation)
    1. [Add the Maven repository](#user-content-add-the-maven-repository)
    2. [Add the library](#user-content-add-the-library)
    3. [Other dependencies](#user-content-other-dependencies)
3. [Initialization](#user-content-initialization)
    1. [Simple Initialization](#user-content-simple-initialization)
    2. [Permissions and hardware requirements](#user-content-permissions-and-hardware-requirements)
    3. [Background Jobs](#user-content-background-jobs)
    4. [Tracking users](#user-content-tracking-users)
    5. [Customizing the notifications](#user-content-customizing-the-notifications)
4. [Receive custom events](#user-content-receive-custom-events)

This library allows you to integrate Jointag Proximity into your Android app.

## Requirements

Minimum API level: `14` (Android 4.0)

> **Note**: to use functionalities that rely on BLE, the minimum API level is
> `18` (Android 4.3). If the device API level is between `14` and `17` the SDK
> won't be able to access BLE and therefore it will be not possible to obtain
> data from BLE devices.

## Installation

### Add the Maven repository

To download the SDK package you can use our Maven repository. To include it, add
the following lines to your build.gradle (Module: app) file:

```gradle
repositories {
    maven { url "http://93.57.20.29:8081/artifactory/jointag" }
}
```

### Add the library

Now add the ProximitySDK dependency (use latest SDK version).

```gradle
dependencies {
    // ProximitySDK SDK
    implementation 'com.jointag:proximitysdk:1.6.+'
}
```

### Other dependencies

- The library requires [Google Play Services][google-play-services] library
  (version >= `11.6.0`) compiled into the project.
- The library requires [Android Support Library][android-support-library]
  library (version >= `25.2.0`) compiled into the project.

To include the required libraries add the following to your dependencies.

```gradle
dependencies {
    implementation 'com.jointag:proximitysdk:1.6.+'
    implementation 'com.android.support:appcompat-v7:25.2.0'
    implementation 'com.google.android.gms:play-services-ads:11.6.0'
}
```

## Initialization

### Simple Initialization

Add the following call to `ProximitySDK.init()` to the `onCreate()` method in
your `Application` class.

```java
@Override
public void onCreate() {
    super.onCreate();
    ProximitySDK.init(this, "YOUR_API_KEY", "YOUR_API_SECRET");
}
```

During the development process it's possible to initialize the SDK in debug
mode. This way all the data will be sent to a sandbox server, preventing to put
test data in production databases. To initialize the SDK in debug mode please
add the following lines of code instead.

```java
@Override
public void onCreate() {
    ProximitySDK.setDebug(true);
    ProximitySDK.init(this, "YOUR_API_KEY", "YOUR_API_SECRET");
}
```

### Permissions and hardware requirements

This SDK uses location permissions. For application running on Android 6.0 or
later, the request for [`ACCESS_FINE_LOCATION`][access-fine-location] or
[`ACCESS_COARSE_LOCATION`][access-coarse-location] permission has to be
implemented by the application that includes the SDK. The request can be
implemented in any point of the application, but it's recommended to ask the
user for location permission as soon as possible, because until the permission
is not granted the SDK can't enable proximity features involving GPS and
beacons. If the permission is not granted the proximity features involving GPS
and beacons will not be enabled.

To implement the permission request dialog in your application follow the
official [Requesting Permissions at Run Time][requesting-permissions]
documentation.

Only for the first application run, after having requested the permissions to
the user and the user has granted the required permissions (tipically in the
`onRequestPermissionsResult` callback of the Activity), the monitoring process
can be resumed by calling the `ProximitySDK#checkPendingPermissions` method.

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

The proximity features require the bluetooth to be enabled, if the bluetooth is
off the SDK will not be able to retrieve any proximity information about
beacons.

### Background Jobs

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

### Tracking users

The SDK associates each tracked request with the *advertisingId*. If the
*advertisingId* is not available due to a user permission denial, the device can
be identified by the *installationId*. The *installationId* identifies in
particular a specific installation of the SDK in a certain app on a certain
device. If the app containing the SDK is uninstalled and then installed again
the *installationId* will be a different one. You can retrieve the
*installationId* after the initialization of the SDK anywhere in your code with
the following line:

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

## Receive custom events

You can receive custom advertising events (if configured in the backend) to
integrate application-specific features by registering a `CustomActionListener`
object using the `addCustomActionListener` method of `ProximitySDK`.

When the application user interacts with a custom-action notification, the
`onCustomAction` method is invoked by passing a `payload` string object.

Since the `CustomActionListener` object is retained by `ProximitySDK`, remember
to remove the listener when the owning instance is being deallocated to avoid
unwanted retaining or NullPointerException. It is therefore good practice to use
a long-life object as CustomActionListener, such as the Application object.

---

[google-play-services]: https://developers.google.com/android/guides/overview#the_google_play_services_apk
[android-support-library]: https://developer.android.com/topic/libraries/support-library/index.html
[requesting-permissions]: https://developer.android.com/training/permissions/requesting.html
[access-fine-location]: https://developer.android.com/reference/android/manifest.permission.html#access-fine-location
[access-coarse-location]: https://developer.android.com/reference/android/manifest.permission.html#access-coarse-location
[android-asset-studio]: https://romannurik.github.io/androidassetstudio/icons-notification.html
[job-services]: https://developer.android.com/reference/android/app/job/jobscheduler
