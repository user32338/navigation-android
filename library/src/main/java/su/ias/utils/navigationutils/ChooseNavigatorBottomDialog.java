package su.ias.utils.navigationutils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

import java.io.Serializable;

/**
 * Created on 6/1/17.
 * Bottom Sheet dialog with choose naviagtor program
 */
public class ChooseNavigatorBottomDialog extends BottomSheetDialogFragment {

    //is empty string;
    private static final String BUILDER = "";

    private TabLayout tb_routeType;
    private CheckBox ch_save;
    private Builder builder;

    private static ChooseNavigatorBottomDialog getChooser(Builder builder) {
        ChooseNavigatorBottomDialog navigatorDialog = new ChooseNavigatorBottomDialog();
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
                                                                       builder.getToLatitude(),
                                                                       builder.getToLongitude(),
                                                                       routeType);

                if (NavigatorHelper.checkAndStartIntent(navIntent, getContext())) {
                    dismiss();
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

        if (!TextUtils.isEmpty(builder.getTitle())) {
            tv_title.setText(builder.getTitle());
        }
        if (builder.isUseSave()) {
            if (!TextUtils.isEmpty(builder.getSaveTitle())) {
                ch_save.setText(builder.getSaveTitle());
            }
        } else {
            ch_save.setChecked(false);
            ch_save.setVisibility(View.GONE);
        }

    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class Builder extends AbstractBuilder<Builder> implements Serializable {

        private boolean useRoadType = true;
        private String defaultRoadType = NavigatorHelper.RouteType.AUTO.name();

        public Builder(double toLatitude, double toLongitude) {
            super(toLatitude, toLongitude);
        }

        public Builder setUseRoadType(boolean useRoadType) {
            this.useRoadType = useRoadType;
            return this;
        }

        public Builder setDefaultRoadType(String roadType){
            defaultRoadType = roadType;
            return this;
        }

        public ChooseNavigatorBottomDialog build() {
            return ChooseNavigatorBottomDialog.getChooser(this);
        }
    }

}
