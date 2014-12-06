package com.sgottard.tvdemoapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v17.leanback.app.HeadersFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.OnItemSelectedListener;
import android.support.v17.leanback.widget.Row;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

/**
 * Created by Sebastiano Gottardo on 08/11/14.
 */
public class CustomHeadersFragment extends HeadersFragment {

	private ArrayObjectAdapter adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		customSetBackground(R.color.fastlane_background);
		setOnItemSelectedListener(getDefaultItemSelectedListener());
		setHeaderAdapter();
	}

	private void setHeaderAdapter() {
		adapter = new ArrayObjectAdapter();

		LinkedHashMap<Integer, CustomRowsFragment> fragments = ((TVDemoActivity) getActivity()).getFragments();

		int id = 0;
		for (int i = 0; i < fragments.size(); i++) {
			HeaderItem header = new HeaderItem(id, "Category " + i, null);
			ArrayObjectAdapter innerAdapter = new ArrayObjectAdapter();
			innerAdapter.add(fragments.get(i));
			adapter.add(id, new ListRow(header, innerAdapter));
			id++;
		}

		setAdapter(adapter);
	}

	private OnItemSelectedListener getDefaultItemSelectedListener() {
		return new OnItemSelectedListener() {
			@Override
			public void onItemSelected(Object o, Row row) {
				Object obj = ((ListRow) row).getAdapter().get(0);
				getFragmentManager().beginTransaction().replace(R.id.rows_container, (Fragment) obj).commit();
			}
		};
	}

	/**
	 * Since the original setBackgroundColor is private, we need to
	 * access it via reflection
	 *
	 * @param colorResource The colour resource
	 */
	private void customSetBackground(int colorResource) {
		try {
			Class clazz = HeadersFragment.class;
			Method m = clazz.getDeclaredMethod("setBackgroundColor", Integer.TYPE);
			m.setAccessible(true);
			m.invoke(this, getResources().getColor(colorResource));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
