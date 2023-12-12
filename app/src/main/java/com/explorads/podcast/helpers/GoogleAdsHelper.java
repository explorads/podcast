package com.explorads.podcast.helpers;

public class GoogleAdsHelper {


    private static boolean didShowInterstitialAd = false;

    public static boolean didShowInterstitialAd() {
        return didShowInterstitialAd;
    }

    public static void setShowInterstitialAd() {
        didShowInterstitialAd = true;
    }

}
