package su.ias.utils.navigationutils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class NavigatorHelper {

    private static final String TAG = "NavigatorHelper";
    private static final String SAVED_NAVIGATOR = "nav_package";
    private static final String SAVED_ROUTE_TYPE = "nav_route_type";

    private static final List<String> navProgram = new ArrayList<>();
    private static final List<ApplicationInfo> installProgram = new ArrayList<>();

    private NavigatorHelper() {

    }

    public static void init(Context context) {

        if (installProgram.size() == 0) {

            navProgram.add(NAVIGATORS.YANDEX.getPackageName());
            navProgram.add(NAVIGATORS.GOOGLE.getPackageName());
            navProgram.add(NAVIGATORS.YANDEXMAP.getPackageName());

            installProgram.clear();
            PackageManager packageManager = context.getPackageManager();
            List<ApplicationInfo> installedProgram =
                    packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo info : installedProgram) {
                Log.d(TAG, "program = " + info);
                if (navProgram.contains(info.packageName)) {
                    installProgram.add(info);
                }
            }

        }
        Log.d(TAG, "installProgram = " + installProgram);
    }

    public static List<ApplicationInfo> getNavigatorProgramList() {
        return installProgram;
    }

    public static void showChooseNavigationDialog(FragmentManager fragmentManager,
                                                  final Context context,
                                                  final double latitude,
                                                  final double longitude) {
        showChooseNavigationDialog(fragmentManager, context, latitude, longitude, null, null);
    }

    public static void showChooseNavigationDialog(FragmentManager fragmentManager,
                                                  final Context context,
                                                  final double toLatitude,
                                                  final double toLongitude,
                                                  @Nullable final Double fromLatitude,
                                                  @Nullable final Double fromLongitude) {

        String savedProgram = getSavedNavigator(context);
        boolean isSave = false;
        if (!TextUtils.isEmpty(savedProgram)) {

            for (ApplicationInfo info : installProgram) {
                if (info.packageName.equals(savedProgram)) {
                    isSave = true;
                    context.startActivity(getNavigationIntent(info,
                                                              toLatitude,
                                                              toLongitude,
                                                              getDefaultRouteType(context)));
                }
            }

            if (!isSave) {
                clearCommand(context);
            }

        }

        if (!isSave) {

            if (installProgram.size() > 1) {
                new ChooseNavigatorDialog.Builder(toLatitude, toLongitude).setFromLatitude(
                        fromLatitude)
                        .setFromLongitude(fromLongitude)
                        .build()
                        .show(fragmentManager, "navigatorDialog");
            } else if (installProgram.size() == 1) {
                context.startActivity(getNavigationIntent(installProgram.get(0),
                                                          toLatitude,
                                                          toLongitude,
                                                          RouteType.AUTO));
            } else {
                Toast.makeText(context, R.string.error_navigator_not_found, Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    public static Intent getNavigationIntent(ApplicationInfo info,
                                             final double latitude,
                                             final double longitude,
                                             RouteType type) {

        Intent intent = new Intent();

        if (info.packageName.equals(NAVIGATORS.YANDEX.getPackageName())) {
            intent.setAction(NAVIGATORS.YANDEX.getIntentCommand());
            intent.setPackage(NAVIGATORS.YANDEX.getPackageName());
            intent.putExtra("lat_to", latitude);
            intent.putExtra("lon_to", longitude);
        } else if (info.packageName.equals(NAVIGATORS.GOOGLE.getPackageName())) {

            //@formatter:off
            Uri navUri =
                    Uri.parse("google.navigation:q="
                                      + latitude + ","
                                      + longitude
                                      + "&mode=" + type.getRouteTypeByNav(NAVIGATORS.GOOGLE));
            //@formatter:on
            intent.setData(navUri);
            intent.setAction(NAVIGATORS.GOOGLE.getIntentCommand());
            intent.setPackage(NAVIGATORS.GOOGLE.getPackageName());
        } else if (info.packageName.equals(NAVIGATORS.YANDEXMAP.getPackageName())) {
            //@formatter:off
            Uri navUri =
                    Uri.parse("yandexmaps://maps.yandex.ru/?rtext=~"
                                      + latitude + ","
                                      + longitude
                                      + "&rtt=" + type.getRouteTypeByNav(NAVIGATORS.YANDEXMAP));
             //@formatter:on
            intent.setData(navUri);
            intent.setAction(NAVIGATORS.YANDEXMAP.getIntentCommand());
            intent.setPackage(NAVIGATORS.YANDEXMAP.getPackageName());
        }

        return intent;

    }

    private static SharedPreferences getPreference(Context context) {
        return context.getSharedPreferences("NavigatorHelper", Context.MODE_PRIVATE);
    }

    static void saveCommand(Context context, ApplicationInfo info, RouteType type) {
        SharedPreferences preferences = getPreference(context);
        preferences.edit()
                .putString(SAVED_NAVIGATOR, info.packageName)
                .putString(SAVED_ROUTE_TYPE, type.name())
                .apply();
    }

    public static void clearCommand(Context context) {
        SharedPreferences preferences = getPreference(context);
        preferences.edit().clear().apply();
    }

    public static String getSavedNavigator(Context context) {
        SharedPreferences preferences = getPreference(context);
        return preferences.getString(SAVED_NAVIGATOR, "");
    }

    public static RouteType getDefaultRouteType(Context context) {
        SharedPreferences preferences = getPreference(context);
        return RouteType.valueOf(preferences.getString(SAVED_ROUTE_TYPE, "AUTO"));
    }

    public enum RouteType {

        AUTO, PUBLIC_TRANSPORT, ON_FOOT;

        public int getIcon() {
            switch (this) {
                case AUTO:
                    return R.drawable.ic_directions_car_black_24dp;
                case PUBLIC_TRANSPORT:
                    return R.drawable.ic_directions_bus_black_24dp;
                default:
                    return R.drawable.ic_directions_walk_black_24dp;
            }
        }

        public String getRouteTypeByNav(NAVIGATORS navigators) {

            switch (navigators) {
                case GOOGLE:
                    switch (this){
                        case AUTO:
                            return "d";
                        default:
                            return "w";
                    }
                case YANDEXMAP:

                    switch (this){
                        case AUTO:
                            return "auto";
                        case PUBLIC_TRANSPORT:
                            return "mt";
                        case ON_FOOT:
                           return "pd";
                    }
                default:
                   return "auto";
            }
        }

    }

    private enum NAVIGATORS {
        YANDEX, GOOGLE, YANDEXMAP;

        public String getPackageName() {
            switch (this) {
                case YANDEX:
                    return "ru.yandex.yandexnavi";
                case YANDEXMAP:
                    return "ru.yandex.yandexmaps";
                default:
                    return "com.google.android.apps.maps";
            }
        }

        public String getIntentCommand() {
            switch (this) {
                case YANDEX:
                    return "ru.yandex.yandexnavi.action.BUILD_ROUTE_ON_MAP";
                default:
                    return Intent.ACTION_VIEW;
            }
        }
    }
}
