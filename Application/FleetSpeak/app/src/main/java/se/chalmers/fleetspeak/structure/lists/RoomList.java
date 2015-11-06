package se.chalmers.fleetspeak.structure.lists;

import android.app.Activity;
import android.app.Notification;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.User;
import se.chalmers.fleetspeak.structure.connected.CarModeInteractive;


public class RoomList extends Fragment {

    private RoomAdapter roomAdapter;
    private RecyclerView rv;
    private RoomListHolder communicator;

    @Override
    public void onAttach(Activity activity){
        try{
            communicator = (RoomListHolder)activity;
        }catch(ClassCastException cce){
            throw new ClassCastException(activity.toString() + " must implement RoomListHolder");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_list,container, false);
        rv = (RecyclerView)view.findViewById(R.id.rvRooms);
        GridLayoutManager lm = new GridLayoutManager(this.getActivity().getApplicationContext(), 1);
        rv.setLayoutManager(lm);
        roomAdapter = new RoomAdapter(this.getActivity(), communicator);
        rv.setAdapter(roomAdapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void changedTruckState(boolean b){
        roomAdapter.notifyDataSetChanged(b);
    }
    public void resetList(List<Room> roomList){
        roomAdapter.resetList(roomList);
    }
    public void itemChanged(int p){
        roomAdapter.notifyItemChanged(p);
    }
    public void itemChanged(Room room){
        roomAdapter.itemChanged(room);
    }
    public void removeItem(int p){
        roomAdapter.deleteItem(p);
    }
    public void removeItem(Room room){
        roomAdapter.deleteItem(room);
    }
    public void addItem(Room room, int p){
        roomAdapter.addItem(room,p);
    }
    public void addItem(Room room){
        roomAdapter.addItem(room);
    }
    public void hightLightItem(int roomId){
        roomAdapter.highlightRoom(roomId);
    }


    public interface RoomListHolder extends CarModeInteractive{
        List<Room> getRooms();
        int getCurrentRoomId();
        void roomClicked(Room room);
        ArrayList<User> getUsersForRoom(int roomID);
    }
}
