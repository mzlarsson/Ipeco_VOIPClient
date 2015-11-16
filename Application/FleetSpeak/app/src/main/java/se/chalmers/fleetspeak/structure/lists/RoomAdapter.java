package se.chalmers.fleetspeak.structure.lists;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.model.Model;
import se.chalmers.fleetspeak.model.ModelFactory;
import se.chalmers.fleetspeak.model.Room;
import se.chalmers.fleetspeak.model.User;
import se.chalmers.fleetspeak.truck.TruckModeHandlerFactory;

/**
 * Created by David Gustafsson on 2015-07-31.
 */
public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private Context context;
    private final LayoutInflater inflater;
    private boolean truckstate;
    private int highlightPos = -1;
    List<Room> rooms = Collections.emptyList();
    private RoomList.OnRoomClickedListener onRoomClickedListener;

    public RoomAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.truckstate = TruckModeHandlerFactory.getCurrentHandler().truckModeActive();

        Model model = ModelFactory.getCurrentModel();
        List<Room> rooms = model.getRooms();
        if (rooms != null) {
            this.rooms = rooms;
            int currentRoom = model.getCurrentRoom();
            for (Room r : rooms) {
                if (r.getId() == currentRoom) {
                    highlightPos = rooms.indexOf(r);
                }
            }
        }
    }

    public void setOnRoomClickedListener(RoomList.OnRoomClickedListener listener) {
        this.onRoomClickedListener = listener;
    }

    public void roomClicked(int position) {
        if (rooms.get(position) != null) {
            if (onRoomClickedListener != null) {
                onRoomClickedListener.onRoomClicked(rooms.get(position));
            }
        }
    }

    public void notifyDataSetChanged(boolean b) {
        changeTruckState(b);
        notifyDataSetChanged();
    }

    public void changeTruckState(boolean b) {
        truckstate = b;
        updateAllItemInList();
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        view = inflater.inflate(R.layout.room_item_row, viewGroup, false);
        RoomViewHolder viewHolder = new RoomViewHolder(view, truckstate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        Room currentRoom = rooms.get(position);
        if (position == highlightPos) {
            Log.d("RoomAdapter", "Highlighting room = " + highlightPos);
            holder.roomname.setTextColor(Color.GREEN);
        } else {
            holder.roomname.setTextColor(Color.WHITE);
        }
        holder.iv.setImageResource(R.drawable.ic_room);
        holder.roomname.setText(currentRoom.getName());
        ArrayList<User> users = ModelFactory.getCurrentModel().getUsers(currentRoom.getId());
        StringBuilder builder = new StringBuilder();
        if (users != null && users.size() > 0) {
            Log.d("RoomAdapter", "Viewholder start user list ");
            if (truckstate) {
                builder.append(("(" + users.size() + ")"));
            } else {
                for (int i = 0; i < users.size(); i++) {
                    builder.append(users.get(i).getName() + ",");
                }
                if (builder.lastIndexOf(",") > 0)
                    builder.deleteCharAt(builder.lastIndexOf(","));
            }
        }
        holder.truckModeChanged(truckstate);
        Log.d("RoomAdapter", "Builder built string= " + builder.toString());
        holder.userListText.setText(builder.toString());

    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public void updateAllItemInList() {
        notifyDataSetChanged();
    }

    public void refreshData(List<Room> list) {
        if (list != null) {
            rooms = list;
        } else {
            rooms = Collections.emptyList();
        }
        highlightPos = ModelFactory.getCurrentModel().getCurrentRoom();

        notifyDataSetChanged();
    }

    public void addItem(Room room) {
        rooms.add(room);
        notifyItemInserted(rooms.size() - 1);
    }

    public void addItem(Room room, int pos) {
        rooms.add(pos, room);
        notifyItemInserted(pos);
    }

    public void deleteItem(int pos) {
        rooms.remove(pos);
        notifyItemRemoved(pos);
    }

    public void deleteItem(Room room) {
        int pos = rooms.indexOf(room);
        if (pos > 0)
            deleteItem(pos);

    }

    public void highlightRoom(int roomID) {
        int oldPos = highlightPos;
        for (Room r : rooms) {
            if (r.getId() == roomID) {
                highlightPos = rooms.indexOf(r);
            }
        }
        notifyItemChanged(oldPos);
        notifyItemChanged(highlightPos);
    }

    public Room acessItem(int pos) {
        if (!rooms.isEmpty() && pos >= 0 && pos < getItemCount()) {
            return rooms.get(pos);
        } else
            return null;
    }

    public void itemChanged(Room room) {
        int pos = rooms.indexOf(room);
        if (pos > 0) {
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
            if (userListText == null) {
                Log.d("RoomAdapter", "userListText is null");
            }

        }

        public void truckModeChanged(boolean b) {
            //Valid since we retrieve the instance of the communicator via the inverse casting.
            if (b) {
                float car = context.getResources().getDimension(R.dimen.carsize);
                roomname.setTextSize(car);
                userListText.setTextSize(car - 5);
                userListText.setSingleLine(true);
            } else {
                float normal = context.getResources().getDimension(R.dimen.normalsize);
                roomname.setTextSize(normal);
                userListText.setTextSize(normal - 5);
                userListText.setSingleLine(false);
            }
        }

        @Override
        public void onClick(View view) {
            roomClicked(getAdapterPosition());
        }
    }
}