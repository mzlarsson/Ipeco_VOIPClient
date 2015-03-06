package se.chalmers.fleetspeak.util;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.fragments.MainActivity;

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
    private static String currentIpAdress = "";
    private static int currentPort = 1;
    private static boolean carmode = false;

    /**
     * Set the theme to either Dark or LIGHT if it is the first run of the application
     * @param i - the theme to set
     */
    public static void setTheme(int i){
        if((i == DARK || i == LIGHT) && firstRun) {
            appTheme = i;
        }
    }
    public static void setCarmode(boolean b){carmode = b;}
    public static boolean getCarMode(){return carmode;}

    /**
     * Set the temporary username
     * @param username - the name to best to the temporary username
     */
    public static void setUsername(String username) {
        currentUsername = username;
    }

    /**
     * Set the temporary port number
     * @param port- the number to be set to the temporary portnumber
     */
    public static void setPortNumber(int port){
        currentPort = port;
    }

    /**
     * Set the temporary ip adress
     * @param adress - the temporary ip adress
     */
    public static void setIpAdress(String adress){
        currentIpAdress = adress;
    }

    /**
     * Get the tempory username
     * @return - the temporary username
     */
    public static String getUsername() {
        return currentUsername;
    }
    /**
     *  Get the temporary ip adress
     *  @return - the temporary ip adress
     */
    public static String getIpAdress(){return currentIpAdress;}
    /**
     * Get the temporary port number
     * @return the temporary username
     */
    public static int getPort(){return currentPort;}
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
    public static void changeTheme(MainActivity activity, Fragment fragment){
        firstRun = false;
        if(appTheme == DARK){
            appTheme = LIGHT;
        }else{
            appTheme = DARK;
        }
        activity.resetFragment();
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
