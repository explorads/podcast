package com.explorads.podcast.config;


import android.app.Application;

import com.explorads.podcast.PodcastApp;
import com.explorads.podcast.core.ApplicationCallbacks;

public class ApplicationCallbacksImpl implements ApplicationCallbacks {

    @Override
    public Application getApplicationInstance() {
        return PodcastApp.getInstance();
    }
}
