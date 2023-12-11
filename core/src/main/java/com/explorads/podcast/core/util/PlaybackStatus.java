package com.explorads.podcast.core.util;

import com.explorads.podcast.core.preferences.PlaybackPreferences;
import com.explorads.podcast.core.service.playback.PlaybackService;
import com.explorads.podcast.model.feed.FeedMedia;

public abstract class PlaybackStatus {
    /**
     * Reads playback preferences to determine whether this FeedMedia object is
     * currently being played and the current player status is playing.
     */
    public static boolean isCurrentlyPlaying(FeedMedia media) {
        return isPlaying(media) && PlaybackService.isRunning
                && ((PlaybackPreferences.getCurrentPlayerStatus() == PlaybackPreferences.PLAYER_STATUS_PLAYING));
    }

    public static boolean isPlaying(FeedMedia media) {
        return PlaybackPreferences.getCurrentlyPlayingMediaType() == FeedMedia.PLAYABLE_TYPE_FEEDMEDIA
                && media != null
                && PlaybackPreferences.getCurrentlyPlayingFeedMediaId() == media.getId();
    }
}
