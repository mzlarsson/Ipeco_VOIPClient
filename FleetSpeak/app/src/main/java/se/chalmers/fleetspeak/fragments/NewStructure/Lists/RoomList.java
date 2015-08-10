package se.chalmers.fleetspeak.fragments.NewStructure.Lists;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.fragments.NewStructure.ConnectedProcess.ConnectedCommunicator;


public class RoomList extends Fragment {
    private RoomAdapter roomAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_list,container, false);
        RecyclerView rv = (RecyclerView)view.findViewById(R.id.rvRooms);
        LinearLayoutManager lm = new LinearLayoutManager(this.getActivity().getApplicationContext());
        rv.setLayoutManager(lm);
        ConnectedCommunicator communicator = (ConnectedCommunicator)this.getActivity();
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
}
