package se.chalmers.fleetspeak.util;

import android.app.Activity;
import android.content.Intent;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.activities.StartActivity;

/**
 * Created by david_000 on 21/10/2014.
 */
public class ThemeUtils {
    private static boolean firstRun = true;
    private static final int DARK = R.style.Theme_Fleetspeak_dark;
    private static final int LIGHT = R.style.Theme_Fleetspeak_light;
    private static int appTheme = DARK;
    private static String currentUsername = "";
    public static void setTheme(int i){
        if((i == DARK || i == LIGHT) && firstRun) {
            appTheme = i;
        }
    }

    public static void setUsername(String username) {
        currentUsername = username;
    }

    public static String getUsername() {
        return currentUsername;
    }

    public static void changeTheme(Activity activity){
        firstRun = false;
        if(appTheme == DARK){
            appTheme = LIGHT;
        }else{
            appTheme = DARK;
        }
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }
    public static void onCreateActivityCreateTheme(Activity activity) {
    activity.setTheme(appTheme);
    }

    public static int getThemeID() {
        return  appTheme;
    }
}
