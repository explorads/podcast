package com.explorads.podcast.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.Log;

import com.explorads.podcast.core.util.download.NetworkConnectionChangeHandler;
import com.explorads.podcast.core.ClientConfigurator;

public class ConnectivityActionReceiver extends BroadcastReceiver {
    private static final String TAG = "ConnectivityActionRecvr";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (TextUtils.equals(intent.getAction(), ConnectivityManager.CONNECTIVITY_ACTION)) {
            Log.d(TAG, "Received intent");

            ClientConfigurator.initialize(context);
            NetworkConnectionChangeHandler.networkChangedDetected();
        }
    }
}
