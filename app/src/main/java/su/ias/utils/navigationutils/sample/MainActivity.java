package su.ias.utils.navigationutils.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import su.ias.utils.navigationutils.ChooseNavigatorAlertDialog;
import su.ias.utils.navigationutils.NavigatorHelper;

public class MainActivity extends AppCompatActivity {

    private TextView tvSavedProgram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // use builder
        new NavigatorHelper.Builder(this).setUseYandexMap(false)
                .setRouteType(true)
                .setSaveCommand(true)
                .init();

        // or use default settings
        //NavigatorHelper.init(this);

        tvSavedProgram = (TextView) findViewById(R.id.tv_savedProgram);

        // show bottom sheet dialog
        findViewById(R.id.bnt_openChooser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavigatorHelper.showChooseNavigationDialog(getSupportFragmentManager(),
                                                           MainActivity.this,
                                                           55.76009f,
                                                           37.648801f);
            }
        });

        findViewById(R.id.bnt_clearSaveProgram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigatorHelper.clearCommand(MainActivity.this);
                tvSavedProgram.setText(R.string.no_save_program);
            }
        });

        // show alert dialog
        findViewById(R.id.bnt_openChooserAlert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavigatorHelper.showChooseNavigationAlertDialog(getSupportFragmentManager(),
                                                           MainActivity.this,
                                                           55.76009f,
                                                           37.648801f);
            }
        });

        // or use custom dialog....
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(NavigatorHelper.getSavedNavigator(this))) {
            tvSavedProgram.setText(getString(R.string.save_program,
                                             NavigatorHelper.getSavedNavigator(this),
                                             NavigatorHelper.getDefaultRouteType(this)));
        } else {
            tvSavedProgram.setText(R.string.no_save_program);
        }

    }
}
