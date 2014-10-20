package com.sgottard.tvdemoapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.SearchFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnItemClickedListener;
import android.support.v17.leanback.widget.Row;
import android.text.TextUtils;

/**
 * Created by Sebastiano Gottardo on 19/10/14.
 */
public class TVSearchFragment extends SearchFragment implements SearchFragment.SearchResultProvider {

	private static final int SEARCH_DELAY_MS = 300;
	private ArrayObjectAdapter mRowsAdapter;
	private Handler mHandler = new Handler();
	private SearchRunnable mDelayedLoad;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
		setSearchResultProvider(this);
		setOnItemClickedListener(getDefaultItemClickedListener());
		mDelayedLoad = new SearchRunnable();
	}

	@Override
	public ObjectAdapter getResultsAdapter() {
		return mRowsAdapter;
	}

	@Override
	public boolean onQueryTextChange(String newQuery) {
		mRowsAdapter.clear();
		if (!TextUtils.isEmpty(newQuery)) {
			mDelayedLoad.setSearchQuery(newQuery);
			mHandler.removeCallbacks(mDelayedLoad);
			mHandler.postDelayed(mDelayedLoad, SEARCH_DELAY_MS);
		}
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		mRowsAdapter.clear();
		if (!TextUtils.isEmpty(query)) {
			mDelayedLoad.setSearchQuery(query);
			mHandler.removeCallbacks(mDelayedLoad);
			mHandler.postDelayed(mDelayedLoad, SEARCH_DELAY_MS);
		}
		return true;
	}

	private OnItemClickedListener getDefaultItemClickedListener() {
		return new OnItemClickedListener() {
			@Override
			public void onItemClicked(Object o, Row row) {
				Utils.showToast(getActivity(), "Do something");
			}
		};
	}

	private class SearchRunnable implements Runnable {

		// we won't use it
		private String query;

		public void setSearchQuery(String query) {
			this.query = query;
		}

		@Override
		public void run() {
			// you should fetch your data here and update the adapter
			// accordingly
			mRowsAdapter.clear();
			ArrayObjectAdapter adapter = new ArrayObjectAdapter(new CardPresenter());
			adapter.addAll(0, MovieList.list);
			HeaderItem header = new HeaderItem(0, getResources().getString(R.string.search_results), null);
			mRowsAdapter.add(new ListRow(header, adapter));
		}
	}

}
