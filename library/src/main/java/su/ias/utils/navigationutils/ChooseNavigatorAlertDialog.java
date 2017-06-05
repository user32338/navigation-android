package su.ias.utils.navigationutils;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created on 6/5/17.
 * Alert dialog to choose naviagtor program
 */

public class ChooseNavigatorAlertDialog extends DialogFragment {

    private static final String BUILDER = "";

    private CheckBox checkBox = null;
    private Builder builder;

    static ChooseNavigatorAlertDialog getAlertDialog(Builder builder) {
        ChooseNavigatorAlertDialog alertDialog = new ChooseNavigatorAlertDialog();
        final Bundle bundle = new Bundle();
        bundle.putSerializable(BUILDER, builder);
        alertDialog.setArguments(bundle);
        return alertDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        builder = (Builder) getArguments().getSerializable(BUILDER);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());

        if (!TextUtils.isEmpty(builder.getTitle())) {
            alertBuilder.setTitle(builder.getTitle());
        } else {
            alertBuilder.setTitle(R.string.tv_title_choose_nav_program);
        }

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        int padding = getResources().getDimensionPixelSize(R.dimen.main_padding);
        linearLayout.setPadding(padding, padding, padding, 0);

        ListView listView = new ListView(getActivity());
        listView.setAdapter(new NavProgramAdapter(true));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ApplicationInfo info = (ApplicationInfo) parent.getAdapter().getItem(position);

                if (checkBox != null && checkBox.isChecked()) {
                    NavigatorHelper.saveCommand(getContext(), info, NavigatorHelper.RouteType.AUTO);
                }

                Intent navIntent = NavigatorHelper.getNavigationIntent(info,
                                                                       builder.getToLatitude(),
                                                                       builder.getToLongitude(),
                                                                       NavigatorHelper.RouteType.AUTO);

                if (NavigatorHelper.checkAndStartIntent(navIntent, getContext())) {
                    dismiss();
                }

            }
        });

        linearLayout.addView(listView);
        if (builder.isUseSave()) {
            checkBox = new CheckBox(getActivity());
            if (!TextUtils.isEmpty(builder.getSaveTitle())) {
                checkBox.setText(builder.getSaveTitle());
            } else {
                checkBox.setText(R.string.ch_title_save_nav_program);
            }
            checkBox.setPadding(padding, padding, padding, padding);
            checkBox.setCompoundDrawablePadding(padding);
            linearLayout.addView(checkBox);
            ((LinearLayout.LayoutParams) checkBox.getLayoutParams()).setMargins(padding, 0, 0, 0);
        }
        alertBuilder.setView(linearLayout);

        return alertBuilder.create();
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class Builder extends AbstractBuilder<Builder> {

        private int style;

        public Builder(double toLatitude, double toLongitude) {
            super(toLatitude, toLongitude);
        }

        public ChooseNavigatorAlertDialog build() {
            return ChooseNavigatorAlertDialog.getAlertDialog(this);
        }
    }
}
