# ProximitySDK for Android

ProximitySDK for Android is a comprehensive library that enables your app to
deliver location-based advertising, analytics, and proximity-driven experiences
with minimal effort. The SDK provides:

- Geofencing and place detection
- Bluetooth beacon and WiFi monitoring
- User behavior tracking and analytics
- GDPR-compliant consent management
- Custom event and notification handling
- Seamless integration with both Google Play Services and Huawei Mobile Services

## Table of Contents

* [📋 Requirements](#-requirements)
* [🧰 Installation](#-installation)
    + [Add the Maven repository](#add-the-maven-repository)
    + [Enable Java 8 language features and APIs](#enable-java-8-language-features-and-apis)
    + [Add the library](#add-the-library)
    + [Supporting Huawei Mobile Services](#supporting-huawei-mobile-services)
* [🚀 Usage](#-usage)
    + [🔧 Initialization](#-initialization)
    + [🔐 Permissions and hardware requirements](#-permissions-and-hardware-requirements)
    + [🔒 Network security](#-network-security)
    + [👤 Tracking user identifier](#-tracking-user-identifier)
    + [🏷 Data Tags](#-data-tags)
    + [🎨 Customizing the notifications](#-customizing-the-notifications)
    + [📬 Receive custom events](#-receive-custom-events)
    + [🚫 Programmatically Disable Advertising](#-programmatically-disable-advertising)
    + [🛡 GDPR Consent](#-gdpr-consent)
    + [⏰ Background Jobs ID](#-background-jobs-id)
* [🧪 Testing with a simulated location](#-testing-with-a-simulated-location)
    + [Injecting a position the SDK will read](#injecting-a-position-the-sdk-will-read)
    + [Re-entering a geofence](#re-entering-a-geofence)
    + [When the campaign stops serving](#when-the-campaign-stops-serving)
    + [Reading the outcome](#reading-the-outcome)

---

## 📋 Requirements

- Minimum API level: `21` (Android 5.0)
- Kotlin [2.2.0](https://kotlinlang.org/) or higher, if the application is written in
  Kotlin

---

## 🧰 Installation

### Add the Maven repository

To download the SDK package you can use our Maven repository. To include it, add
the following lines to your build.gradle (Module: app) file:

```groovy
repositories {
    google()
    mavenCentral()
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
    implementation("com.jointag:proximitysdk:1.20.0")
}
```

### Supporting Huawei Mobile Services

You can enable ProximitySDK support for **Huawei Mobile Services** by following
the official Huawei documentation for integrating [Huawei HMS Core][hms-core] into your
application, then adding the dependencies for [Huawei Ads Kit][huawei-ads-kit]
and [Huawei Location Kit][huawei-location-kit] to your app/build.gradle
file:

```groovy
dependencies {
    <...app dependencies...>
    implementation("com.jointag:proximitysdk:1.20.0")
    // Huawei Mobile Services dependencies
    implementation("com.huawei.hms:ads-identifier:3.4.41.302")
    implementation("com.huawei.hms:location:4.0.0.300")
}
```

**Note**: Huawei libraries may have a **higher minimum API level** requirement
than the ProximitySDK library. Make sure to check the official Huawei
documentation for the minimum API level required by the versions of the
libraries you are including.

#### For distribution on Huawei AppGallery only

If your app will only be available on Huawei AppGallery and you want to avoid
including any Google Play Services dependencies that ProximitySDK includes, you
can specify the group "com.google.android.gms" from the library transitive
dependencies in your app/build.gradle file:

```groovy
dependencies {
    // ProximitySDK SDK
    implementation("com.jointag:proximitysdk:1.20.0") {
        exclude group: "com.google.android.gms"
    }
}
```

If you do this **and build your release with R8 or ProGuard** — which is the
default for release builds — add the following rule to your application's
`proguard-rules.pro`:

```proguard
-dontwarn com.google.android.gms.**
```

The SDK references Play Services classes so that they are used when available,
and guards every access against `NoClassDefFoundError`. Once you exclude the
dependency those classes are no longer on the shrinker's classpath, and without
this rule R8 fails the build with `Missing class com.google.android.gms.…`.

The rule is not shipped in the SDK's consumer rules on purpose: Play Services
are a regular dependency of the library, present for everyone who does not
deliberately exclude them, and suppressing those warnings for every integrator
would also hide warnings about their own Play Services usage.

---

## 🚀 Usage

### 🔧 Initialization

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

If you plan to implement a user consent flow manually or using a IAB-compliant CMP,
you must specify the following entry:

```xml
        <meta-data
            android:name="com.jointag.proximity.CMP_ENABLED"
            android:value="true" />
```

> :warning: **Note** Some features, such as the ability to subscribe to custom
> events, still requires their relative initialization code to be execute in the
> `Application` onCreate method.

#### Manual Initialization

If you want to manually initialize the SDK, you have to include the
initialization code in your app **Application class** onCreate method.

If you don't have an `Application` class, read the following section on how to
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

### 🔐 Permissions and hardware requirements

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
public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Message explaining why granting the user location permission is useful to the user", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }
}
```

After having requested the permissions to the user and the user has granted the
required permissions (typically in the `onRequestPermissionsResult` callback of
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

##### For applications running on Android 12.0 or later

Beacon detection requires the [`BLUETOOTH_SCAN`][bluetooth-scan] permission. As with
`ACCESS_BACKGROUND_LOCATION`, the SDK does **not** declare it: applications that do not use beacons
would inherit a sensitive permission they neither need nor can justify in Play review. Applications
that do use beacons declare it themselves.

Add the following to your AndroidManifest.xml:

```xml
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
```

Declare it **without** `android:usesPermissionFlags="neverForLocation"`. The SDK derives the user's
physical position from beacon proximity, so that flag would misdescribe what the permission is used
for. One consequence worth knowing: on Android 12 and later the system delivers scan results only
when location permission has been granted as well, so `BLUETOOTH_SCAN` alone is not enough.

The permission must then be **requested at runtime**, exactly as location permission is. Until it is
granted the SDK cannot detect beacons, and `ProximitySDK#getBluetoothState` reports `DENIED`.

`BLUETOOTH_CONNECT` is **not** required: the SDK only scans for advertising packets and never
connects or pairs with a device.

The legacy `BLUETOOTH` and `BLUETOOTH_ADMIN` permissions are declared by the SDK with
`android:maxSdkVersion="30"`, so they no longer apply on Android 12 and later.

##### For applications running on Android 13.0 or later

The request for [`POST_NOTIFICATIONS`][post-notifications] permission has to be
implemented by the application that includes the SDK. The request can be
implemented in any point in the application, but it's recommended to ask the
user for notification permission as soon as possible..


#### Permission compatibility matrix

Permissions the SDK declares on its own, and those the application is expected to declare or request.

| Permission | API levels | Declared by | Runtime request |
| --- | --- | --- | --- |
| `INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`, `CHANGE_WIFI_STATE` | 21+ | SDK | not required |
| `RECEIVE_BOOT_COMPLETED`, `WAKE_LOCK` | 21+ | SDK | not required |
| `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION` | 21+ | SDK | **application** |
| `ACCESS_BACKGROUND_LOCATION` | 29+ | **application** | **application** |
| `BLUETOOTH`, `BLUETOOTH_ADMIN` | 21–30 | SDK | not required |
| `BLUETOOTH_SCAN` | 31+ | **application** | **application** |
| `POST_NOTIFICATIONS` | 33+ | SDK | **application** |
| `com.google.android.gms.permission.AD_ID` | 21+ | SDK | not required |

`BLUETOOTH_CONNECT` is deliberately absent: the SDK scans but never connects to a device.

Permissions marked as declared by the **application** are sensitive ones the SDK keeps out of its
own manifest on purpose, so that applications not using the corresponding feature do not inherit
them and do not have to justify or strip them.

### 🔒 Network security

Every endpoint the SDK contacts is HTTPS, and it never falls back to cleartext. On
`targetSdk >= 28` Android already refuses cleartext traffic by default, so an application
embedding the SDK needs no configuration for the SDK's own traffic to be safe.

#### Why the SDK does not ship a network security configuration

`android:networkSecurityConfig` and `android:usesCleartextTraffic` are **single-valued attributes**
of the `<application>` element. If the SDK declared either of them, the manifest merger would fail
for every application that already declares its own:

```
Manifest merger failed : Attribute application@networkSecurityConfig
value=(@xml/your_config) from AndroidManifest.xml
Suggestion: add 'tools:replace="android:networkSecurityConfig"' to <application> element
```

Applications would have to add `tools:replace` just to keep building, and the SDK would be
overriding a security decision that belongs to the host application. So the SDK declares neither,
and the recommended configuration is documented here instead.

#### Recommended configuration

If your application does not already have one, this is a configuration that works with the SDK.
Create `res/xml/network_security_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
</network-security-config>
```

and reference it from your `<application>` element:

```xml
<application android:networkSecurityConfig="@xml/network_security_config">
```

Cleartext is refused everywhere, only the system trust anchors are accepted, and Certificate
Transparency is left at its default with no opt-out. The sample application ships exactly this file
as a reference.

If you already have a configuration of your own, nothing needs to change on the SDK's account, as
long as the SDK endpoints are reachable over HTTPS with the system trust anchors. Note in particular
that a `debug-overrides` block adding a user-installed CA applies to debug builds only and does not
affect the SDK in release.

#### Certificate Transparency

From Android 16 the platform enforces Certificate Transparency by default for applications
targeting the new API level: a certificate presented over HTTPS has to carry Signed Certificate
Timestamps proving it was published to public CT logs, or the connection is refused. This applies
to everything the SDK fetches, because both the API client and the notification image downloader
go through `HttpURLConnection`.

The SDK's own endpoints are compliant and need nothing from you.

What is worth checking is any host **you** control that the SDK ends up reaching — most often the
server that hosts the images attached to your campaigns. A certificate without SCTs makes the
image download fail; the notification is still delivered, but without its picture, and the only
trace is a line in the log:

```
Failed to download notification image from https://…
```

To check a host, look for embedded SCTs in the certificate it serves:

```bash
echo | openssl s_client -connect example.com:443 -servername example.com 2>/dev/null \
  | openssl x509 -text -noout | grep -c "Log ID"
```

Two or more, from logs run by different operators, is what the policy expects. Any certificate
issued by a public CA in recent years satisfies this without special handling; a certificate
issued by a private or internal CA does not, and that is the case to watch for.

### 👤 Tracking user identifier

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
of your choice with our` installationId`. Typically this identifier must be set
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

### 🏷 Data Tags

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

### 🎨 Customizing the notifications

It is possibile to customize the icon and color of the advertising
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

### 📬 Receive custom events

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

### 🚫 Programmatically Disable Advertising

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

### 🛡 GDPR Consent

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

### ⏰ Background Jobs ID

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

#### When jobs do not run on time

The SDK schedules five periodic jobs: configuration refresh, trace upload, place
status, WiFi scan and database cleanup. Android applies runtime quotas to them,
more strictly from Android 16 onwards and regardless of whether a foreground
service is running, so the system can defer or stop a job that was due.

The symptom is not an error: deliveries simply arrive later, or less often. To
make that attributable the SDK logs it at `WARN`, which is the default log level,
so no extra configuration is needed to see it:

```
Job TracesJob was held back at 09:14:32: out of job execution quota
Job ConfigurationJob was stopped by the system before finishing: exceeded its maximum execution time
```

The first line comes from the system's own record of why a job stayed pending and
is available from Android 16; the second is reported from Android 12 onwards.
Neither is actionable from inside the SDK — they describe decisions the platform
has already taken — but they turn a vague "fewer notifications than before" into
something that can be traced to a cause.

---

## 🧪 Testing with a simulated location

Triggering a delivery on an emulator takes more than typing coordinates into the
emulator UI. The SDK requests positions through `FusedLocationProviderClient` with
[balanced power accuracy][priority-balanced] — a priority that relies on network and
WiFi positioning and never powers the GPS. A position injected into the emulated GPS
provider, which is what `adb emu geo fix` does, is therefore never read.

When the device cannot satisfy the request, the SDK logs:

```
GMSLocationClient Resolvable Api Exception: RESOLUTION_REQUIRED
```

This is the [location settings check][location-settings] reporting the problem, not a
failure of the SDK: on a stock emulator image only the `gps` provider is enabled, and
the *Google Location Accuracy* toggle that would add network positioning lives in the
`partner` settings namespace, which cannot be written from the shell.

### Injecting a position the SDK will read

Register [test providers][test-providers] and feed them directly. The shell needs the
`MOCK_LOCATION` app operation first, otherwise every command fails with
`SecurityException: android from uid 2000 not allowed to perform MOCK_LOCATION`.

```bash
adb shell appops set com.android.shell android:mock_location allow

# Replace with coordinates covered by the campaign you want to test
LOC="45.46427,9.18951"

for PROVIDER in fused gps network; do
  adb shell cmd location providers add-test-provider "$PROVIDER"
  adb shell cmd location providers set-test-provider-enabled "$PROVIDER" true
  adb shell cmd location providers set-test-provider-location "$PROVIDER" \
      --location "$LOC" --accuracy 10
done
```

Two details decide whether this works:

* **`fused` is the provider that matters.** Feeding only `gps` reproduces exactly the
  problem described above.
* **The argument is `--location <latitude>,<longitude>`.** The `--latitude` and
  `--longitude` flags found in older guides are rejected with `Unknown option`.

Check that the position was accepted:

```bash
adb shell dumpsys location | grep 'last location=Location\[fused'
```

The `et=` field holds the elapsed time of the fix. If it does not advance between two
reads, no new position is reaching the device.

### Re-entering a geofence

Place status is persisted, so pushing the same coordinates a second time produces
nothing — the SDK has already recorded the entry. A new `enter` event needs a real
exit first: move the device away, keep it there long enough for the exits to be
processed, then come back.

```bash
FAR="41.90278,12.49637"      # anywhere well outside the test area
TARGET="45.46427,9.18951"

set_location() {
  for PROVIDER in fused gps network; do
    adb shell cmd location providers set-test-provider-location "$PROVIDER" \
        --location "$1" --accuracy 10 > /dev/null
  done
}

for i in $(seq 1 24); do set_location "$FAR"; sleep 20; done     # leave
for i in $(seq 1 15); do set_location "$TARGET"; sleep 20; done  # return
```

A single faraway fix is not enough: the SDK evaluates place status periodically
rather than on every position, so the device has to stay outside the area for several
minutes before the exits are recorded.

### When the campaign stops serving

A campaign is delivered to a given device only once. After that the advertising
endpoint answers `404`, which the SDK treats as *no campaign available* and discards
silently — so the symptom is an absence of notifications rather than an error.

Clearing the application data regenerates the device identifier and the campaign is
served again. Runtime permissions can be restored from the shell:

```bash
APP_ID=your.application.id

adb shell pm clear "$APP_ID"
adb shell pm grant "$APP_ID" android.permission.ACCESS_FINE_LOCATION
adb shell pm grant "$APP_ID" android.permission.ACCESS_COARSE_LOCATION
adb shell pm grant "$APP_ID" android.permission.ACCESS_BACKGROUND_LOCATION
adb shell pm grant "$APP_ID" android.permission.POST_NOTIFICATIONS
```

Consents are not permissions and are not restored this way. Re-enable them from the
application, or set them programmatically before the test — see
[🛡 GDPR Consent](#-gdpr-consent):

```java
ProximitySDK sdk = ProximitySDK.getInstance();
sdk.setManualConsent(ManualConsent.Profiling, true);
sdk.setManualConsent(ManualConsent.Monitoring, true);
sdk.setManualConsent(ManualConsent.Advertising, true);
sdk.setManualConsent(ManualConsent.AdvancedTracking, true);
```

### Reading the outcome

With verbose logging enabled — see [🔧 Initialization](#-initialization) — the whole
delivery chain is visible in the log:

| Log line | Meaning |
|----------|---------|
| `Geofence <id> distance = <n> meters` | the position reached the SDK and the area was evaluated |
| `AdvManager.response = Success(status=200)` | the platform returned a campaign |
| `AdvManager.response = ServerError(status=404)` | no campaign available for this device and place |
| `Displayed notification with ID <id> for ADV '<message>'` | the notification was posted |
| `Failed to build or display notification` | the notification could not be built |

Confirm what actually reached the notification drawer with:

```bash
adb shell dumpsys notification | grep "pkg=$APP_ID"
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
[bluetooth-scan]: https://developer.android.com/reference/android/Manifest.permission#BLUETOOTH_SCAN
[android-asset-studio]: https://romannurik.github.io/AndroidAssetStudio/icons-notification.html
[job-services]: https://developer.android.com/reference/android/app/job/jobscheduler
[hms-core]: https://developer.huawei.com/consumer/en/doc/start/integrate-hms-core-0000001053032045
[huawei-ads-kit]: https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/publisher-service-introduction-0000001070671805
[huawei-location-kit]: https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/introduction-0000001050706106
[jcenter-migration]: https://developer.android.com/studio/build/jcenter-migration
[priority-balanced]: https://developers.google.com/android/reference/com/google/android/gms/location/Priority#PRIORITY_BALANCED_POWER_ACCURACY
[location-settings]: https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
[test-providers]: https://developer.android.com/reference/android/location/LocationManager#addTestProvider(java.lang.String,%20boolean,%20boolean,%20boolean,%20boolean,%20boolean,%20boolean,%20boolean,%20int,%20int)
