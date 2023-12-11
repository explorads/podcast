package com.explorads.podcast;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.os.StrictMode;

import com.explorads.podcast.activity.SplashActivity;
import com.explorads.podcast.config.ApplicationCallbacksImpl;
import com.explorads.podcast.core.ApCoreEventBusIndex;
import com.explorads.podcast.error.CrashReportWriter;
import com.explorads.podcast.error.RxJavaErrorHandlerSetup;
import com.explorads.podcast.spa.SPAUtil;
import com.google.android.material.color.DynamicColors;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.MaterialModule;

import com.explorads.podcast.core.ClientConfig;
import com.explorads.podcast.core.ClientConfigurator;
import com.explorads.podcast.preferences.PreferenceUpgrader;

import org.greenrobot.eventbus.EventBus;



/** Main application class. */
public class PodcastApp extends Application {

    private static PodcastApp singleton;

    public static PodcastApp getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ClientConfig.USER_AGENT = "AntennaPod/" + BuildConfig.VERSION_NAME;
        ClientConfig.applicationCallbacks = new ApplicationCallbacksImpl();

        Thread.setDefaultUncaughtExceptionHandler(new CrashReportWriter());
        RxJavaErrorHandlerSetup.setupRxJavaErrorHandler();

        if (BuildConfig.DEBUG) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .penaltyLog()
                    .penaltyDropBox()
                    .detectActivityLeaks()
                    .detectLeakedClosableObjects()
                    .detectLeakedRegistrationObjects();
            StrictMode.setVmPolicy(builder.build());
        }

        singleton = this;

        ClientConfigurator.initialize(this);
        PreferenceUpgrader.checkUpgrades(this);

        Iconify.with(new FontAwesomeModule());
        Iconify.with(new MaterialModule());

        SPAUtil.sendSPAppsQueryFeedsIntent(this);
        EventBus.builder()
                .addIndex(new ApEventBusIndex())
                .addIndex(new ApCoreEventBusIndex())
                .logNoSubscriberMessages(false)
                .sendNoSubscriberEvent(false)
                .installDefaultEventBus();

        DynamicColors.applyToActivitiesIfAvailable(this);
    }

    public static void forceRestart() {
        Intent intent = new Intent(getInstance(), SplashActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(cn);
        getInstance().startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

}
