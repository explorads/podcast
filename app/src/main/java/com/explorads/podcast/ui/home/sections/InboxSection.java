package com.explorads.podcast.ui.home.sections;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.explorads.podcast.R;
import com.explorads.podcast.adapter.EpisodeItemListAdapter;
import com.explorads.podcast.core.storage.DBReader;

import com.explorads.podcast.activity.MainActivity;
import com.explorads.podcast.core.menuhandler.MenuItemUtils;
import com.explorads.podcast.core.util.FeedItemUtil;
import com.explorads.podcast.event.EpisodeDownloadEvent;
import com.explorads.podcast.event.FeedItemEvent;
import com.explorads.podcast.event.FeedListUpdateEvent;
import com.explorads.podcast.event.UnreadItemsUpdateEvent;
import com.explorads.podcast.fragment.InboxFragment;
import com.explorads.podcast.fragment.swipeactions.SwipeActions;
import com.explorads.podcast.model.feed.FeedItem;
import com.explorads.podcast.model.feed.FeedItemFilter;
import com.explorads.podcast.storage.preferences.UserPreferences;
import com.explorads.podcast.ui.home.HomeSection;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InboxSection extends HomeSection {
    public static final String TAG = "InboxSection";
    private static final int NUM_EPISODES = 2;
    private EpisodeItemListAdapter adapter;
    private List<FeedItem> items = new ArrayList<>();
    private Disposable disposable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        viewBinding.recyclerView.setPadding(0, 0, 0, 0);
        viewBinding.recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        viewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        viewBinding.recyclerView.setRecycledViewPool(((MainActivity) requireActivity()).getRecycledViewPool());
        adapter = new EpisodeItemListAdapter((MainActivity) requireActivity()) {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                super.onCreateContextMenu(menu, v, menuInfo);
                MenuItemUtils.setOnClickListeners(menu, InboxSection.this::onContextItemSelected);
            }
        };
        adapter.setDummyViews(NUM_EPISODES);
        viewBinding.recyclerView.setAdapter(adapter);

        SwipeActions swipeActions = new SwipeActions(this, InboxFragment.TAG);
        swipeActions.attachTo(viewBinding.recyclerView);
        swipeActions.setFilter(new FeedItemFilter(FeedItemFilter.NEW));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadItems();
    }

    @Override
    protected void handleMoreClick() {
        ((MainActivity) requireActivity()).loadChildFragment(new InboxFragment());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnreadItemsChanged(UnreadItemsUpdateEvent event) {
        loadItems();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(FeedItemEvent event) {
        loadItems();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFeedListChanged(FeedListUpdateEvent event) {
        loadItems();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EpisodeDownloadEvent event) {
        for (String downloadUrl : event.getUrls()) {
            int pos = FeedItemUtil.indexOfItemWithDownloadUrl(items, downloadUrl);
            if (pos >= 0) {
                adapter.notifyItemChangedCompat(pos);
            }
        }
    }

    @Override
    protected String getSectionTitle() {
        return getString(R.string.home_new_title);
    }

    @Override
    protected String getMoreLinkTitle() {
        return getString(R.string.inbox_label);
    }

    private void loadItems() {
        if (disposable != null) {
            disposable.dispose();
        }
        disposable = Observable.fromCallable(() ->
                        new Pair<>(DBReader.getEpisodes(0, NUM_EPISODES,
                                new FeedItemFilter(FeedItemFilter.NEW), UserPreferences.getInboxSortedOrder()),
                                DBReader.getTotalEpisodeCount(new FeedItemFilter(FeedItemFilter.NEW))))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    items = data.first;
                    adapter.setDummyViews(0);
                    adapter.updateItems(items);
                    viewBinding.numNewItemsLabel.setVisibility(View.VISIBLE);
                    if (data.second >= 100) {
                        viewBinding.numNewItemsLabel.setText(String.format(Locale.getDefault(), "%d+", 99));
                    } else {
                        viewBinding.numNewItemsLabel.setText(String.format(Locale.getDefault(), "%d", data.second));
                    }
                }, error -> Log.e(TAG, Log.getStackTraceString(error)));
    }
}
