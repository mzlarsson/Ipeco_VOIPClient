package se.chalmers.fleetspeak.fragments.NewStructure.location;

import android.location.Location;

/**
 * Created by David Gustafsson on 2015-08-20.
 */
public interface LocationUpdateListener {
    public void locationUpdated(Location newLocation);
}
