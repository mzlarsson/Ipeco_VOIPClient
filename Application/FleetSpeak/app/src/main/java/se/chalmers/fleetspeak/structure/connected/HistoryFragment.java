package se.chalmers.fleetspeak.structure.connected;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.model.Model;
import se.chalmers.fleetspeak.model.ModelFactory;
import se.chalmers.fleetspeak.model.Room;
import se.chalmers.fleetspeak.structure.lists.RoomList;



public class HistoryFragment extends AppConnectFragment {

    private RoomList roomList;
    private HistoryFragmentHolder communicator;

    public HistoryFragment(){
        super();
        roomList = new RoomList();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            communicator = (HistoryFragmentHolder) activity;
        } catch (ClassCastException cce) {
            throw new ClassCastException(activity.toString() + " must implement LobbyFragmentHolder");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_proximity, container, false);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_holder_room, roomList);
        ft.commit();

        return view;
    }

    public void truckModeChanged(boolean b) {
        roomList.changedTruckState(b);
    }

    public void setOnRoomClickedListener(RoomList.OnRoomClickedListener listener){
        roomList.setOnRoomClickedListener(listener);
    }


    public void movedToRoom(int roomID) {
        roomList.hightLightItem(roomID);
    }

    public void roomAdded(Room room) {
        roomList.addItem(room);
    }

    public void roomRemoved(Room room) {
        roomList.removeItem(room);
    }

    public void refresh() {
        Log.d("Proxmity", "refresh");
        if (roomList != null) {
            Model m = ModelFactory.getCurrentModel();
            List<Room> rooms = m.getHistory();
            if(rooms != null) {
                Log.d("Proximity", rooms.size() + "");
                roomList.refreshData(rooms);
            }
        }
    }

    public interface HistoryFragmentHolder{
        public void moveToRoom();
    }
}