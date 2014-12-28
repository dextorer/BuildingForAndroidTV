package com.sgottard.tvdemoapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.SearchOrbView;
import android.support.v17.leanback.widget.VerticalGridView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

public class TVDemoActivity extends Activity {

	private SearchOrbView orbView;

	private CustomHeadersFragment headersFragment;
	private CustomRowsFragment rowsFragment;

	private final int CATEGORIES_NUMBER = 5;
	private LinkedHashMap<Integer, CustomRowsFragment> fragments;
	private CustomFrameLayout customFrameLayout;

	private boolean navigationDrawerOpen;
	private static final float NAVIGATION_DRAWER_SCALE_FACTOR = 0.9f;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

		orbView = (SearchOrbView) findViewById(R.id.custom_search_orb);
		orbView.setOrbColor(getResources().getColor(R.color.search_opaque));
		orbView.bringToFront();
		orbView.setOnOrbClickedListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), TVSearchActivity.class);
				startActivity(intent);
			}
		});

		fragments = new LinkedHashMap<Integer, CustomRowsFragment>();

		for (int i = 0; i < CATEGORIES_NUMBER; i++) {
			CustomRowsFragment fragment = new CustomRowsFragment();
			fragments.put(i, fragment);
		}

		headersFragment = new CustomHeadersFragment();
		rowsFragment = fragments.get(0);

		customFrameLayout = (CustomFrameLayout) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
		setupCustomFrameLayout();

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction
			.replace(R.id.header_container, headersFragment, "CustomHeadersFragment")
			.replace(R.id.rows_container, rowsFragment, "CustomRowsFragment");
		transaction.commit();
	}

	public LinkedHashMap<Integer, CustomRowsFragment> getFragments() {
		return fragments;
	}

	private void setupCustomFrameLayout() {
		customFrameLayout.setOnChildFocusListener(new CustomFrameLayout.OnChildFocusListener() {
			@Override
			public boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
				if (headersFragment.getView() != null && headersFragment.getView().requestFocus(direction, previouslyFocusedRect)) {
					return true;
				}
				if (rowsFragment.getView() != null && rowsFragment.getView().requestFocus(direction, previouslyFocusedRect)) {
					return true;
				}
				return false;
			}

			@Override
			public void onRequestChildFocus(View child, View focused) {
				int childId = child.getId();
				if (childId == R.id.rows_container) {
					toggleHeadersFragment(false);
				} else if (childId == R.id.header_container) {
					toggleHeadersFragment(true);
				}
			}
		});

		customFrameLayout.setOnFocusSearchListener(new CustomFrameLayout.OnFocusSearchListener() {
			@Override
			public View onFocusSearch(View focused, int direction) {
				if (direction == View.FOCUS_LEFT) {
					if (isVerticalScrolling() || navigationDrawerOpen) {
						return focused;
					}
					return getVerticalGridView(headersFragment);
				} else if (direction == View.FOCUS_RIGHT) {
					if (isVerticalScrolling() || !navigationDrawerOpen) {
						return focused;
					}
					return getVerticalGridView(rowsFragment);
				} else if (focused == orbView && direction == View.FOCUS_DOWN) {
					return navigationDrawerOpen ? getVerticalGridView(headersFragment) : getVerticalGridView(rowsFragment);
				} else if (focused != orbView && orbView.getVisibility() == View.VISIBLE && direction == View.FOCUS_UP) {
					return orbView;
				} else {
					return null;
				}
			}
		});
	}

	public synchronized void toggleHeadersFragment(final boolean doOpen) {
		boolean condition = (doOpen ? !isNavigationDrawerOpen() : isNavigationDrawerOpen());
		if (condition) {
			final View headersContainer = (View) headersFragment.getView().getParent();
			final View rowsContainer = (View) rowsFragment.getView().getParent();

			final float delta = headersContainer.getWidth() * NAVIGATION_DRAWER_SCALE_FACTOR;

			// get current margin (a previous animation might have been interrupted)
			final int currentHeadersMargin = (((ViewGroup.MarginLayoutParams) headersContainer.getLayoutParams()).leftMargin);
			final int currentRowsMargin = (((ViewGroup.MarginLayoutParams) rowsContainer.getLayoutParams()).leftMargin);

			// calculate destination
			final int headersDestination = (doOpen ? 0 : (int) (0 - delta));
			final int rowsDestination = (doOpen ? (Utils.dpToPx(300, this)) : (int) (Utils.dpToPx(300, this) - delta));

			// calculate the delta (destination - current)
			final int headersDelta = headersDestination - currentHeadersMargin;
			final int rowsDelta = rowsDestination - currentRowsMargin;

			Animation animation = new Animation() {
				@Override
				protected void applyTransformation(float interpolatedTime, Transformation t) {
					ViewGroup.MarginLayoutParams headersParams = (ViewGroup.MarginLayoutParams) headersContainer.getLayoutParams();
					headersParams.leftMargin = (int) (currentHeadersMargin + headersDelta * interpolatedTime);
					headersContainer.setLayoutParams(headersParams);

					ViewGroup.MarginLayoutParams rowsParams = (ViewGroup.MarginLayoutParams) rowsContainer.getLayoutParams();
					rowsParams.leftMargin = (int) (currentRowsMargin + rowsDelta * interpolatedTime);
					rowsContainer.setLayoutParams(rowsParams);
				}
			};

			animation.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					navigationDrawerOpen = doOpen;
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					if (!doOpen) {
						rowsFragment.refresh();
					}
				}

				@Override
				public void onAnimationRepeat(Animation animation) {}

			});

			animation.setDuration(200);
			((View) rowsContainer.getParent()).startAnimation(animation);
		}
	}

	private boolean isVerticalScrolling() {
		try {
			// don't run transition
			return getVerticalGridView(headersFragment).getScrollState()
					!= HorizontalGridView.SCROLL_STATE_IDLE
					|| getVerticalGridView(rowsFragment).getScrollState()
					!= HorizontalGridView.SCROLL_STATE_IDLE;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public VerticalGridView getVerticalGridView(Fragment fragment) {
		try {
			Class baseRowFragmentClass = getClassLoader().loadClass("android/support/v17/leanback/app/BaseRowFragment");
			Method getVerticalGridViewMethod = baseRowFragmentClass.getDeclaredMethod("getVerticalGridView", null);
			getVerticalGridViewMethod.setAccessible(true);
			VerticalGridView gridView = (VerticalGridView) getVerticalGridViewMethod.invoke(fragment, null);

			return gridView;

		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	public synchronized boolean isNavigationDrawerOpen() {
		return navigationDrawerOpen;
	}

	public void updateCurrentRowsFragment(CustomRowsFragment fragment) {
		rowsFragment = fragment;
	}

}
