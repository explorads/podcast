package com.explorads.podcast.core.storage;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.explorads.podcast.model.feed.FeedItemFilter;
import com.explorads.podcast.model.feed.SortOrder;
import com.explorads.podcast.core.util.PlaybackStatus;
import com.explorads.podcast.model.feed.FeedItem;
import com.explorads.podcast.model.feed.FeedPreferences;
import com.explorads.podcast.net.download.serviceinterface.DownloadServiceInterface;
import com.explorads.podcast.storage.preferences.UserPreferences;
import com.explorads.podcast.core.util.NetworkUtils;
import com.explorads.podcast.core.util.PowerUtils;

/**
 * Implements the automatic download algorithm used by AntennaPod. This class assumes that
 * the client uses the {@link EpisodeCleanupAlgorithm}.
 */
public class AutomaticDownloadAlgorithm {
    private static final String TAG = "DownloadAlgorithm";

    /**
     * Looks for undownloaded episodes in the queue or list of new items and request a download if
     * 1. Network is available
     * 2. The device is charging or the user allows auto download on battery
     * 3. There is free space in the episode cache
     * This method is executed on an internal single thread executor.
     *
     * @param context  Used for accessing the DB.
     * @return A Runnable that will be submitted to an ExecutorService.
     */
    public Runnable autoDownloadUndownloadedItems(final Context context) {
        return () -> {

            // true if we should auto download based on network status
            boolean networkShouldAutoDl = NetworkUtils.isAutoDownloadAllowed()
                    && UserPreferences.isEnableAutodownload();

            // true if we should auto download based on power status
            boolean powerShouldAutoDl = PowerUtils.deviceCharging(context)
                    || UserPreferences.isEnableAutodownloadOnBattery();

            // we should only auto download if both network AND power are happy
            if (networkShouldAutoDl && powerShouldAutoDl) {

                Log.d(TAG, "Performing auto-dl of undownloaded episodes");

                List<FeedItem> candidates;
                final List<FeedItem> queue = DBReader.getQueue();
                final List<FeedItem> newItems = DBReader.getEpisodes(0, Integer.MAX_VALUE,
                        new FeedItemFilter(FeedItemFilter.NEW), SortOrder.DATE_NEW_OLD);
                candidates = new ArrayList<>(queue.size() + newItems.size());
                candidates.addAll(queue);
                for (FeedItem newItem : newItems) {
                    FeedPreferences feedPrefs = newItem.getFeed().getPreferences();
                    if (feedPrefs.getAutoDownload()
                            && !candidates.contains(newItem)
                            && feedPrefs.getFilter().shouldAutoDownload(newItem)) {
                        candidates.add(newItem);
                    }
                }

                // filter items that are not auto downloadable
                Iterator<FeedItem> it = candidates.iterator();
                while (it.hasNext()) {
                    FeedItem item = it.next();
                    if (!item.isAutoDownloadable(System.currentTimeMillis())
                            || PlaybackStatus.isPlaying(item.getMedia())
                            || item.getFeed().isLocalFeed()) {
                        it.remove();
                    }
                }

                int autoDownloadableEpisodes = candidates.size();
                int downloadedEpisodes = DBReader.getTotalEpisodeCount(new FeedItemFilter(FeedItemFilter.DOWNLOADED));
                int deletedEpisodes = EpisodeCleanupAlgorithmFactory.build()
                        .makeRoomForEpisodes(context, autoDownloadableEpisodes);
                boolean cacheIsUnlimited =
                        UserPreferences.getEpisodeCacheSize() == UserPreferences.EPISODE_CACHE_SIZE_UNLIMITED;
                int episodeCacheSize = UserPreferences.getEpisodeCacheSize();

                int episodeSpaceLeft;
                if (cacheIsUnlimited || episodeCacheSize >= downloadedEpisodes + autoDownloadableEpisodes) {
                    episodeSpaceLeft = autoDownloadableEpisodes;
                } else {
                    episodeSpaceLeft = episodeCacheSize - (downloadedEpisodes - deletedEpisodes);
                }

                List<FeedItem> itemsToDownload = candidates.subList(0, episodeSpaceLeft);
                if (itemsToDownload.size() > 0) {
                    Log.d(TAG, "Enqueueing " + itemsToDownload.size() + " items for download");

                    for (FeedItem episode : itemsToDownload) {
                        DownloadServiceInterface.get().download(context, episode);
                    }
                }
            }
        };
    }
}
