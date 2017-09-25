package com.jointag.proximity.examples.empty;

import android.app.Application;

import com.jointag.proximity.ProximitySDK;
import com.jointag.proximity.util.Logger;

/**
 * @author marco.fraccaroli@metiswebdev.com on 25/09/17.
 */
public class SampleApplication extends Application {
    public static final String API_KEY = "59648a8a3ee5e036568873db";
    public static final String SECRET = "N2NiN2JkMjMtNGQ0Zi00ZGE2LTgwNDEtN2Q5YTY0MjY1ZWU2";

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.setPrefix("Sample.");
        Logger.setLogLevel(Logger.VERBOSE);
        ProximitySDK.init(this, API_KEY, SECRET);
    }
}
