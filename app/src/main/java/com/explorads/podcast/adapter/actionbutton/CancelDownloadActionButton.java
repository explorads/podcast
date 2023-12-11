package com.explorads.podcast.adapter.actionbutton;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.explorads.podcast.R;
import com.explorads.podcast.net.download.serviceinterface.DownloadServiceInterface;
import com.explorads.podcast.model.feed.FeedItem;
import com.explorads.podcast.model.feed.FeedMedia;
import com.explorads.podcast.storage.preferences.UserPreferences;
import com.explorads.podcast.core.storage.DBWriter;

public class CancelDownloadActionButton extends ItemActionButton {

    public CancelDownloadActionButton(FeedItem item) {
        super(item);
    }

    @Override
    @StringRes
    public int getLabel() {
        return R.string.cancel_download_label;
    }

    @Override
    @DrawableRes
    public int getDrawable() {
        return R.drawable.ic_cancel;
    }

    @Override
    public void onClick(Context context) {
        FeedMedia media = item.getMedia();
        DownloadServiceInterface.get().cancel(context, media);
        if (UserPreferences.isEnableAutodownload()) {
            item.disableAutoDownload();
            DBWriter.setFeedItem(item);
        }
    }
}
