package com.explorads.podcast.adapter.actionbutton;

import android.content.Context;
import android.view.KeyEvent;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.explorads.podcast.R;
import com.explorads.podcast.core.receiver.MediaButtonReceiver;
import com.explorads.podcast.core.util.PlaybackStatus;
import com.explorads.podcast.model.feed.FeedItem;
import com.explorads.podcast.model.feed.FeedMedia;

public class PauseActionButton extends ItemActionButton {

    public PauseActionButton(FeedItem item) {
        super(item);
    }

    @Override
    @StringRes
    public int getLabel() {
        return R.string.pause_label;
    }

    @Override
    @DrawableRes
    public int getDrawable() {
        return R.drawable.ic_pause;
    }

    @Override
    public void onClick(Context context) {
        FeedMedia media = item.getMedia();
        if (media == null) {
            return;
        }

        if (PlaybackStatus.isCurrentlyPlaying(media)) {
            context.sendBroadcast(MediaButtonReceiver.createIntent(context, KeyEvent.KEYCODE_MEDIA_PAUSE));
        }
    }
}
