package com.sgottard.tvdemoapp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.OnItemClickedListener;
import android.support.v17.leanback.widget.OnItemSelectedListener;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.util.DisplayMetrics;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sebastiano Gottardo on 19/10/14.
 */
public class TVGridFragment extends VerticalGridFragment {

	private Drawable mDefaultBackground;
	private Target mBackgroundTarget;
	private DisplayMetrics mMetrics;
	private Timer mBackgroundTimer;
	private final Handler mHandler = new Handler();
	private URI mBackgroundURI;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		VerticalGridPresenter presenter = new VerticalGridPresenter();
		presenter.setNumberOfColumns(3);
		setGridPresenter(presenter);

		loadRows();
	}

	private void loadRows() {
		ArrayObjectAdapter adapter = new ArrayObjectAdapter(new CardPresenter());
		adapter.addAll(0, MovieList.list);
		setAdapter(adapter);
	}

	private void prepareBackgroundManager() {
		BackgroundManager backgroundManager = BackgroundManager.getInstance(getActivity());
		backgroundManager.attach(getActivity().getWindow());
		mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);

		mDefaultBackground = getResources().getDrawable(R.drawable.default_background);

		mMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
	}

	private void setupUIElements() {
		setTitle("TVGridFragment");
	}

	private void startBackgroundTimer() {
		if (null != mBackgroundTimer) {
			mBackgroundTimer.cancel();
		}
		mBackgroundTimer = new Timer();
		mBackgroundTimer.schedule(new UpdateBackgroundTask(), 300);
	}

	protected void updateBackground(Drawable drawable) {
		BackgroundManager.getInstance(getActivity()).setDrawable(drawable);
	}

	protected void clearBackground() {
		BackgroundManager.getInstance(getActivity()).setDrawable(mDefaultBackground);
	}

	protected void updateBackground(URI uri) {
		Picasso.with(getActivity())
				.load(uri.toString())
				.resize(mMetrics.widthPixels, mMetrics.heightPixels)
				.centerInside()
				.error(mDefaultBackground)
				.into(mBackgroundTarget);
	}

	private void setupEventListeners() {
		setOnItemSelectedListener(getDefaultItemSelectedListener());
		setOnItemClickedListener(getDefaultItemClickedListener());
	}

	protected OnItemSelectedListener getDefaultItemSelectedListener() {
		return new OnItemSelectedListener() {
			@Override
			public void onItemSelected(Object item, Row row) {
				// item is selected
			}
		};
	}

	protected OnItemClickedListener getDefaultItemClickedListener() {
		return new OnItemClickedListener() {
			@Override
			public void onItemClicked(Object item, Row row) {
				Utils.showToast(getActivity(), "Item clicked");
			}
		};
	}

	/*****************
	 * PRIVATE CLASSES
	 *****************/

	private class UpdateBackgroundTask extends TimerTask {
		@Override
		public void run() {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (mBackgroundURI != null) {
						updateBackground(mBackgroundURI);
					}
				}
			});
		}
	}
}
