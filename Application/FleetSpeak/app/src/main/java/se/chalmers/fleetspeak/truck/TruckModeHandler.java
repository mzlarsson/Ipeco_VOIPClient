package se.chalmers.fleetspeak.truck;

/**
 * Main setup for a handler to control the truck mode with listeners
 * for all classes that needs to receive the data. Notifications should
 * be sent to all listeners when the value of truck mode is evaluated
 * to have changed.
 * Created by Matz on 2015-11-05.
 */
public interface TruckModeHandler {

    public void addListener(TruckStateListener listener);
    public void removeListener(TruckStateListener listener);
    public boolean truckModeActive();

}
