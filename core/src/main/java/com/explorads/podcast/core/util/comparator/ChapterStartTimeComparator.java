package com.explorads.podcast.core.util.comparator;

import java.util.Comparator;

import com.explorads.podcast.model.feed.Chapter;

public class ChapterStartTimeComparator implements Comparator<Chapter> {

	@Override
	public int compare(Chapter lhs, Chapter rhs) {
		return Long.compare(lhs.getStart(), rhs.getStart());
	}

}
