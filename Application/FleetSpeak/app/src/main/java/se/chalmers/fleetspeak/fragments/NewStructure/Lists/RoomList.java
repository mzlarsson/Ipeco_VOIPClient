package se.chalmers.fleetspeak.fragments.NewStructure.Lists;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.List;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.fragments.NewStructure.ConnectedProcess.ConnectedCommunicator;


public class RoomList extends Fragment {
    private RoomAdapter roomAdapter;
    RecyclerView rv;
    View altView;
    ConnectedCommunicator communicator;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_list,container, false);
        altView = (RelativeLayout) view.findViewById(R.id.altView);
        Button button = (Button) view.findViewById(R.id.reconnectButton);
        communicator = (ConnectedCommunicator)this.getActivity();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communicator.reconnect();
            }
        });
        rv = (RecyclerView)view.findViewById(R.id.rvRooms);
        GridLayoutManager lm = new GridLayoutManager(this.getActivity().getApplicationContext(), 1);
        rv.setLayoutManager(lm);
        roomAdapter = new RoomAdapter(this.getActivity(), communicator);
        rv.setAdapter(roomAdapter);
        altView.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    public void isConnected(boolean b){
        if(b){
            altView.setVisibility(View.INVISIBLE);
            rv.setVisibility(View.VISIBLE);
        }else{
            altView.setVisibility(View.VISIBLE);
            rv.setVisibility(View.INVISIBLE);
        }
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
}
