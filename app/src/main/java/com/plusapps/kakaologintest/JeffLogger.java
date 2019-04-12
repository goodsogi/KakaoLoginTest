package com.plusapps.kakaologintest;

import android.util.Log;

public class JeffLogger {

    private static final String LOG_TAG = "JEFF";


    public static void log(String message) {
        Log.d(LOG_TAG, "###################################################");
        Log.d(LOG_TAG, message);
    }

    public static void serverLog(String message) {
        Log.d(LOG_TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        Log.d(LOG_TAG, message);
    }

    public static void kakaoLog(String message) {
        Log.d(LOG_TAG, "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        Log.d(LOG_TAG, message);
    }
}
