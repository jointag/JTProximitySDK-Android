package com.jointag.proximity.examples.empty;

import android.app.Application;
import android.widget.Toast;

import com.jointag.proximity.ProximitySDK;
import com.jointag.proximity.listener.CustomActionListener;
import com.jointag.proximity.util.Logger;

/**
 * @author marco.fraccaroli@metiswebdev.com on 25/09/17.
 */
public class SampleApplication extends Application implements CustomActionListener {
    public static final String API_KEY = "598322107a5b646fd1785fd9";
    public static final String SECRET = "qxUe5vECy5DPeXmeFhPHOerVYdVDg34/StHkV3IPNdA927v4";

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.setTag("SampleApplication");
        Logger.setLogLevel(Logger.VERBOSE);
        ProximitySDK.init(this, API_KEY, SECRET);
        ProximitySDK.getInstance().addCustomActionListener(this);
    }

    @Override
    public void onCustomAction(String s) {
        Toast.makeText(this, "Received custom action " + s, Toast.LENGTH_LONG).show();
    }
}
