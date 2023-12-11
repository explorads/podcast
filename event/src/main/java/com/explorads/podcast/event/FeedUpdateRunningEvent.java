package com.explorads.podcast.event;

public class FeedUpdateRunningEvent {
    public final boolean isFeedUpdateRunning;

    public FeedUpdateRunningEvent(boolean isRunning) {
        this.isFeedUpdateRunning = isRunning;
    }
}
