package com.explorads.podcast.core.service.download.handler;

import android.content.Context;
import androidx.annotation.NonNull;

import com.explorads.podcast.core.storage.DBTasks;
import com.explorads.podcast.model.download.DownloadResult;
import com.explorads.podcast.model.feed.Feed;
import com.explorads.podcast.net.download.serviceinterface.DownloadRequest;
import com.explorads.podcast.parser.feed.FeedHandlerResult;

public class FeedSyncTask {
    private final Context context;
    private Feed savedFeed;
    private final FeedParserTask task;
    private FeedHandlerResult feedHandlerResult;

    public FeedSyncTask(Context context, DownloadRequest request) {
        this.context = context;
        this.task = new FeedParserTask(request);
    }

    public boolean run() {
        feedHandlerResult = task.call();
        if (!task.isSuccessful()) {
            return false;
        }

        savedFeed = DBTasks.updateFeed(context, feedHandlerResult.feed, false);
        return true;
    }

    @NonNull
    public DownloadResult getDownloadStatus() {
        return task.getDownloadStatus();
    }

    public Feed getSavedFeed() {
        return savedFeed;
    }

    public String getRedirectUrl() {
        return feedHandlerResult.redirectUrl;
    }
}
