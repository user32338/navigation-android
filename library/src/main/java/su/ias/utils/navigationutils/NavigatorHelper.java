package su.ias.utils.navigationutils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * help to send navigation intent
 */
public final class NavigatorHelper {

    private static final String TAG = "NavigatorHelper";
    private static final String SAVED_NAVIGATOR = "nav_package";
    private static final String SAVED_ROUTE_TYPE = "nav_route_type";

    private static final List<String> navProgram = new ArrayList<>();
    private static final List<ApplicationInfo> installProgram = new ArrayList<>();
    private static boolean isDebug = false;
    private static boolean routeType = false;
    private static boolean saveCommand = true;
    private static String title;
    private static String saveTitle;

    private NavigatorHelper() {
    }

    private static void init(Builder builder) {
        installProgram.clear();
        if (builder.useGoogleMaps) {
            navProgram.add(NAVIGATORS.GOOGLE.getPackageName());
        }
        if (builder.useYandexMap) {
            navProgram.add(NAVIGATORS.YANDEXMAP.getPackageName());
        }
        if (builder.useYandexNav) {
            navProgram.add(NAVIGATORS.YANDEX.getPackageName());
        }

        isDebug = builder.debug;
        routeType = builder.routeType;
        saveCommand = builder.saveCommand;
        title = builder.title;
        saveTitle = builder.saveTitle;

        createNavList(builder.context.getPackageManager());

    }

    public static void init(Context context) {

        if (installProgram.size() == 0) {

            navProgram.add(NAVIGATORS.YANDEX.getPackageName());
            navProgram.add(NAVIGATORS.GOOGLE.getPackageName());
            navProgram.add(NAVIGATORS.YANDEXMAP.getPackageName());

            installProgram.clear();
            createNavList(context.getPackageManager());
        }
        if (isDebug) {
            Log.d(TAG, "installProgram = " + installProgram);
        }
    }

    private static void createNavList(PackageManager packageManager) {
        List<ApplicationInfo> installedProgram =
                packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo info : installedProgram) {
            if (isDebug) {
                Log.d(TAG, "program = " + info);
            }
            if (navProgram.contains(info.packageName)) {
                installProgram.add(info);
            }
        }
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

        showNavDialog(fragmentManager, context,
                      //@formatter:off
                      new ChooseNavigatorBottomDialog.Builder(toLatitude, toLongitude)
                              .setDefaultRoadType(RouteType.AUTO.name())
                              .setUseRoadType(routeType)
                              .setTitle(title)
                              .setSaveTitle(saveTitle)
                              .setFromLatitude(fromLatitude)
                              .setFromLongitude(fromLongitude)
                              .setUseSave(saveCommand));
                      //@formatter:on

    }

    public static void showChooseNavigationAlertDialog(FragmentManager fragmentManager,
                                                       final Context context,
                                                       final double latitude,
                                                       final double longitude) {
        showChooseNavigationAlertDialog(fragmentManager, context, latitude, longitude, null, null);
    }

    public static void showChooseNavigationAlertDialog(FragmentManager fragmentManager,
                                                       final Context context,
                                                       final double toLatitude,
                                                       final double toLongitude,
                                                       @Nullable final Double fromLatitude,
                                                       @Nullable final Double fromLongitude) {

        showNavDialog(fragmentManager, context,
                      //@formatter:off
                      new ChooseNavigatorAlertDialog.Builder(toLatitude, toLongitude)
                              .setTitle(title)
                              .setSaveTitle(saveTitle)
                              .setFromLatitude(fromLatitude)
                              .setFromLongitude(fromLongitude)
                              .setUseSave(saveCommand));
                      //@formatter:on

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

    public static boolean checkAndStartIntent(Intent navIntent, Context context) {
        if (navIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(navIntent);
            return true;
        } else {
            Toast.makeText(context, R.string.error_create_intent, Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public static void showNavDialog(FragmentManager fragmentManager,
                                     Context context,
                                     AbstractBuilder builder) {

        String savedProgram = getSavedNavigator(context);
        boolean isSave = false;
        if (!TextUtils.isEmpty(savedProgram)) {

            for (ApplicationInfo info : installProgram) {
                if (info.packageName.equals(savedProgram)) {
                    isSave = true;
                    context.startActivity(getNavigationIntent(info,
                                                              builder.getToLatitude(),
                                                              builder.getToLongitude(),
                                                              getDefaultRouteType(context)));
                }
            }

            if (!isSave) {
                clearCommand(context);
            }

        }

        if (!isSave) {

            if (installProgram.size() > 1) {
                builder.build().show(fragmentManager, "navigatorDialog");
            } else if (installProgram.size() == 1) {
                context.startActivity(getNavigationIntent(installProgram.get(0),
                                                          builder.getToLatitude(),
                                                          builder.getToLongitude(),
                                                          RouteType.AUTO));
            } else {
                Toast.makeText(context, R.string.error_navigator_not_found, Toast.LENGTH_LONG)
                        .show();
            }
        }

    }

    public static List<ApplicationInfo> getNavigatorProgramList() {
        return new ArrayList<>(installProgram);
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
                    switch (this) {
                        case AUTO:
                            return "d";
                        default:
                            return "w";
                    }
                case YANDEXMAP:

                    switch (this) {
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

    @SuppressWarnings("unused")
    public static class Builder {

        private Context context;
        private boolean useYandexNav = true;
        private boolean useYandexMap = true;
        private boolean useGoogleMaps = true;
        private boolean debug = false;
        private boolean routeType = false;
        private boolean saveCommand = true;
        private String title = null;
        private String saveTitle = null;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setUseYandexNav(boolean useYandexNav) {
            this.useYandexNav = useYandexNav;
            return this;
        }

        public Builder setUseYandexMap(boolean useYandexMap) {
            this.useYandexMap = useYandexMap;
            return this;
        }

        public Builder setUseGoogleMaps(boolean useGoogleMaps) {
            this.useGoogleMaps = useGoogleMaps;
            return this;
        }

        public Builder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder setRouteType(boolean routeType) {
            this.routeType = routeType;
            return this;
        }

        public Builder setSaveCommand(boolean saveCommand) {
            this.saveCommand = saveCommand;
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

        public Builder setSaveTitle(@StringRes int saveTitlee) {
            this.saveTitle = context.getResources().getString(saveTitlee);
            return this;
        }

        public Builder setTitle(@StringRes int title) {
            this.title = context.getResources().getString(title);
            return this;
        }

        public void init() {
            NavigatorHelper.init(this);
        }

    }
}
