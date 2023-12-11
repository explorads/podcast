package com.explorads.podcast.view.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.explorads.podcast.R;
import com.explorads.podcast.adapter.CoverLoader;
import com.explorads.podcast.adapter.actionbutton.ItemActionButton;
import com.explorads.podcast.ui.common.CircularProgressBar;
import com.explorads.podcast.ui.common.SquareImageView;
import com.explorads.podcast.ui.common.ThemeUtils;
import com.google.android.material.elevation.SurfaceColors;
import com.explorads.podcast.activity.MainActivity;
import com.explorads.podcast.core.feed.util.ImageResourceUtils;
import com.explorads.podcast.core.util.DateFormatter;
import com.explorads.podcast.core.util.PlaybackStatus;
import com.explorads.podcast.event.playback.PlaybackPositionEvent;
import com.explorads.podcast.model.feed.FeedItem;
import com.explorads.podcast.model.feed.FeedMedia;
import com.explorads.podcast.net.download.serviceinterface.DownloadServiceInterface;

public class HorizontalItemViewHolder extends RecyclerView.ViewHolder {
    public final CardView card;
    public final ImageView secondaryActionIcon;
    private final SquareImageView cover;
    private final TextView title;
    private final TextView date;
    private final ProgressBar progressBar;
    private final CircularProgressBar circularProgressBar;
    private final View progressBarReplacementSpacer;

    private final MainActivity activity;
    private FeedItem item;

    public HorizontalItemViewHolder(MainActivity activity, ViewGroup parent) {
        super(LayoutInflater.from(activity).inflate(R.layout.horizontal_itemlist_item, parent, false));
        this.activity = activity;

        card = itemView.findViewById(R.id.card);
        cover = itemView.findViewById(R.id.cover);
        title = itemView.findViewById(R.id.titleLabel);
        date = itemView.findViewById(R.id.dateLabel);
        secondaryActionIcon = itemView.findViewById(R.id.secondaryActionIcon);
        circularProgressBar = itemView.findViewById(R.id.circularProgressBar);
        progressBar = itemView.findViewById(R.id.progressBar);
        progressBarReplacementSpacer = itemView.findViewById(R.id.progressBarReplacementSpacer);
        itemView.setTag(this);
    }

    public void bind(FeedItem item) {
        this.item = item;

        card.setAlpha(1.0f);
        float density = activity.getResources().getDisplayMetrics().density;
        card.setCardBackgroundColor(SurfaceColors.getColorForElevation(activity, 1 * density));
        new CoverLoader(activity)
                .withUri(ImageResourceUtils.getEpisodeListImageLocation(item))
                .withFallbackUri(item.getFeed().getImageUrl())
                .withCoverView(cover)
                .load();
        title.setText(item.getTitle());
        date.setText(DateFormatter.formatAbbrev(activity, item.getPubDate()));
        date.setContentDescription(DateFormatter.formatForAccessibility(item.getPubDate()));
        ItemActionButton actionButton = ItemActionButton.forItem(item);
        actionButton.configure(secondaryActionIcon, secondaryActionIcon, activity);
        secondaryActionIcon.setFocusable(false);

        FeedMedia media = item.getMedia();
        if (media == null) {
            circularProgressBar.setPercentage(0, item);
            setProgressBar(false, 0);
        } else {
            if (PlaybackStatus.isCurrentlyPlaying(media)) {
                card.setCardBackgroundColor(ThemeUtils.getColorFromAttr(activity, R.attr.colorSurfaceVariant));
            }

            if (item.getMedia().getDuration() > 0 && item.getMedia().getPosition() > 0) {
                setProgressBar(true, 100.0f * item.getMedia().getPosition() / item.getMedia().getDuration());
            } else {
                setProgressBar(false, 0);
            }

            if (DownloadServiceInterface.get().isDownloadingEpisode(media.getDownload_url())) {
                float percent = 0.01f * DownloadServiceInterface.get().getProgress(media.getDownload_url());
                circularProgressBar.setPercentage(Math.max(percent, 0.01f), item);
                circularProgressBar.setIndeterminate(
                        DownloadServiceInterface.get().isEpisodeQueued(media.getDownload_url()));
            } else if (media.isDownloaded()) {
                circularProgressBar.setPercentage(1, item); // Do not animate 100% -> 0%
                circularProgressBar.setIndeterminate(false);
            } else {
                circularProgressBar.setPercentage(0, item); // Animate X% -> 0%
                circularProgressBar.setIndeterminate(false);
            }
        }
    }

    public void bindDummy() {
        card.setAlpha(0.1f);
        new CoverLoader(activity)
                .withResource(android.R.color.transparent)
                .withCoverView(cover)
                .load();
        title.setText("████ █████");
        date.setText("███");
        secondaryActionIcon.setImageDrawable(null);
        circularProgressBar.setPercentage(0, null);
        circularProgressBar.setIndeterminate(false);
        setProgressBar(true, 50);
    }

    public boolean isCurrentlyPlayingItem() {
        return item != null && item.getMedia() != null && PlaybackStatus.isCurrentlyPlaying(item.getMedia());
    }

    public void notifyPlaybackPositionUpdated(PlaybackPositionEvent event) {
        setProgressBar(true, 100.0f * event.getPosition() / event.getDuration());
    }

    private void setProgressBar(boolean visible, float progress) {
        progressBar.setVisibility(visible ? ViewGroup.VISIBLE : ViewGroup.GONE);
        progressBarReplacementSpacer.setVisibility(visible ? View.GONE : ViewGroup.VISIBLE);
        progressBar.setProgress(Math.max(5, (int) progress)); // otherwise invisible below the edge radius
    }
}
