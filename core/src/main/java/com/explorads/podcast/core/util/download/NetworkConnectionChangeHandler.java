package com.explorads.podcast.core.util.download;

import android.content.Context;

import com.explorads.podcast.core.storage.DBTasks;
import com.explorads.podcast.net.download.serviceinterface.DownloadServiceInterface;
import com.google.android.exoplayer2.util.Log;
import com.explorads.podcast.core.util.NetworkUtils;

public abstract class NetworkConnectionChangeHandler {
    private static final String TAG = "NetworkConnectionChangeHandler";
    private static Context context;

    public static void init(Context context) {
        NetworkConnectionChangeHandler.context = context;
    }

    public static void networkChangedDetected() {
        if (NetworkUtils.isAutoDownloadAllowed()) {
            Log.d(TAG, "auto-dl network available, starting auto-download");
            DBTasks.autodownloadUndownloadedItems(context);
        } else { // if new network is Wi-Fi, finish ongoing downloads,
            // otherwise cancel all downloads
            if (NetworkUtils.isNetworkRestricted()) {
                Log.i(TAG, "Device is no longer connected to Wi-Fi. Cancelling ongoing downloads");
                DownloadServiceInterface.get().cancelAll(context);
            }
        }
    }
}
