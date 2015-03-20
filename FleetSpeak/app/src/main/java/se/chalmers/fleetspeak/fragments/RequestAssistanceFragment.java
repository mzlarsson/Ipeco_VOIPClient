package se.chalmers.fleetspeak.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.util.Utils;

/**
 * Created by david_000 on 20/03/2015.
 */
public class RequestAssistanceFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("Joinfragment:", "view created");
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), Utils.getThemeID());

        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.request_assistance_fragment, container, false);
        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Utils.getThemeID() == R.style.Theme_Fleetspeak_light ? Color.WHITE : Color.BLACK));

        setHasOptionsMenu(true);

        Button requestFleet =(Button) view.findViewById(R.id.requestFleet);
        Button requestMechanic = (Button) view.findViewById(R.id.requestFleet);
        Button requestVechile = (Button) view.findViewById(R.id.requestVechile);
        Button requestTraffic = (Button) view.findViewById(R.id.requestTraffic);
        return view;
    }
    

}
