package se.chalmers.fleetspeak.truck;

/**
 * Created by Matz on 2014-10-17.
 */
public interface TruckListener {

    public void speedChanged(float currentSpeed);
    public void parkingBrakeChanged(boolean isOn);
}
