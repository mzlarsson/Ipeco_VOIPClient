package se.chalmers.fleetspeak.util;

import android.util.Log;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.fragments.MainActivity;

/**
 * A util class that saves and get temporary settings
 * Created by David Gustafsson on 21/10/2014.
 */
public class appSettings {
    private static boolean firstRun = true;
    private static String currentUsername = "";
    private static String currentIpAdress = "";
    private static int currentPort = 1;

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
}
