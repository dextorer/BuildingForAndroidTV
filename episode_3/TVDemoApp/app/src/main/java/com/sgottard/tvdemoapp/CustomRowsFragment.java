package com.sgottard.tvdemoapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v17.leanback.app.RowsFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Sebastiano Gottardo on 08/11/14.
 */
public class CustomRowsFragment extends RowsFragment {

	private final int NUM_ROWS = 5;
	private final int NUM_COLS = 15;

	private ArrayObjectAdapter rowsAdapter;
	private CardPresenter cardPresenter;

	// CustomHeadersFragment, scaled by 0.9 on a 1080p screen, is 600px wide.
	// This is the corresponding dip size.
	private static final int HEADERS_FRAGMENT_SCALE_SIZE = 300;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);

		int marginOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HEADERS_FRAGMENT_SCALE_SIZE, getResources().getDisplayMetrics());
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
		params.rightMargin -= marginOffset;
		v.setLayoutParams(params);

		v.setBackgroundColor(getRandomColor());
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		loadRows();

		setCustomPadding();
	}

	private void loadRows() {
		rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
		cardPresenter = new CardPresenter();

		List<Movie> list = MovieList.setupMovies();

		int i;
		for (i = 0; i < NUM_ROWS; i++) {
			if (i != 0) {
				Collections.shuffle(list);
			}
			ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
			for (int j = 0; j < NUM_COLS; j++) {
				listRowAdapter.add(list.get(j % 5));
			}
			HeaderItem header = new HeaderItem(i, MovieList.MOVIE_CATEGORY[i], null);
			rowsAdapter.add(new ListRow(header, listRowAdapter));
		}

		setAdapter(rowsAdapter);
	}

	private void setCustomPadding() {
		getView().setPadding(Utils.dpToPx(-24, getActivity()), Utils.dpToPx(128, getActivity()), Utils.dpToPx(48, getActivity()), 0);
	}

	private int getRandomColor() {
		Random rnd = new Random();
		return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
	}

	public void refresh() {
		getView().setPadding(Utils.dpToPx(-24, getActivity()), Utils.dpToPx(128, getActivity()), Utils.dpToPx(300, getActivity()), 0);
	}
}
