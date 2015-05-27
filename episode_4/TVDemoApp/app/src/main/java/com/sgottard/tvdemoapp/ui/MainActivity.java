/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.sgottard.tvdemoapp.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.SearchOrbView;
import android.support.v17.leanback.widget.VerticalGridView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.sgottard.tvdemoapp.Utils;
import com.sgottard.tvdemoapp.tvleanback.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity {

    /**
     * Change this value to toggle between the stock demo
     * and the custom demo.
     */
    private static boolean useStandardBrowseFragment = false;
    public static final String PREFS_ROOT = "PREFS_ROOT";
    public static final String PREFS_USE_STANDARD_BROWSE_FRAGMENT = PREFS_ROOT + ".EXTRA_USE_STANDARD_BROWSE_FRAGMENT";

    private int standardBrowseFragmentLayoutId = R.layout.main;
    private int customBrowseFragmentLayoutId = R.layout.custom;

    private SearchOrbView orbView;

    private CustomHeadersFragment headersFragment;
    private CustomRowsFragment rowsFragment;
    private MoreSamplesFragment moreSamplesFragment;

    private Fragment currentFragment;

    private LinkedHashMap<Integer, Fragment> fragments;
    private CustomFrameLayout customFrameLayout;

    private boolean navigationDrawerOpen;
    private static final float NAVIGATION_DRAWER_SCALE_FACTOR = 0.9f;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(PREFS_ROOT, Context.MODE_PRIVATE);
        useStandardBrowseFragment = prefs.getBoolean(PREFS_USE_STANDARD_BROWSE_FRAGMENT, false);

        /*
         * This flag discriminates the use of the CustomBrowseFragment widget.
         */
        if (useStandardBrowseFragment) {
            setContentView(standardBrowseFragmentLayoutId);
            return;
        }

        setContentView(customBrowseFragmentLayoutId);

        orbView = (SearchOrbView) findViewById(R.id.custom_search_orb);
        orbView.setOrbColor(getResources().getColor(R.color.search_opaque));
        orbView.bringToFront();
        orbView.setOnOrbClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        rowsFragment = new CustomRowsFragment();
        headersFragment = new CustomHeadersFragment();
        moreSamplesFragment = new MoreSamplesFragment();

        fragments = new LinkedHashMap<Integer, Fragment>();
        fragments.put(0, rowsFragment);
        fragments.put(1, moreSamplesFragment);

        currentFragment = rowsFragment;

        customFrameLayout = (CustomFrameLayout) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        setupCustomFrameLayout();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction
                .replace(R.id.header_container, headersFragment, "CustomHeadersFragment")
                .replace(R.id.rows_container, rowsFragment, "CustomRowsFragment");
        transaction.commit();
    }

    public LinkedHashMap<Integer, Fragment> getFragments() {
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
            final View rowsContainer = (View) currentFragment.getView().getParent();

            final float delta = headersContainer.getWidth() * NAVIGATION_DRAWER_SCALE_FACTOR;

            // get current margin (a previous animation might have been interrupted)
            final int currentHeadersMargin = (((ViewGroup.MarginLayoutParams) headersContainer.getLayoutParams()).leftMargin);
            final int currentRowsMargin = (((ViewGroup.MarginLayoutParams) rowsContainer.getLayoutParams()).leftMargin);

            // calculate destination
            final int headersDestination = (doOpen ? 0 : (int) (0 - delta));
            final int rowsDestination = (doOpen ? (Utils.convertDpToPixel(this, 300)) : (int) (Utils.convertDpToPixel(this, 300) - delta));

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
                    if (!doOpen && currentFragment instanceof CustomRowsFragment) {
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

    public void updateCurrentFragment(Fragment fragment) {
        currentFragment = fragment;
    }

    public static boolean isUsingStandardBrowseFragment() {
        return useStandardBrowseFragment;
    }

    @Override
    public boolean onSearchRequested() {
        startActivity(new Intent(this, SearchActivity.class));
        return true;
    }
}
