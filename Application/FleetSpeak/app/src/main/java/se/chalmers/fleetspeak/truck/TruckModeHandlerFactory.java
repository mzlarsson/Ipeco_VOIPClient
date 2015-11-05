package se.chalmers.fleetspeak.truck;

import android.content.Context;

/**
 * Created by Matz on 2015-11-05.
 */
public class TruckModeHandlerFactory {

    private static TruckModeHandler handler;

    public static TruckModeHandler getCurrentHandler(Context context){
        if(handler == null) {
            if (canAccessHardware()) {
                handler = new TruckHardwareHandler();
            }else{
                handler = new LocationDataHandler(context);
            }
        }

        return handler;
    }

    private static boolean canAccessHardware(){
        return false;
    }
}
