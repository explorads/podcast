package com.explorads.podcast.core.feed.util;

import android.util.Log;
import com.explorads.podcast.model.feed.Feed;
import com.explorads.podcast.model.feed.FeedItem;
import com.explorads.podcast.model.feed.FeedMedia;
import com.explorads.podcast.model.playback.MediaType;
import com.explorads.podcast.core.preferences.PlaybackPreferences;
import com.explorads.podcast.storage.preferences.UserPreferences;
import com.explorads.podcast.model.playback.Playable;

import static com.explorads.podcast.model.feed.FeedPreferences.SPEED_USE_GLOBAL;

/**
 * Utility class to use the appropriate playback speed based on {@link PlaybackPreferences}
 */
public final class PlaybackSpeedUtils {
    private static final String TAG = "PlaybackSpeedUtils";

    private PlaybackSpeedUtils() {
    }

    /**
     * Returns the currently configured playback speed for the specified media.
     */
    public static float getCurrentPlaybackSpeed(Playable media) {
        float playbackSpeed = SPEED_USE_GLOBAL;
        MediaType mediaType = null;

        if (media != null) {
            mediaType = media.getMediaType();
            playbackSpeed = PlaybackPreferences.getCurrentlyPlayingTemporaryPlaybackSpeed();

            if (playbackSpeed == SPEED_USE_GLOBAL && media instanceof FeedMedia) {
                FeedItem item = ((FeedMedia) media).getItem();
                if (item != null) {
                    Feed feed = item.getFeed();
                    if (feed != null && feed.getPreferences() != null) {
                        playbackSpeed = feed.getPreferences().getFeedPlaybackSpeed();
                    } else {
                        Log.d(TAG, "Can not get feed specific playback speed: " + feed);
                    }
                }
            }
        }

        if (playbackSpeed == SPEED_USE_GLOBAL) {
            playbackSpeed = UserPreferences.getPlaybackSpeed(mediaType);
        }

        return playbackSpeed;
    }
}
