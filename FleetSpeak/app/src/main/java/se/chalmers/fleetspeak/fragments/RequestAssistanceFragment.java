package se.chalmers.fleetspeak.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
 * Created by David Gustafsson on 20/03/2015.
 */
public class RequestAssistanceFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("RequestAssistanceFragment:", "view created");
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), Utils.getThemeID());

        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.request_assistance_fragment, container, false);
        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Utils.getThemeID() == R.style.Theme_Fleetspeak_light ? Color.WHITE : Color.BLACK));

        setHasOptionsMenu(true);

        Button requestFleet =(Button) view.findViewById(R.id.requestFleet);
        requestFleet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMain().requestAssistance(0);
            }
        });
        Button requestMechanic = (Button) view.findViewById(R.id.requestFleet);
        requestMechanic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMain().requestAssistance(1);
            }
        });
        Button requestVechile = (Button) view.findViewById(R.id.requestVechile);
        requestVechile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMain().requestAssistance(2);
            }
        });
        Button requestTraffic = (Button) view.findViewById(R.id.requestTraffic);
        requestTraffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMain().requestAssistance(3);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.day_night_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                getMain().onBackPressed();
                return true;
            case R.id.day_night_toggle:
                Utils.changeTheme(getMain());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private MainActivity getMain(){
        return (MainActivity) this.getActivity();
    }
    public void update(){
        //TODO
    }
}
