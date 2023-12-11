package com.explorads.podcast.event;

import com.explorads.podcast.model.download.DownloadStatus;

import java.util.Map;
import java.util.Set;

public class EpisodeDownloadEvent {
    private final Map<String, DownloadStatus> map;

    public EpisodeDownloadEvent(Map<String, DownloadStatus> map) {
        this.map = map;
    }

    public Set<String> getUrls() {
        return map.keySet();
    }
}
