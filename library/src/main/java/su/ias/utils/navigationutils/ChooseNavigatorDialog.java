package su.ias.utils.navigationutils;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

/**
 * Created on 6/1/17.
 */
public class ChooseNavigatorDialog extends BottomSheetDialogFragment {

    //is empty string;
    private static final String BUILDER = "";

    private TabLayout tb_routeType;
    private CheckBox ch_save;
    private Builder builder;

    private static ChooseNavigatorDialog getChooser(Builder builder) {
        ChooseNavigatorDialog navigatorDialog = new ChooseNavigatorDialog();
        final Bundle bundle = new Bundle();
        bundle.putSerializable(BUILDER, builder);
        navigatorDialog.setArguments(bundle);
        return navigatorDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_choose_navigation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        builder = (Builder) getArguments().getSerializable(BUILDER);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        ch_save = (CheckBox) view.findViewById(R.id.ch_save);
        tb_routeType = (TabLayout) view.findViewById(R.id.tab_roteType);
        GridView gv_program = (GridView) view.findViewById(R.id.gv_navigator);
        gv_program.setAdapter(new NavProgramAdapter());
        gv_program.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ApplicationInfo info = (ApplicationInfo) parent.getAdapter().getItem(position);

                NavigatorHelper.RouteType routeType =
                        NavigatorHelper.RouteType.valueOf(builder.defaultRoadType);

                if (builder.useRoadType) {
                    routeType =
                            NavigatorHelper.RouteType.values()[tb_routeType.getSelectedTabPosition()];
                }

                if (ch_save.isChecked()) {
                    NavigatorHelper.saveCommand(getContext(), info, routeType);
                }

                Intent navIntent = NavigatorHelper.getNavigationIntent(info,
                                                                       builder.toLatitude,
                                                                       builder.toLongitude,
                                                                       routeType);

                if (navIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    getContext().startActivity(navIntent);
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), R.string.error_create_intent, Toast.LENGTH_LONG)
                            .show();
                }

            }
        });

        if (builder.useRoadType) {
            for (NavigatorHelper.RouteType type : NavigatorHelper.RouteType.values()) {
                tb_routeType.addTab(tb_routeType.newTab().setIcon(type.getIcon()));
            }
        } else {
            tb_routeType.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(builder.title)) {
            tv_title.setText(builder.title);
        }
        if (builder.useSave) {
            if (!TextUtils.isEmpty(builder.saveTitle)) {
                ch_save.setText(builder.saveTitle);
            }
        } else {
            ch_save.setChecked(false);
            ch_save.setVisibility(View.GONE);
        }

    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class Builder implements Serializable {

        private boolean useRoadType = true;
        private boolean useSave = true;
        private String title = null;
        private String saveTitle = null;
        private String defaultRoadType = NavigatorHelper.RouteType.AUTO.name();
        private Double fromLatitude = null;
        private Double fromLongitude = null;
        private double toLatitude;
        private double toLongitude;
        private int style;

        public Builder(double latitude, double longitude) {
            toLongitude = longitude;
            toLatitude = latitude;
        }

        public Builder setUseRoadType(boolean useRoadType) {
            this.useRoadType = useRoadType;
            return this;
        }

        public Builder setUseSave(boolean useSave) {
            this.useSave = useSave;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setSaveTitle(String saveTitle) {
            this.saveTitle = saveTitle;
            return this;
        }

        public Builder setDefaultRoadType(String defaultRoadType) {
            this.defaultRoadType = defaultRoadType;
            return this;
        }

        public Builder setFromLatitude(Double fromLatitude) {
            this.fromLatitude = fromLatitude;
            return this;
        }

        public Builder setFromLongitude(Double fromLongitude) {
            this.fromLongitude = fromLongitude;
            return this;
        }

        public ChooseNavigatorDialog build() {
            return ChooseNavigatorDialog.getChooser(this);
        }
    }

}
