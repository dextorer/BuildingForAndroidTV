package com.sgottard.tvdemoapp.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.view.View;

import com.sgottard.tvdemoapp.tvleanback.R;

import java.util.List;

/**
 * Created by Sebastiano Gottardo on 17/05/15.
 */
public class SettingsFragment extends GuidedStepFragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance(getString(R.string.settings_title), null, getString(R.string.app_name), null);
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        actions.add(new GuidedAction.Builder()
                .id(R.id.settings_category_id)
                .infoOnly(true)
                .title(getString(R.string.settings_category))
                .build());

        actions.add(new GuidedAction.Builder()
                .id(R.id.settings_toggle_nav_id)
                .title(getString(R.string.settings_toggle_nav_title))
                .checked(!MainActivity.isUsingStandardBrowseFragment())
                .description(getString(R.string.settings_toggle_nav_desc))
                .build());

        super.onCreateActions(actions, savedInstanceState);
    }

    @Override
    public void onGuidedActionClicked(final GuidedAction action) {
        switch ((int) action.getId()) {
            case R.id.settings_toggle_nav_id :
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Use custom navigation")
                        .setMessage("Restart the application?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.PREFS_ROOT, Context.MODE_PRIVATE);
                                prefs
                                    .edit()
                                    .putBoolean(MainActivity.PREFS_USE_STANDARD_BROWSE_FRAGMENT, action.isChecked())
                                    .apply();
                                Intent i = new Intent(getActivity(), MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            default :
                break;
        }

        super.onGuidedActionClicked(action);
    }
}
