package com.explorads.podcast.core.service.download.handler;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import androidx.annotation.NonNull;

import com.explorads.podcast.core.storage.DBReader;
import com.explorads.podcast.core.storage.DBWriter;
import com.explorads.podcast.model.MediaMetadataRetrieverCompat;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.concurrent.ExecutionException;

import com.explorads.podcast.event.UnreadItemsUpdateEvent;
import com.explorads.podcast.net.download.serviceinterface.DownloadRequest;
import com.explorads.podcast.model.download.DownloadResult;
import com.explorads.podcast.core.sync.queue.SynchronizationQueueSink;
import com.explorads.podcast.core.util.ChapterUtils;
import com.explorads.podcast.model.download.DownloadError;
import com.explorads.podcast.model.feed.FeedItem;
import com.explorads.podcast.model.feed.FeedMedia;
import com.explorads.podcast.net.sync.model.EpisodeAction;

/**
 * Handles a completed media download.
 */
public class MediaDownloadedHandler implements Runnable {
    private static final String TAG = "MediaDownloadedHandler";
    private final DownloadRequest request;
    private final Context context;
    private DownloadResult updatedStatus;

    public MediaDownloadedHandler(@NonNull Context context, @NonNull DownloadResult status,
                                  @NonNull DownloadRequest request) {
        this.request = request;
        this.context = context;
        this.updatedStatus = status;
    }

    @Override
    public void run() {
        FeedMedia media = DBReader.getFeedMedia(request.getFeedfileId());
        if (media == null) {
            Log.e(TAG, "Could not find downloaded media object in database");
            return;
        }
        // media.setDownloaded modifies played state
        boolean broadcastUnreadStateUpdate = media.getItem() != null && media.getItem().isNew();
        media.setDownloaded(true);
        media.setFile_url(request.getDestination());
        media.setSize(new File(request.getDestination()).length());
        media.checkEmbeddedPicture(); // enforce check

        // check if file has chapters
        if (media.getItem() != null && !media.getItem().hasChapters()) {
            media.setChapters(ChapterUtils.loadChaptersFromMediaFile(media, context));
        }

        if (media.getItem() != null && media.getItem().getPodcastIndexChapterUrl() != null) {
            ChapterUtils.loadChaptersFromUrl(media.getItem().getPodcastIndexChapterUrl(), false);
        }
        // Get duration
        String durationStr = null;
        try (MediaMetadataRetrieverCompat mmr = new MediaMetadataRetrieverCompat()) {
            mmr.setDataSource(media.getFile_url());
            durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            media.setDuration(Integer.parseInt(durationStr));
            Log.d(TAG, "Duration of file is " + media.getDuration());
        } catch (NumberFormatException e) {
            Log.d(TAG, "Invalid file duration: " + durationStr);
        } catch (Exception e) {
            Log.e(TAG, "Get duration failed", e);
        }

        final FeedItem item = media.getItem();

        try {
            DBWriter.setFeedMedia(media).get();

            // we've received the media, we don't want to autodownload it again
            if (item != null) {
                item.disableAutoDownload();
                // setFeedItem() signals (via EventBus) that the item has been updated,
                // so we do it after the enclosing media has been updated above,
                // to ensure subscribers will get the updated FeedMedia as well
                DBWriter.setFeedItem(item).get();
                if (broadcastUnreadStateUpdate) {
                    EventBus.getDefault().post(new UnreadItemsUpdateEvent());
                }
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "MediaHandlerThread was interrupted");
        } catch (ExecutionException e) {
            Log.e(TAG, "ExecutionException in MediaHandlerThread: " + e.getMessage());
            updatedStatus = new DownloadResult(media, media.getEpisodeTitle(),
                    DownloadError.ERROR_DB_ACCESS_ERROR, false, e.getMessage());
        }

        if (item != null) {
            EpisodeAction action = new EpisodeAction.Builder(item, EpisodeAction.DOWNLOAD)
                    .currentTimestamp()
                    .build();
            SynchronizationQueueSink.enqueueEpisodeActionIfSynchronizationIsActive(context, action);
        }
    }

    @NonNull
    public DownloadResult getUpdatedStatus() {
        return updatedStatus;
    }
}
