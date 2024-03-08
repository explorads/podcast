package com.explorads.podcast.managers;

import android.content.Context;

import com.google.android.gms.ads.appopen.AppOpenAd;

public class AppOpenAdManager { private static final String LOG_TAG = "AppOpenAdManager";
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921";

    private AppOpenAd appOpenAd = null;
    private boolean isLoadingAd = false;
    private boolean isShowingAd = false;

    /** Constructor. */
    public AppOpenAdManager() {}

    /** Request an ad. */
    private void loadAd(Context context) {
        // We will implement this below.
    }

    /** Check if ad exists and can be shown. */
    private boolean isAdAvailable() {
        return appOpenAd != null;
    }
}
