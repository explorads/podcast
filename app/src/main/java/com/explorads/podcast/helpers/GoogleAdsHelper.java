package com.explorads.podcast.helpers;

public class GoogleAdsHelper {



    private static boolean didShowAppOpenAd = false;

    public static boolean didShowAppOpenAd() {
        return didShowAppOpenAd;
    }

    public static void setShowAppOpenAd() {
        didShowAppOpenAd = true;
    }

//    private static boolean didShowInterstitialAd = false;
//
//    public static boolean didShowInterstitialAd() {
//        return didShowInterstitialAd;
//    }
//
//    public static void setShowInterstitialAd() {
//        didShowInterstitialAd = true;
//    }

}
