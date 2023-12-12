package com.explorads.podcast.core;

import android.content.Context;

import com.explorads.podcast.core.service.download.AntennapodHttpClient;
import com.explorads.podcast.core.service.download.DownloadServiceInterfaceImpl;
import com.explorads.podcast.core.sync.SyncService;
import com.explorads.podcast.core.sync.queue.SynchronizationQueueSink;
import com.explorads.podcast.core.util.NetworkUtils;
import com.explorads.podcast.core.util.download.NetworkConnectionChangeHandler;
import com.explorads.podcast.core.util.gui.NotificationUtils;
import com.explorads.podcast.net.download.serviceinterface.DownloadServiceInterface;
//import com.explorads.podcast.net.ssl.SslProviderInstaller;
import com.explorads.podcast.storage.database.PodDBAdapter;
import com.explorads.podcast.core.preferences.PlaybackPreferences;
import com.explorads.podcast.core.preferences.SleepTimerPreferences;
import com.explorads.podcast.core.preferences.UsageStatistics;
import com.explorads.podcast.storage.preferences.UserPreferences;

import java.io.File;

import com.explorads.podcast.net.ssl.SslProviderInstaller;

public class ClientConfigurator {
    private static boolean initialized = false;

    public static synchronized void initialize(Context context) {
        if (initialized) {
            return;
        }
        PodDBAdapter.init(context);
        UserPreferences.init(context);
        UsageStatistics.init(context);
        PlaybackPreferences.init(context);
        SslProviderInstaller.install(context);
        NetworkUtils.init(context);
        NetworkConnectionChangeHandler.init(context);
        DownloadServiceInterface.setImpl(new DownloadServiceInterfaceImpl());
        SynchronizationQueueSink.setServiceStarterImpl(() -> SyncService.sync(context));
        AntennapodHttpClient.setCacheDirectory(new File(context.getCacheDir(), "okhttp"));
        AntennapodHttpClient.setProxyConfig(UserPreferences.getProxyConfig());
        SleepTimerPreferences.init(context);
        NotificationUtils.createChannels(context);
        initialized = true;
    }
}
