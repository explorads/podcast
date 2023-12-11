package com.explorads.podcast.core.service.download;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.explorads.podcast.net.download.serviceinterface.DownloadRequest;

public interface DownloaderFactory {
    @Nullable
    Downloader create(@NonNull DownloadRequest request);
}