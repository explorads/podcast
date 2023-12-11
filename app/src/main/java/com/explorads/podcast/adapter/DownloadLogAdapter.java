package com.explorads.podcast.adapter;

import android.app.Activity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.explorads.podcast.R;
import com.explorads.podcast.adapter.actionbutton.DownloadActionButton;
import com.explorads.podcast.core.storage.DBReader;

import com.explorads.podcast.activity.MainActivity;
import com.explorads.podcast.core.util.DownloadErrorLabel;
import com.explorads.podcast.core.util.download.FeedUpdateManager;
import com.explorads.podcast.model.download.DownloadError;
import com.explorads.podcast.model.download.DownloadResult;
import com.explorads.podcast.model.feed.Feed;
import com.explorads.podcast.model.feed.FeedMedia;
import com.explorads.podcast.ui.common.ThemeUtils;
import com.explorads.podcast.view.viewholder.DownloadLogItemViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a list of DownloadStatus entries.
 */
public class DownloadLogAdapter extends BaseAdapter {
    private static final String TAG = "DownloadLogAdapter";

    private final Activity context;
    private List<DownloadResult> downloadLog = new ArrayList<>();

    public DownloadLogAdapter(Activity context) {
        super();
        this.context = context;
    }

    public void setDownloadLog(List<DownloadResult> downloadLog) {
        this.downloadLog = downloadLog;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DownloadLogItemViewHolder holder;
        if (convertView == null) {
            holder = new DownloadLogItemViewHolder(context, parent);
            holder.itemView.setTag(holder);
        } else {
            holder = (DownloadLogItemViewHolder) convertView.getTag();
        }
        bind(holder, getItem(position), position);
        return holder.itemView;
    }

    private void bind(DownloadLogItemViewHolder holder, DownloadResult status, int position) {
        String statusText = "";
        if (status.getFeedfileType() == Feed.FEEDFILETYPE_FEED) {
            statusText += context.getString(R.string.download_type_feed);
        } else if (status.getFeedfileType() == FeedMedia.FEEDFILETYPE_FEEDMEDIA) {
            statusText += context.getString(R.string.download_type_media);
        }
        statusText += " · ";
        statusText += DateUtils.getRelativeTimeSpanString(status.getCompletionDate().getTime(),
                System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, 0);
        holder.status.setText(statusText);

        if (status.getTitle() != null) {
            holder.title.setText(status.getTitle());
        } else {
            holder.title.setText(R.string.download_log_title_unknown);
        }

        if (status.isSuccessful()) {
            holder.icon.setTextColor(ThemeUtils.getColorFromAttr(context, R.attr.icon_green));
            holder.icon.setText("{fa-check-circle}");
            holder.icon.setContentDescription(context.getString(R.string.download_successful));
            holder.secondaryActionButton.setVisibility(View.INVISIBLE);
            holder.reason.setVisibility(View.GONE);
            holder.tapForDetails.setVisibility(View.GONE);
        } else {
            if (status.getReason() == DownloadError.ERROR_PARSER_EXCEPTION_DUPLICATE) {
                holder.icon.setTextColor(ThemeUtils.getColorFromAttr(context, R.attr.icon_yellow));
                holder.icon.setText("{fa-exclamation-circle}");
            } else {
                holder.icon.setTextColor(ThemeUtils.getColorFromAttr(context, R.attr.icon_red));
                holder.icon.setText("{fa-times-circle}");
            }
            holder.icon.setContentDescription(context.getString(R.string.error_label));
            holder.reason.setText(DownloadErrorLabel.from(status.getReason()));
            holder.reason.setVisibility(View.VISIBLE);
            holder.tapForDetails.setVisibility(View.VISIBLE);

            if (newerWasSuccessful(position, status.getFeedfileType(), status.getFeedfileId())) {
                holder.secondaryActionButton.setVisibility(View.INVISIBLE);
                holder.secondaryActionButton.setOnClickListener(null);
                holder.secondaryActionButton.setTag(null);
            } else {
                holder.secondaryActionIcon.setImageResource(R.drawable.ic_refresh);
                holder.secondaryActionButton.setVisibility(View.VISIBLE);

                if (status.getFeedfileType() == Feed.FEEDFILETYPE_FEED) {
                    holder.secondaryActionButton.setOnClickListener(v -> {
                        holder.secondaryActionButton.setVisibility(View.INVISIBLE);
                        Feed feed = DBReader.getFeed(status.getFeedfileId());
                        if (feed == null) {
                            Log.e(TAG, "Could not find feed for feed id: " + status.getFeedfileId());
                            return;
                        }
                        FeedUpdateManager.runOnce(context, feed);
                    });
                } else if (status.getFeedfileType() == FeedMedia.FEEDFILETYPE_FEEDMEDIA) {
                    holder.secondaryActionButton.setOnClickListener(v -> {
                        holder.secondaryActionButton.setVisibility(View.INVISIBLE);
                        FeedMedia media = DBReader.getFeedMedia(status.getFeedfileId());
                        if (media == null) {
                            Log.e(TAG, "Could not find feed media for feed id: " + status.getFeedfileId());
                            return;
                        }
                        new DownloadActionButton(media.getItem()).onClick(context);
                        ((MainActivity) context).showSnackbarAbovePlayer(
                                R.string.status_downloading_label, Toast.LENGTH_SHORT);
                    });
                }
            }
        }
    }

    private boolean newerWasSuccessful(int downloadStatusIndex, int feedTypeId, long id) {
        for (int i = 0; i < downloadStatusIndex; i++) {
            DownloadResult status = downloadLog.get(i);
            if (status.getFeedfileType() == feedTypeId && status.getFeedfileId() == id && status.isSuccessful()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getCount() {
        return downloadLog.size();
    }

    @Override
    public DownloadResult getItem(int position) {
        if (position < downloadLog.size()) {
            return downloadLog.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
