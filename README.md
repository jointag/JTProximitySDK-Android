Jointag Proximity SDK for Android
===

This library allows you to integrate Jointag Proximity into your Android app.

## Requirements

Minimum API level: 14 (Android 4.0)

_**Warning**_: _to use functionalities that rely on BLE, the minimum API level is 18 (Android 4.3). If the device API level is between 14 and 17 the SDK won't be able to access BLE and therefore it will be not possible to obtain data from BLE devices._

## Adding ProximitySDK to your project

To download the SDK package you can use our Maven repository. To include it, add the following lines to your build.gradle (Module: app) file:

```gradle
repositories {
    maven { url "http://93.57.20.29:8081/artifactory/jointag" }
}
```

Now add the ProximitySDK dependency (use latest SDK version).

```gradle
dependencies {
    // ProximitySDK SDK
    compile 'com.jointag:proximitysdk:1.2.0'
}
```

## Other dependencies

- The library requires [Google Play Services](https://developers.google.com/android/guides/overview#the_google_play_services_apk) framework installed on the device, and the [Google Play Services Ads](https://developers.google.com/android/guides/setup) library (version >= 7.0) compiled into the project.
- The library requires [Android Support Library](https://developer.android.com/topic/libraries/support-library/index.html) library (version >= 25) compiled into the project.
- The library required [Glide](https://github.com/bumptech/glide) library compiled into the project.

To include the required libraries add the following to your dependencies.

```gradle
dependencies {
    compile 'com.android.support:appcompat-v7:26.0.2'
    compile 'com.google.android.gms:play-services-ads:11.0.2'
    compile 'com.github.bumptech.glide:glide:4.1.1'
    compile 'com.jointag:proximitysdk:1.2.0'
}
```

## Permissions and hardware requirements

This SDK uses location permissions. For application running on Android 6.0 or later, the request for [`ACCESS_FINE_LOCATION`](https://developer.android.com/reference/android/Manifest.permission.html#ACCESS_FINE_LOCATION) or [`ACCESS_COARSE_LOCATION`](https://developer.android.com/reference/android/Manifest.permission.html#ACCESS_COARSE_LOCATION) permission has to be implemented by the application that includes the SDK. The request can be implemented in any point of the application, but it's recommended to ask the user for location permission as soon as possible, because until the permission is not granted the SDK can't enable proximity features involving GPS and beacons. If the permission is not granted the proximity features involving GPS and beacons will not be enabled.

To implement the permission request dialog in your application follow the official [Requesting Permissions at Run Time](https://developer.android.com/training/permissions/requesting.html) documentation.

The proximity features require the bluetooth to be enabled, if the bluetooth is off the SDK will not be able to retrieve any proximity information about beacons.

## Initialization

Add the following to the `onCreate` method in your `Application` class.

```java
ProximitySDK.init(this, "YOUR_API_KEY", "YOUR_API_SECRET");
```

During the development process it's possible to initialize the SDK in debug mode. This way all the data will be sent to a sandbox server, preventing to put test data in production databases.
To initialize the SDK in debug mode please add the following lines of code instead.

```java
ProximitySDK.setDebug(true);
ProximitySDK.init(this, "YOUR_API_KEY", "YOUR_API_SECRET");
```

## Tracking users

The SDK associates each tracked request with the *advertisingId*. If the *advertisingId* is not available due to a user permission denial, the device can be identified by the *installationId*. The *installationId* identifies in particular a specific installation of the SDK in a certain app on a certain device. If the app containing the SDK is uninstalled and then installed again the *installationId* will be a different one. You can retrieve the *installationId* after the initialization of the SDK anywhere in your code with the following line:

```java
ProximitySDK.getInstance().getInstallationId();
```

## Customizing the notifications

It is possibile to to customize the look (icon and title) of the advertising notifications.

In order to customize the icon, include in your project a drawable named `ic_stat_jointag_default`.

We recommend using [Android Asset Studio](https://romannurik.github.io/AndroidAssetStudio/icons-notification.html) to quickly and easily generate small icons with the correct settings.

If you prefer to create your own icons, make sure to generate the icon for the following densities:

- mdpi
- hdpi
- xhdpi
- xxhdpi
- xxxhdpi

In order to customize the title, include in your project a string resource named `jointag_notification_title`.