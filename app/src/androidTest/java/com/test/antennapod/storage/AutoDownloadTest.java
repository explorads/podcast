package com.test.antennapod.storage;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import com.explorads.podcast.model.feed.FeedItem;
import com.explorads.podcast.model.feed.FeedMedia;
import com.explorads.podcast.core.preferences.PlaybackPreferences;
import com.explorads.podcast.storage.preferences.UserPreferences;
import com.explorads.podcast.core.storage.AutomaticDownloadAlgorithm;
import com.explorads.podcast.core.storage.DBReader;
import com.explorads.podcast.core.storage.DBTasks;
import com.explorads.podcast.core.util.playback.PlaybackServiceStarter;
import com.test.antennapod.EspressoTestUtils;
import com.test.antennapod.ui.UITestUtils;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AutoDownloadTest {

    private Context context;
    private UITestUtils stubFeedsServer;
    private StubDownloadAlgorithm stubDownloadAlgorithm;

    @Before
    public void setUp() throws Exception {
        context = ApplicationProvider.getApplicationContext();

        stubFeedsServer = new UITestUtils(context);
        stubFeedsServer.setup();

        EspressoTestUtils.clearPreferences();
        EspressoTestUtils.clearDatabase();
        UserPreferences.setAllowMobileStreaming(true);

        // Setup: enable automatic download
        // it is not needed, as the actual automatic download is stubbed.
        stubDownloadAlgorithm = new StubDownloadAlgorithm();
        DBTasks.setDownloadAlgorithm(stubDownloadAlgorithm);
    }

    @After
    public void tearDown() throws Exception {
        DBTasks.setDownloadAlgorithm(new AutomaticDownloadAlgorithm());
        EspressoTestUtils.tryKillPlaybackService();
        stubFeedsServer.tearDown();
    }

    /**
     * A cross-functional test, ensuring playback's behavior works with Auto Download in boundary condition.
     *
     * Scenario:
     * - For setting enqueue location AFTER_CURRENTLY_PLAYING
     * - when playback of an episode is complete and the app advances to the next episode (continuous playback on)
     * - when automatic download kicks in,
     * - ensure the next episode is the current playing one, needed for AFTER_CURRENTLY_PLAYING enqueue location.
     */
    @Test
    public void downloadsEnqueuedToAfterCurrent_CurrentAdvancedToNextOnPlaybackComplete() throws Exception {
        UserPreferences.setFollowQueue(true); // continuous playback

        // Setup: feeds and queue
        // downloads 3 of them, leave some in new state (auto-downloadable)
        stubFeedsServer.addLocalFeedData(false);
        List<FeedItem> queue = DBReader.getQueue();
        assertTrue(queue.size() > 1);
        FeedItem item0 = queue.get(0);
        FeedItem item1 = queue.get(1);

        // Actual test
        // Play the first one in the queue
        playEpisode(item0);

        try {
            // when playback is complete, advances to the next one, and auto download kicks in,
            // ensure that currently playing has been advanced to the next one by this point.
            Awaitility.await("advanced to the next episode")
                    .atMost(6000, MILLISECONDS) // the test mp3 media is 3-second long. twice should be enough
                    .until(() -> item1.getMedia().getId() == stubDownloadAlgorithm.getCurrentlyPlayingAtDownload());
        } catch (ConditionTimeoutException cte) {
            long actual = stubDownloadAlgorithm.getCurrentlyPlayingAtDownload();
            fail("when auto download is triggered, the next episode should be playing: ("
                    + item1.getId() + ", " + item1.getTitle() + ") . "
                    + "Actual playing: (" + actual + ")"
            );
        }
    }

    private void playEpisode(@NonNull FeedItem item) {
        FeedMedia media = item.getMedia();
        new PlaybackServiceStarter(context, media)
                .callEvenIfRunning(true)
                .start();
        Awaitility.await("episode is playing")
                .atMost(2000, MILLISECONDS)
                .until(() -> item.getMedia().getId() == PlaybackPreferences.getCurrentlyPlayingFeedMediaId());
    }

    private static class StubDownloadAlgorithm extends AutomaticDownloadAlgorithm {
        private long currentlyPlaying = -1;

        @Override
        public Runnable autoDownloadUndownloadedItems(Context context) {
            return () -> {
                if (currentlyPlaying == -1) {
                    currentlyPlaying = PlaybackPreferences.getCurrentlyPlayingFeedMediaId();
                } else {
                    throw new AssertionError("Stub automatic download should be invoked once and only once");
                }
            };
        }

        long getCurrentlyPlayingAtDownload() {
            return currentlyPlaying;
        }
    }
}
