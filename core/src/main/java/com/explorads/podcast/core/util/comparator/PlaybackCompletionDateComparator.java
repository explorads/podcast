package com.explorads.podcast.core.util.comparator;

import java.util.Comparator;

import com.explorads.podcast.model.feed.FeedItem;

public class PlaybackCompletionDateComparator implements Comparator<FeedItem> {
	
	public int compare(FeedItem lhs, FeedItem rhs) {
		if (lhs.getMedia() != null
				&& lhs.getMedia().getPlaybackCompletionDate() != null
				&& rhs.getMedia() != null
				&& rhs.getMedia().getPlaybackCompletionDate() != null) {
			return rhs.getMedia().getPlaybackCompletionDate()
					.compareTo(lhs.getMedia().getPlaybackCompletionDate());
		}
		return 0;
	}
}
