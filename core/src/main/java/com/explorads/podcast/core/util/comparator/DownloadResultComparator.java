package com.explorads.podcast.core.util.comparator;

import java.util.Comparator;

import com.explorads.podcast.model.download.DownloadResult;

/** Compares the completion date of two DownloadResult objects. */
public class DownloadResultComparator implements Comparator<DownloadResult> {

    @Override
    public int compare(DownloadResult lhs, DownloadResult rhs) {
        return rhs.getCompletionDate().compareTo(lhs.getCompletionDate());
    }
}
