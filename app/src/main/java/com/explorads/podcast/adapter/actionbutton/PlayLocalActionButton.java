package com.explorads.podcast.adapter.actionbutton;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.explorads.podcast.R;
import com.explorads.podcast.model.feed.FeedItem;
import com.explorads.podcast.model.feed.FeedMedia;
import com.explorads.podcast.model.playback.MediaType;
import com.explorads.podcast.core.service.playback.PlaybackService;
import com.explorads.podcast.core.util.playback.PlaybackServiceStarter;

public class PlayLocalActionButton extends ItemActionButton {

    public PlayLocalActionButton(FeedItem item) {
        super(item);
    }

    @Override
    @StringRes
    public int getLabel() {
        return R.string.play_label;
    }

    @Override
    @DrawableRes
    public int getDrawable() {
        return R.drawable.ic_play_24dp;
    }

    @Override
    public void onClick(Context context) {
        final FeedMedia media = item.getMedia();
        if (media == null) {
            return;
        }

        new PlaybackServiceStarter(context, media)
                .callEvenIfRunning(true)
                .start();

        if (media.getMediaType() == MediaType.VIDEO) {
            context.startActivity(PlaybackService.getPlayerActivityIntent(context, media));
        }
    }
}
