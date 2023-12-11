package com.test.antennapod.service.playback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.explorads.podcast.model.playback.MediaType;
import com.explorads.podcast.model.playback.Playable;
import com.explorads.podcast.playback.base.PlaybackServiceMediaPlayer;

public class DefaultPSMPCallback implements PlaybackServiceMediaPlayer.PSMPCallback {
    @Override
    public void statusChanged(PlaybackServiceMediaPlayer.PSMPInfo newInfo) {

    }

    @Override
    public void shouldStop() {

    }

    @Override
    public void onMediaChanged(boolean reloadUI) {

    }

    @Override
    public void onPostPlayback(@NonNull Playable media, boolean ended, boolean skipped, boolean playingNext) {

    }

    @Override
    public void onPlaybackStart(@NonNull Playable playable, int position) {

    }

    @Override
    public void onPlaybackPause(Playable playable, int position) {

    }

    @Override
    public Playable getNextInQueue(Playable currentMedia) {
        return null;
    }

    @Nullable
    @Override
    public Playable findMedia(@NonNull String url) {
        return null;
    }

    @Override
    public void onPlaybackEnded(MediaType mediaType, boolean stopPlaying) {

    }

    @Override
    public void ensureMediaInfoLoaded(@NonNull Playable media) {
    }
}