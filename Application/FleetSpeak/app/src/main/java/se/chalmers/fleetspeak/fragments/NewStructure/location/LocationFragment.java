package se.chalmers.fleetspeak.fragments.NewStructure.location;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.fragments.NewStructure.ConnectedProcess.AppConnectFragment;

public class LocationFragment extends AppConnectFragment {

    LocationManager locationManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
}
