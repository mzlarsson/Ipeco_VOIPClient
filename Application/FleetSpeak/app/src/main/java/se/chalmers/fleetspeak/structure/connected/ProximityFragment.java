package se.chalmers.fleetspeak.structure.connected;


import android.location.Location;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.model.Model;
import se.chalmers.fleetspeak.model.ModelFactory;
import se.chalmers.fleetspeak.model.ProximityChangeListener;
import se.chalmers.fleetspeak.model.Room;
import se.chalmers.fleetspeak.model.User;
import se.chalmers.fleetspeak.structure.lists.RoomList;
import se.chalmers.fleetspeak.truck.TruckModeHandlerFactory;
import se.chalmers.fleetspeak.util.LocationUtil;

/**
 * Created by Matz Larsson on 2015-11-17.
 */

public class ProximityFragment extends AppConnectFragment implements ProximityChangeListener{

    private boolean carmode = false;

    private RoomList roomList;
    private TextView infoView;
    private RoomList.OnRoomClickedListener onRoomClickedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        carmode = TruckModeHandlerFactory.getCurrentHandler().truckModeActive();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_proximity, container, false);
        roomList = new RoomList();
        roomList.setOnRoomClickedListener(onRoomClickedListener);

        infoView = (TextView)view.findViewById(R.id.info_proximity_fragment);

        loadContents();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_holder_room_proximity, roomList);
        ft.commit();

        return view;
    }

    public void truckModeChanged(boolean b) {
        if(roomList != null) {
            roomList.changedTruckState(b);
        }

        carmode = b;
        updateInfoLabel(false);
    }

    private void updateInfoLabel(boolean loading){
        if(infoView != null) {
            boolean hasItems = (roomList != null && roomList.getRoomCount() > 0);
            infoView.setVisibility(hasItems ? View.GONE : View.VISIBLE);
            infoView.setText(getResources().getText(loading?R.string.room_not_loaded:R.string.no_rooms_found));
            infoView.setTextSize(getResources().getDimension(carmode ? R.dimen.carsize : R.dimen.normalsize));
        }else{
            Log.d("Nano", "Got an uninitiated info view");
        }
    }

    public void setOnRoomClickedListener(RoomList.OnRoomClickedListener listener){
        this.onRoomClickedListener = listener;
        if(roomList != null) {
            roomList.setOnRoomClickedListener(listener);
        }
    }

    private void loadContents() {
        updateInfoLabel(true);

        final Model m = ModelFactory.getCurrentModel();
        if (true) { //FIXME Temp hack for DEMO 24/11
            Location loc = new Location("current user: Volvo");
            loc.setLongitude(11.920601);
            loc.setLatitude(57.716697);
            refreshData(new ArrayList<Room>(m.getRoomsCloserThan(loc, 500000).keySet()));
        } else {
            m.requestProximityUpdate(this);
        }
    }

    private void refreshData(List<Room> rooms){
        roomList.refreshData(rooms);
        updateInfoLabel(false);
    }

    @Override
    public Location getRequestedLocation() {
        return LocationUtil.getInstance(ProximityFragment.this.getContext(), false).getCurrentLocation();
    }

    @Override
    public int getRequestedDistance() {
        return 50000;                            //50 km by default
    }

    @Override
    public void roomProximityUpdate(final HashMap<Room, ArrayList<User>> roomMap) {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            if (roomList != null && roomMap != null) {
                refreshData(new ArrayList<>(roomMap.keySet()));
            }
            }
        });
    }
}