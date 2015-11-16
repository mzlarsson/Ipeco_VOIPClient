package se.chalmers.fleetspeak.model;

import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Interface for comunication between the model and the GUI for handling requests of nearby users.
 *
 * Created by Patrik on 2015-11-16.
 */
public interface ProximityChangeListener {

    /**
     * Will be called before roomProximityUpdate to get the location to base the search from.
     * @return The origin to search from.
     */
    Location getRequestedLocation();

    /**
     * Will be called before roomProximityUpdate to get the distance from the location.
     * @return The distance in meters to search for users.
     */
    int getRequestedDistance();

    /**
     * Will be called when the request to for nearby users is completed.
     * @param roomMap A map with the rooms as key and a list with the users of that room within the
     * distance as the values.
     */
    void roomProximityUpdate(HashMap<Room,ArrayList<User>> roomMap);
}
