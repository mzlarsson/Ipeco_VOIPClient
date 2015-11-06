package se.chalmers.fleetspeak.truck;

/**
 * Contains a switch between two modes to determine car mode or not.
 * To register for data, please use:
 *      TruckModeHandler handler = TruckModeHandlerFactory.getCurrentHandler(context);
 *      handler.addListener(truckStateListener);
 * Version 1.0
 * Created by Matz on 2014-10-17.
 */
public interface TruckStateListener {

    public void truckModeChanged(boolean mode);

}
