package com.explorads.podcast.adapter.actionbutton;

import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.explorads.podcast.R;
import com.explorads.podcast.model.feed.FeedItem;
import com.explorads.podcast.model.feed.FeedMedia;
import com.explorads.podcast.model.playback.MediaType;
import com.explorads.podcast.core.preferences.UsageStatistics;
import com.explorads.podcast.core.service.playback.PlaybackService;
import com.explorads.podcast.core.util.NetworkUtils;
import com.explorads.podcast.core.util.playback.PlaybackServiceStarter;
import com.explorads.podcast.dialog.StreamingConfirmationDialog;

public class StreamActionButton extends ItemActionButton {

    public StreamActionButton(FeedItem item) {
        super(item);
    }

    @Override
    @StringRes
    public int getLabel() {
        return R.string.stream_label;
    }

    @Override
    @DrawableRes
    public int getDrawable() {
        return R.drawable.ic_stream;
    }

    @Override
    public void onClick(Context context) {
        final FeedMedia media = item.getMedia();
        if (media == null) {
            return;
        }
        UsageStatistics.logAction(UsageStatistics.ACTION_STREAM);

        if (!NetworkUtils.isStreamingAllowed()) {
            new StreamingConfirmationDialog(context, media).show();
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
