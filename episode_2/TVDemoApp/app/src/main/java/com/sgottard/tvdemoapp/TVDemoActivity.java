package com.sgottard.tvdemoapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import java.util.LinkedHashMap;

public class TVDemoActivity extends Activity {

	private CustomHeadersFragment headersFragment;
	private CustomRowsFragment rowsFragment;

	private final int CATEGORIES_NUMBER = 5;
	private LinkedHashMap<Integer, CustomRowsFragment> fragments;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

		headersFragment = new CustomHeadersFragment();
		rowsFragment = new CustomRowsFragment();

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction
			.replace(R.id.header_container, headersFragment, "CustomHeadersFragment")
			.replace(R.id.rows_container, rowsFragment, "CustomRowsFragment");
		transaction.commit();

		fragments = new LinkedHashMap<Integer, CustomRowsFragment>();

		for (int i = 0; i < CATEGORIES_NUMBER; i++) {
			CustomRowsFragment fragment = new CustomRowsFragment();
			fragment.setCustomId(i);
			fragments.put(i, fragment);
		}
	}

	public LinkedHashMap<Integer, CustomRowsFragment> getFragments() {
		return fragments;
	}
}
