package com.jointag.proximity.examples.empty.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.jointag.proximity.ProximitySDK;
import com.jointag.proximity.examples.empty.R;
import com.next14.cmp.CMPSdk;

public class MainActivity extends AppCompatActivity {
    public static final int CMP_REQUEST_CODE = 100;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    public static final int BACKGROUND_PERMISSION_REQUEST_CODE = 102;
    public static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProximitySDK.getInstance().setExternalUserId("some-external-id");
        ProximitySDK.getInstance().sendTag("color", "blue");
        CMPSdk.getInstance(this).start("CADD2B2AD06D8A0CAEE658E3C05E615A", (b, throwable) -> {
            if (CMPSdk.getInstance(this).shouldPresentCMP()) {
                CMPSdk.getInstance(this).present(this, true, CMP_REQUEST_CODE);
            } else {
                this.verifyPermissions();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CMP_REQUEST_CODE && resultCode == RESULT_OK) {
            this.verifyPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Always check for pending permissions
        ProximitySDK.getInstance().checkPendingPermissions();
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Request next permission
                this.requestLocationPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_main_coordinatorlayout), "You must grant Notification permissions to continue testing the SDK", Snackbar.LENGTH_SHORT);
                snackbar.setAction("GRANT", view -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestNotificationPermission();
                    }
                });
                snackbar.show();
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Request next permission
                    this.requestBackgroundPermission();
                }
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_main_coordinatorlayout), "You must grant Location permissions to continue testing the SDK", Snackbar.LENGTH_SHORT);
                snackbar.setAction("GRANT", view -> ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE));
                snackbar.show();
            }
        }
    }

    private void verifyPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermission();
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestBackgroundPermission();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestNotificationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
    }

    private void requestLocationPermission() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Show the Location exaplanation dialog only once
        if (!preferences.getBoolean("EXPLAINED_LOCATION", false)) {
            preferences.edit().putBoolean("EXPLAINED_LOCATION", true).apply();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Required Permission");
            builder.setMessage("This app collects location data to enable <some-feature> even when the app is closed or not in use. This data is also used to provide ads/support advertising/support ads.");
            builder.setPositiveButton("Continue", (dialogInterface, i) -> ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE));
            builder.show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestBackgroundPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_PERMISSION_REQUEST_CODE);
        }
    }
}
