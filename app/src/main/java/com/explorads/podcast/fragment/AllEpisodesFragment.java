package com.explorads.podcast.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

import com.explorads.podcast.R;
import com.explorads.podcast.core.storage.DBReader;
import com.explorads.podcast.dialog.AllEpisodesFilterDialog;
import com.explorads.podcast.model.feed.FeedItem;
import com.explorads.podcast.model.feed.FeedItemFilter;
import com.explorads.podcast.model.feed.SortOrder;


import com.explorads.podcast.storage.preferences.UserPreferences;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Shows all episodes (possibly filtered by user).
 */
public class AllEpisodesFragment extends EpisodesListFragment {
    public static final String TAG = "EpisodesFragment";
    public static final String PREF_NAME = "PrefAllEpisodesFragment";

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = super.onCreateView(inflater, container, savedInstanceState);
        toolbar.inflateMenu(R.menu.episodes);
        inflateSortMenu();
        toolbar.setTitle(R.string.episodes_label);
        updateToolbar();
        updateFilterUi();
        txtvInformation.setOnClickListener(
                v -> AllEpisodesFilterDialog.newInstance(getFilter()).show(getChildFragmentManager(), null));
        return root;
    }

    private void inflateSortMenu() {
        MenuItem sortItem = toolbar.getMenu().findItem(R.id.episodes_sort);
        getActivity().getMenuInflater().inflate(R.menu.sort_menu, sortItem.getSubMenu());

        // Remove the sorting options that are not needed in this fragment
        toolbar.getMenu().findItem(R.id.sort_episode_title).setVisible(false);
        toolbar.getMenu().findItem(R.id.sort_feed_title).setVisible(false);
        toolbar.getMenu().findItem(R.id.sort_random).setVisible(false);
        toolbar.getMenu().findItem(R.id.sort_smart_shuffle).setVisible(false);
        toolbar.getMenu().findItem(R.id.keep_sorted).setVisible(false);
    }

    @NonNull
    @Override
    protected List<FeedItem> loadData() {
        return DBReader.getEpisodes(0, page * EPISODES_PER_PAGE, getFilter(),
                UserPreferences.getAllEpisodesSortOrder());
    }

    @NonNull
    @Override
    protected List<FeedItem> loadMoreData(int page) {
        return DBReader.getEpisodes((page - 1) * EPISODES_PER_PAGE, EPISODES_PER_PAGE, getFilter(),
                UserPreferences.getAllEpisodesSortOrder());
    }

    @Override
    protected int loadTotalItemCount() {
        return DBReader.getTotalEpisodeCount(getFilter());
    }

    @Override
    protected FeedItemFilter getFilter() {
        return new FeedItemFilter(UserPreferences.getPrefFilterAllEpisodes());
    }

    @Override
    protected String getFragmentTag() {
        return TAG;
    }

    @Override
    protected String getPrefName() {
        return PREF_NAME;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.filter_items) {
            AllEpisodesFilterDialog.newInstance(getFilter()).show(getChildFragmentManager(), null);
            return true;
        } else if (item.getItemId() == R.id.action_favorites) {
            ArrayList<String> filter = new ArrayList<>(getFilter().getValuesList());
            if (filter.contains(FeedItemFilter.IS_FAVORITE)) {
                filter.remove(FeedItemFilter.IS_FAVORITE);
            } else {
                filter.add(FeedItemFilter.IS_FAVORITE);
            }
            onFilterChanged(new AllEpisodesFilterDialog.AllEpisodesFilterChangedEvent(new HashSet<>(filter)));
            return true;
        } else {
            SortOrder sortOrder = MenuItemToSortOrderConverter.convert(item);
            if (sortOrder != null) {
                saveSortOrderAndRefresh(sortOrder);
                return true;
            }
        }
        return false;
    }

    private void saveSortOrderAndRefresh(SortOrder type) {
        UserPreferences.setAllEpisodesSortOrder(type);
        loadItems();
    }

    @Subscribe
    public void onFilterChanged(AllEpisodesFilterDialog.AllEpisodesFilterChangedEvent event) {
        UserPreferences.setPrefFilterAllEpisodes(StringUtils.join(event.filterValues, ","));
        updateFilterUi();
        page = 1;
        loadItems();
    }

    private void updateFilterUi() {
        swipeActions.setFilter(getFilter());
        if (getFilter().getValues().length > 0) {
            txtvInformation.setVisibility(View.VISIBLE);
            emptyView.setMessage(R.string.no_all_episodes_filtered_label);
        } else {
            txtvInformation.setVisibility(View.GONE);
            emptyView.setMessage(R.string.no_all_episodes_label);
        }
        toolbar.getMenu().findItem(R.id.action_favorites).setIcon(
                getFilter().showIsFavorite ? R.drawable.ic_star : R.drawable.ic_star_border);
    }
}
