package com.explorads.podcast.fragment.preferences;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

import com.explorads.podcast.R;
import com.explorads.podcast.dialog.SwipeActionsDialog;

import com.explorads.podcast.activity.PreferenceActivity;
import com.explorads.podcast.fragment.AllEpisodesFragment;
import com.explorads.podcast.fragment.CompletedDownloadsFragment;
import com.explorads.podcast.fragment.FeedItemlistFragment;
import com.explorads.podcast.fragment.InboxFragment;
import com.explorads.podcast.fragment.PlaybackHistoryFragment;
import com.explorads.podcast.fragment.QueueFragment;

public class SwipePreferencesFragment extends PreferenceFragmentCompat {
    private static final String PREF_SWIPE_QUEUE = "prefSwipeQueue";
    private static final String PREF_SWIPE_INBOX = "prefSwipeInbox";
    private static final String PREF_SWIPE_EPISODES = "prefSwipeEpisodes";
    private static final String PREF_SWIPE_DOWNLOADS = "prefSwipeDownloads";
    private static final String PREF_SWIPE_FEED = "prefSwipeFeed";
    private static final String PREF_SWIPE_HISTORY = "prefSwipeHistory";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences_swipe);

        findPreference(PREF_SWIPE_QUEUE).setOnPreferenceClickListener(preference -> {
            new SwipeActionsDialog(requireContext(), QueueFragment.TAG).show(() -> { });
            return true;
        });
        findPreference(PREF_SWIPE_INBOX).setOnPreferenceClickListener(preference -> {
            new SwipeActionsDialog(requireContext(), InboxFragment.TAG).show(() -> { });
            return true;
        });
        findPreference(PREF_SWIPE_EPISODES).setOnPreferenceClickListener(preference -> {
            new SwipeActionsDialog(requireContext(), AllEpisodesFragment.TAG).show(() -> { });
            return true;
        });
        findPreference(PREF_SWIPE_DOWNLOADS).setOnPreferenceClickListener(preference -> {
            new SwipeActionsDialog(requireContext(), CompletedDownloadsFragment.TAG).show(() -> { });
            return true;
        });
        findPreference(PREF_SWIPE_FEED).setOnPreferenceClickListener(preference -> {
            new SwipeActionsDialog(requireContext(), FeedItemlistFragment.TAG).show(() -> { });
            return true;
        });
        findPreference(PREF_SWIPE_HISTORY).setOnPreferenceClickListener(preference -> {
            new SwipeActionsDialog(requireContext(), PlaybackHistoryFragment.TAG).show(() -> { });
            return true;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ((PreferenceActivity) getActivity()).getSupportActionBar().setTitle(R.string.swipeactions_label);
    }

}
