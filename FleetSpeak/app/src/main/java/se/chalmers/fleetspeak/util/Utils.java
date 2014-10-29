package se.chalmers.fleetspeak.util;

import android.app.Activity;
import android.content.Intent;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.activities.StartActivity;

/**
 * A util class that saves and get temporary settings
 * Created by david_000 on 21/10/2014.
 */
public class Utils {
    private static boolean firstRun = true;
    public static final int DARK = R.style.Theme_Fleetspeak_dark;
    public static final int LIGHT = R.style.Theme_Fleetspeak_light;
    private static int appTheme = DARK;
    private static String currentUsername = "";

    /**
     * Set the theme to either Dark or LIGHT
     * @param i - the theme to set
     */
    public static void setTheme(int i){
        if((i == DARK || i == LIGHT) && firstRun) {
            appTheme = i;
        }
    }

    /**
     * Set a temporary username
     * @param username
     */
    public static void setUsername(String username) {
        currentUsername = username;
    }

    /**
     * Get the tempory username
     * @return - the temporary username
     */
    public static String getUsername() {
        return currentUsername;
    }

    /**
     * Change the theme of the application
     * @param activity
     */
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

    /**
     * A method that set the theme of the application when the activity is created
     * @param activity
     */
    public static void onCreateActivityCreateTheme(Activity activity) {
    activity.setTheme(appTheme);
    }

    /**
     * Get the current theme ID
     * @return int - the current theme ID
     */
    public static int getThemeID() {
        return  appTheme;
    }
}
