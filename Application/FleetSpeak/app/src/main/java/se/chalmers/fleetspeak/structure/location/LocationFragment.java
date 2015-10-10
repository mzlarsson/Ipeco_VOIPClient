package se.chalmers.fleetspeak.structure.location;

import android.content.Context;
import android.location.LocationManager;

import se.chalmers.fleetspeak.structure.connected.AppConnectFragment;

public class LocationFragment extends AppConnectFragment {

    LocationManager locationManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
}
