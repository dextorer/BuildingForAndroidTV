package com.sgottard.tvdemoapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.RowsFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sgottard.tvdemoapp.Utils;
import com.sgottard.tvdemoapp.presenter.GridItemPresenter;
import com.sgottard.tvdemoapp.tvleanback.R;

/**
 * Created by Sebastiano Gottardo on 03/05/15.
 */
public class MoreSamplesFragment extends RowsFragment {

    private ArrayObjectAdapter rowsAdapter;

    private static final int HEADERS_FRAGMENT_SCALE_SIZE = 300;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        int marginOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HEADERS_FRAGMENT_SCALE_SIZE, getResources().getDisplayMetrics());
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        params.rightMargin -= marginOffset;
        v.setLayoutParams(params);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadRows();
        setCustomPadding();
        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                if (((String) item).indexOf(getString(R.string.grid_view)) >= 0) {
                    Intent intent = new Intent(getActivity(), VerticalGridActivity.class);
                    getActivity().startActivity(intent);
                } else if (((String) item).indexOf(getString(R.string.error_fragment)) >= 0) {
                    Intent intent = new Intent(getActivity(), BrowseErrorActivity.class);
                    getActivity().startActivity(intent);
                } else if (((String) item)
                        .indexOf(getString(R.string.guidedstep_first_title)) >= 0) {
                    Intent intent = new Intent(getActivity(), GuidedStepActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });
    }

    private void setCustomPadding() {
        getView().setPadding(Utils.convertDpToPixel(getActivity(), -24), Utils.convertDpToPixel(getActivity(), 128), Utils.convertDpToPixel(getActivity(), 48), 0);
    }

    private void loadRows() {
        rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        HeaderItem gridHeader = new HeaderItem(1, getString(R.string.more_samples));
        GridItemPresenter gridPresenter = new GridItemPresenter(this);

        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(gridPresenter);
        gridRowAdapter.add(getString(R.string.grid_view));
        gridRowAdapter.add(getString(R.string.guidedstep_first_title));
        gridRowAdapter.add(getString(R.string.error_fragment));
        gridRowAdapter.add(getString(R.string.personal_settings));

        rowsAdapter.add(new ListRow(gridHeader, gridRowAdapter));

        setAdapter(rowsAdapter);
    }
}
