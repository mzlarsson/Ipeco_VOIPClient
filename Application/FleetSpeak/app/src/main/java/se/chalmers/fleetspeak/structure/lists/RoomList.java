package se.chalmers.fleetspeak.structure.lists;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.model.Room;


public class RoomList extends Fragment {

    private RoomAdapter roomAdapter;
    private RecyclerView rv;
    private OnRoomClickedListener onRoomClickedListener;

    private RoomKeeper keeper;

    public void setRoomKeeper(RoomKeeper keeper){
        this.keeper = keeper;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_list, container, false);
        rv = (RecyclerView) view.findViewById(R.id.rvRooms);
        GridLayoutManager lm = new GridLayoutManager(this.getActivity().getApplicationContext(), 1);
        rv.setLayoutManager(lm);
        roomAdapter = new RoomAdapter(this.getActivity(), (keeper!=null?keeper.getRooms(): Collections.EMPTY_LIST));
        roomAdapter.setOnRoomClickedListener(onRoomClickedListener);
        rv.setAdapter(roomAdapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setOnRoomClickedListener(OnRoomClickedListener listener) {
        this.onRoomClickedListener = listener;
        if(roomAdapter != null) {
            roomAdapter.setOnRoomClickedListener(listener);
        }
    }

    public void changedTruckState(boolean b) {
        if(roomAdapter != null) {
            roomAdapter.notifyDataSetChanged(b);
        }
    }

    public void refreshData(List<Room> roomList) {
        if(roomAdapter != null) {
            roomAdapter.refreshData(roomList);
        }
    }

    public void hightLightItem(int roomId) {
        roomAdapter.highlightRoom(roomId);
    }


    public interface OnRoomClickedListener {
        void onRoomClicked(Room room);
    }

    public interface RoomKeeper{
        List<Room> getRooms();
    }
}
