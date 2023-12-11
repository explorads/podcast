package com.explorads.podcast.core.util;

import com.explorads.podcast.model.feed.Feed;
import com.explorads.podcast.storage.preferences.UserPreferences;

public abstract class FeedUtil {
    public static boolean shouldAutoDeleteItemsOnThatFeed(Feed feed) {
        if (!UserPreferences.isAutoDelete()) {
            return false;
        }
        return !feed.isLocalFeed() || UserPreferences.isAutoDeleteLocal();
    }
}
