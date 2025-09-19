package com.jointag.proximity.examples.empty.ui

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.jointag.proximity.ProximitySDK
import com.jointag.proximity.examples.empty.R
import com.next14.cmp.CMPActivityContract
import com.next14.cmp.CMPSdk

class MainActivity : AppCompatActivity() {
    private val cmpLauncher = registerForActivityResult(CMPActivityContract()) {
        this.verifyPermissions()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ProximitySDK.getInstance().externalUserId = "some-external-id"
        ProximitySDK.getInstance().sendTag("color", "blue")
        CMPSdk.getInstance(this).start("CADD2B2AD06D8A0CAEE658E3C05E615A") { success: Boolean, error: Throwable? ->
            if (CMPSdk.getInstance(this).shouldPresentCMP()) {
                cmpLauncher.launch(true)
            } else {
                this.verifyPermissions()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Always check for pending permissions
        ProximitySDK.getInstance().checkPendingPermissions()
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Request next permission
                this.requestLocationPermission()
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                this.displayNotificationPermissionRationale()
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Request next permission
                    this.requestBackgroundPermission()
                }
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                displayLocationPermissionRationale()
            }
        }
    }

    private fun displayNotificationPermissionRationale() {
        val snackbar = Snackbar.make(findViewById(R.id.activity_main_coordinatorlayout), "You must grant Notification permissions to continue testing the SDK", Snackbar.LENGTH_SHORT)
        snackbar.setAction("GRANT") { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) requestNotificationPermission() }
        snackbar.show()
    }

    private fun displayLocationPermissionRationale() {
        val snackbar = Snackbar.make(findViewById(R.id.activity_main_coordinatorlayout), "You must grant Location permissions to continue testing the SDK", Snackbar.LENGTH_SHORT)
        snackbar.setAction("GRANT") { view: View? -> ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE) }
        snackbar.show()
    }

    private fun verifyPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermission()
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestBackgroundPermission()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
    }

    private fun requestLocationPermission() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        // Show the Location exaplanation dialog only once
        if (!preferences.getBoolean("EXPLAINED_LOCATION", false)) {
            preferences.edit { putBoolean("EXPLAINED_LOCATION", true) }
            AlertDialog.Builder(this).apply {
                setTitle("Required Permission")
                setMessage("This app collects location data to enable <some-feature> even when the app is closed or not in use. This data is also used to provide ads/support advertising/support ads.")
                setPositiveButton("Continue") { dialogInterface: DialogInterface?, i: Int -> ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE) }
            }.show()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun requestBackgroundPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), BACKGROUND_PERMISSION_REQUEST_CODE)
        }
    }

    companion object {
        const val CMP_REQUEST_CODE: Int = 100
        const val LOCATION_PERMISSION_REQUEST_CODE: Int = 101
        const val BACKGROUND_PERMISSION_REQUEST_CODE: Int = 102
        const val NOTIFICATION_PERMISSION_REQUEST_CODE: Int = 103
    }
}
