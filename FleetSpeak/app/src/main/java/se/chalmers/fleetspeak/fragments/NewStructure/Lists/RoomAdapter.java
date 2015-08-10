package se.chalmers.fleetspeak.fragments.NewStructure.Lists;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.User;
import se.chalmers.fleetspeak.fragments.NewStructure.ConnectedProcess.ConnectedCommunicator;

/**
 * Created by David Gustafsson on 2015-07-31.
 */
public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private final LayoutInflater inflater;
    private boolean truckstate;
    List<Room> rooms = Collections.emptyList();
    private ConnectedCommunicator communicator;

    public RoomAdapter(Context context, ConnectedCommunicator communicator) {
        this.inflater = LayoutInflater.from(context);
        this.communicator = communicator;
        this.truckstate = communicator.getTruckState();
        this.rooms = communicator.getRooms();
    }
    public void roomClicked(int position){
        if(rooms.get(position) != null) {
            communicator.roomClicked(rooms.get(position));
        }
    }
    public void notifyDataSetChanged(boolean b){
        changeTruckState(b);
        notifyDataSetChanged();
    };
    public void changeTruckState(boolean b){
        truckstate = b;
    }
    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        view = inflater.inflate(R.layout.room_item_row, viewGroup, false);
        RoomViewHolder viewHolder = new RoomViewHolder(view, truckstate);
        return viewHolder;
    }
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        Room current = rooms.get(position);
        holder.iv.setImageResource(R.drawable.ic_room);
        holder.roomname.setText(current.getName());
        ArrayList<User> users = communicator.getUsersForRoom(current.getId());

        StringBuilder builder = new StringBuilder();
        if(users != null && users.size() > 0) {
            if (truckstate) {
                builder.append(("(" + users.size() + ")"));
            } else {                for(int i = 0; i < users.size(); i++) {
                    builder.append(users.get(i).getName() + ",");
                }
                if(builder.lastIndexOf(",") > 0)
                    builder.deleteCharAt(builder.lastIndexOf(","));
            }
        }
        holder.userListText.setText(builder.toString());

    }
    @Override
    public int getItemCount() {
        return rooms.size();
    }
    public void resetList(List<Room> list){
        if(list != null){
            rooms = list;
        }else{
            rooms = Collections.emptyList();
        }
        notifyDataSetChanged();
    }
    public void addItem(Room room){
        rooms.add(room);
        notifyItemInserted(rooms.size()-1);
    }
    public void addItem(Room room , int pos){
        rooms.add(pos, room);
        notifyItemInserted(pos);
    }
    public void deleteItem(int pos){
        rooms.remove(pos);
        notifyItemRemoved(pos);
    }
    public void deleteItem(Room room){
        int pos = rooms.indexOf(room);
        if(pos > 0)
        deleteItem(pos);

    }
    public Room acessItem(int pos){
        if(!rooms.isEmpty() && pos >= 0 && pos < getItemCount()){
            return rooms.get(pos);
        }
        else
            return null;
    }

    public void itemChanged(Room room) {
        int pos = rooms.indexOf(room);
        if(pos > 0){
           notifyItemChanged(pos);
        }
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv;
        TextView roomname;
        TextView userListText;

        RoomViewHolder(View viewItem, boolean b) {
            super(viewItem);
            viewItem.setOnClickListener(this);
            iv = (ImageView) viewItem.findViewById(R.id.roomIcon);
            roomname = (TextView) viewItem.findViewById(R.id.roomName);
            userListText = (TextView) viewItem.findViewById(R.id.list_item_users);
            truckModeChanged(b);
        }
        public void truckModeChanged(boolean b){
            if(b) {
                roomname.setTextSize(45);
                userListText.setTextSize(30);
                userListText.setSingleLine(true);
            }
            else{
                roomname.setTextSize(20);
                userListText.setTextSize(15);
                userListText.setSingleLine(false);
            }
        }

        @Override
        public void onClick(View view) {
            roomClicked(getAdapterPosition());
        }
    }

}