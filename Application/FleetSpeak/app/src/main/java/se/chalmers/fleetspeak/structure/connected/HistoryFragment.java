package se.chalmers.fleetspeak.structure.connected;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.model.Model;
import se.chalmers.fleetspeak.model.ModelFactory;
import se.chalmers.fleetspeak.model.Room;
import se.chalmers.fleetspeak.structure.lists.RoomList;

/**
 * Created by Matz Larsson on 2015-11-17.
 */

public class HistoryFragment extends AppConnectFragment {

    private RoomList roomList;
    private RoomList.OnRoomClickedListener onRoomClickedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        roomList = new RoomList();
        roomList.setRoomKeeper(new RoomList.RoomKeeper() {
            @Override
            public List<Room> getRooms() {
                return ModelFactory.getCurrentModel().getHistory();
            }
        });
        roomList.setOnRoomClickedListener(onRoomClickedListener);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_holder_room_history, roomList);
        ft.commit();

        return view;
    }

    public void truckModeChanged(boolean b) {
        if(roomList != null)
        roomList.changedTruckState(b);
    }

    public void setOnRoomClickedListener(RoomList.OnRoomClickedListener listener){
        this.onRoomClickedListener = listener;
        if(roomList != null) {
            roomList.setOnRoomClickedListener(listener);
        }
    }

    public void refresh() {
        if (roomList != null) {
            Model m = ModelFactory.getCurrentModel();
            List<Room> rooms = m.getHistory();
            if(rooms != null) {
                roomList.refreshData(rooms);
            }
        }
    }
}