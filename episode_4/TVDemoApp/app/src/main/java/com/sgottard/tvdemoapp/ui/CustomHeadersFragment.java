package com.sgottard.tvdemoapp.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v17.leanback.app.HeadersFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.OnChildSelectedListener;
import android.support.v17.leanback.widget.VerticalGridView;
import android.view.View;
import android.view.ViewGroup;

import com.sgottard.tvdemoapp.Utils;
import com.sgottard.tvdemoapp.tvleanback.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

/**
 * Created by Sebastiano Gottardo on 08/11/14.
 */
public class CustomHeadersFragment extends android.support.v17.leanback.app.HeadersFragment {

	private ArrayObjectAdapter adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		customSetBackground(R.color.fastlane_background);
		setHeaderAdapter();

		setCustomPadding();

		/**
		 * The setOnItemSelectedListener has been not only deprecated, but brutally removed by
		 * Google. To get around this limitation, I went to see how BaseRowFragment handled it.
		 * Turns out it sets a listener to the GridView (which is a RecyclerView): there you go.
		 */
		VerticalGridView gridView = ((MainActivity) getActivity()).getVerticalGridView(this);
		gridView.setOnChildSelectedListener(new OnChildSelectedListener() {
			@Override
			public void onChildSelected(ViewGroup viewGroup, View view, int i, long l) {
				Object obj = ((ListRow) getAdapter().get(i)).getAdapter().get(0);
				getFragmentManager().beginTransaction().replace(R.id.rows_container, (Fragment) obj).commit();
				((MainActivity) getActivity()).updateCurrentFragment((Fragment) obj);
			}
		});

	}

	private void setHeaderAdapter() {
		adapter = new ArrayObjectAdapter();

		LinkedHashMap<Integer, Fragment> fragments = ((MainActivity) getActivity()).getFragments();

		int id = 0;
		for (int i = 0; i < fragments.size(); i++) {
			HeaderItem header = new HeaderItem(id, "Category " + i);
			ArrayObjectAdapter innerAdapter = new ArrayObjectAdapter();
			innerAdapter.add(fragments.get(i));
			adapter.add(id, new ListRow(header, innerAdapter));
			id++;
		}

		setAdapter(adapter);
	}

	private void setCustomPadding() {
		getView().setPadding(0, Utils.convertDpToPixel(getActivity(), 128), Utils.convertDpToPixel(getActivity(), 48), 0);
	}

//	private OnItemSelectedListener getDefaultItemSelectedListener() {
//		return new OnItemSelectedListener() {
//			@Override
//			public void onItemSelected(Object o, Row row) {
//				Object obj = ((ListRow) row).getAdapter().get(0);
//				getFragmentManager().beginTransaction().replace(R.id.rows_container, (Fragment) obj).commit();
//				((MainActivity) getActivity()).updateCurrentFragment((Fragment) obj);
//			}
//		};
//	}

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
