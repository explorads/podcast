package com.explorads.podcast.core.util;

import android.text.TextUtils;
import android.util.Log;

import com.explorads.podcast.core.BuildConfig;

public class MyLogger {


    public static void log(String message){

        if (TextUtils.isEmpty(message)){
            return;
        }

        if (BuildConfig.DEBUG){
            Log.d("SNAP_LOG", message);
        }
    }

}
