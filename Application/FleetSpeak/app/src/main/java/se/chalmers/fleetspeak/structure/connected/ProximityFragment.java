package se.chalmers.fleetspeak.structure.connected;


import android.location.Location;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.model.Model;
import se.chalmers.fleetspeak.model.ModelFactory;
import se.chalmers.fleetspeak.model.ProximityChangeListener;
import se.chalmers.fleetspeak.model.Room;
import se.chalmers.fleetspeak.model.User;
import se.chalmers.fleetspeak.structure.lists.RoomList;
import se.chalmers.fleetspeak.util.LocationUtil;

/**
 * Created by Matz Larsson on 2015-11-17.
 */

public class ProximityFragment extends AppConnectFragment {

    private RoomList roomList;
    private RoomList.OnRoomClickedListener onRoomClickedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_proximity, container, false);
        roomList = new RoomList();
        roomList.setOnRoomClickedListener(onRoomClickedListener);

        loadContents();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_holder_room_proximity, roomList);
        ft.commit();

        return view;
    }

    public void truckModeChanged(boolean b) {
        roomList.changedTruckState(b);

        TextView v = getInfoLabel();
        v.setTextSize(getResources().getDimension(b ? R.dimen.carsize : R.dimen.normalsize));
    }

    private TextView getInfoLabel(){
        if(getActivity()!=null) {
            View v = getActivity().findViewById(R.id.info_proximity_fragment);
            if(v != null && v instanceof TextView){
                return (TextView)v;
            }
        }

        return null;
    }

    public void setOnRoomClickedListener(RoomList.OnRoomClickedListener listener){
        this.onRoomClickedListener = listener;
        if(roomList != null) {
            roomList.setOnRoomClickedListener(listener);
        }
    }

    private void loadContents() {
        final Model m = ModelFactory.getCurrentModel();
        m.requestProximityUpdate(new ProximityChangeListener() {
            @Override
            public Location getRequestedLocation() {
                return LocationUtil.getInstance(ProximityFragment.this.getContext(), false).getCurrentLocation();
            }

            @Override
            public int getRequestedDistance() {
                return 5000;                            //5 km by default
            }

            @Override
            public void roomProximityUpdate(HashMap<Room, ArrayList<User>> roomMap) {
                if (roomList != null) {
                    roomList.refreshData(new ArrayList<>(roomMap.keySet()));
                }

                TextView v = getInfoLabel();
                if(v != null) {
                    v.setVisibility((roomList != null && roomMap != null) ? View.VISIBLE : View.GONE);
                }
            }
        });
    }
}