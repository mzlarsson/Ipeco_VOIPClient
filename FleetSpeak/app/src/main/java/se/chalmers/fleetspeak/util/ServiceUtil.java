package se.chalmers.fleetspeak.util;

import se.chalmers.fleetspeak.SocketService;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

/**
 * Created by Fridgeridge on 2014-10-23.
 */
public class ServiceUtil {

    public static void close(Context context){
        if(SoundController.hasValue()) {
            SoundController.close();
        }

        if(isMyServiceRunning(context, ServiceConnection.class)){
            context.stopService(new Intent(context, SocketService.class));
        }
    }


    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
