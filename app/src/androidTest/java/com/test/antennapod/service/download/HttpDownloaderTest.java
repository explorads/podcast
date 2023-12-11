package com.test.antennapod.service.download;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.filters.LargeTest;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import com.explorads.podcast.model.feed.FeedFile;
import com.explorads.podcast.storage.preferences.UserPreferences;
import com.explorads.podcast.net.download.serviceinterface.DownloadRequest;
import com.explorads.podcast.model.download.DownloadResult;
import com.explorads.podcast.core.service.download.Downloader;
import com.explorads.podcast.core.service.download.HttpDownloader;
import com.explorads.podcast.model.download.DownloadError;
import com.test.antennapod.util.service.download.HTTPBin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@LargeTest
public class HttpDownloaderTest {
    private static final String TAG = "HttpDownloaderTest";
    private static final String DOWNLOAD_DIR = "testdownloads";

    private String url404;
    private String urlAuth;
    private File destDir;
    private HTTPBin httpServer;

    public HttpDownloaderTest() {
        super();
    }

    @After
    public void tearDown() throws Exception {
        File[] contents = destDir.listFiles();
        for (File f : contents) {
            assertTrue(f.delete());
        }

        httpServer.stop();
    }

    @Before
    public void setUp() throws Exception {
        UserPreferences.init(InstrumentationRegistry.getInstrumentation().getTargetContext());
        destDir = InstrumentationRegistry.getInstrumentation().getTargetContext().getExternalFilesDir(DOWNLOAD_DIR);
        assertNotNull(destDir);
        assertTrue(destDir.exists());
        httpServer = new HTTPBin();
        httpServer.start();
        url404 = httpServer.getBaseUrl() + "/status/404";
        urlAuth = httpServer.getBaseUrl() + "/basic-auth/user/passwd";
    }

    private FeedFileImpl setupFeedFile(String downloadUrl, String title, boolean deleteExisting) {
        FeedFileImpl feedfile = new FeedFileImpl(downloadUrl);
        String fileUrl = new File(destDir, title).getAbsolutePath();
        File file = new File(fileUrl);
        if (deleteExisting) {
            Log.d(TAG, "Deleting file: " + file.delete());
        }
        feedfile.setFile_url(fileUrl);
        return feedfile;
    }

    private Downloader download(String url, String title, boolean expectedResult) {
        return download(url, title, expectedResult, true, null, null);
    }

    private Downloader download(String url, String title, boolean expectedResult, boolean deleteExisting,
                                String username, String password) {
        FeedFile feedFile = setupFeedFile(url, title, deleteExisting);
        DownloadRequest request = new DownloadRequest(feedFile.getFile_url(), url, title, 0, feedFile.getTypeAsInt(),
                username, password, null, false);
        Downloader downloader = new HttpDownloader(request);
        downloader.call();
        DownloadResult status = downloader.getResult();
        assertNotNull(status);
        assertEquals(expectedResult, status.isSuccessful());
        // the file should not exist if the download has failed and deleteExisting was true
        assertTrue(!deleteExisting || new File(feedFile.getFile_url()).exists() == expectedResult);
        return downloader;
    }

    @Test
    public void testPassingHttp() {
        download(httpServer.getBaseUrl() + "/status/200", "test200", true);
    }

    @Test
    public void testRedirect() {
        download(httpServer.getBaseUrl() + "/redirect/4", "testRedirect", true);
    }

    @Test
    public void testGzip() {
        download(httpServer.getBaseUrl() + "/gzip/100", "testGzip", true);
    }

    @Test
    public void test404() {
        download(url404, "test404", false);
    }

    @Test
    public void testCancel() {
        final String url = httpServer.getBaseUrl() + "/delay/3";
        FeedFileImpl feedFile = setupFeedFile(url, "delay", true);
        final Downloader downloader = new HttpDownloader(new DownloadRequest(feedFile.getFile_url(), url, "delay", 0,
                feedFile.getTypeAsInt(), null, null, null, false));
        Thread t = new Thread() {
            @Override
            public void run() {
                downloader.call();
            }
        };
        t.start();
        downloader.cancel();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DownloadResult result = downloader.getResult();
        assertFalse(result.isSuccessful());
    }

    @Test
    public void testDeleteOnFailShouldDelete() {
        Downloader downloader = download(url404, "testDeleteOnFailShouldDelete", false, true, null, null);
        assertFalse(new File(downloader.getDownloadRequest().getDestination()).exists());
    }

    @Test
    public void testDeleteOnFailShouldNotDelete() throws IOException {
        String filename = "testDeleteOnFailShouldDelete";
        File dest = new File(destDir, filename);
        dest.delete();
        assertTrue(dest.createNewFile());
        Downloader downloader = download(url404, filename, false, false, null, null);
        assertTrue(new File(downloader.getDownloadRequest().getDestination()).exists());
    }

    @Test
    public void testAuthenticationShouldSucceed() throws InterruptedException {
        download(urlAuth, "testAuthSuccess", true, true, "user", "passwd");
    }

    @Test
    public void testAuthenticationShouldFail() {
        Downloader downloader = download(urlAuth, "testAuthSuccess", false, true, "user", "Wrong passwd");
        assertEquals(DownloadError.ERROR_UNAUTHORIZED, downloader.getResult().getReason());
    }

    /* TODO: replace with smaller test file
    public void testUrlWithSpaces() {
        download("http://acedl.noxsolutions.com/ace/Don't Call Salman Rushdie Sneezy in Finland.mp3", "testUrlWithSpaces", true);
    }
    */

    private static class FeedFileImpl extends FeedFile {
        public FeedFileImpl(String download_url) {
            super(null, download_url, false);
        }


        @Override
        public String getHumanReadableIdentifier() {
            return download_url;
        }

        @Override
        public int getTypeAsInt() {
            return 0;
        }
    }

}
