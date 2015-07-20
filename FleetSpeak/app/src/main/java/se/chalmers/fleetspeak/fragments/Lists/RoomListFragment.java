package se.chalmers.fleetspeak.fragments.Lists;

import android.app.LauncherActivity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.internal.view.menu.ListMenuItemView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.User;
import se.chalmers.fleetspeak.fragments.MainActivity;

/**
 * Created by David Gustafsson on 2015-07-20.
 */
public class RoomListFragment extends ListFragment {
    private List<Room> rooms;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rooms = new ArrayList<Room>();
        getMain().getRooms();
        setListAdapter(new JoinRoomAdapter(getActivity(), rooms));
    }
    public MainActivity getMain(){
        return (MainActivity) this.getActivity();
    }
    private class JoinRoomAdapter extends ArrayAdapter<Room> {
        private JoinRoomAdapter(Context context, List<Room> values) {
            super(context, R.layout.list_item_rooms, values);
            Log.d("JoinFragment", "Creating join room adapter");
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(getMain().getCarMode()?R.layout.list_item_rooms_while_driving:
                    R.layout.list_item_rooms, parent, false);
            TextView roomView = (TextView) view.findViewById(R.id.roomName);
            ImageView imageView = (ImageView) view.findViewById(R.id.roomIcon);
            TextView userView = (TextView) view.findViewById(R.id.list_item_users);

            String whatRoom = getItem(position).getName();

            roomView.setText(whatRoom);
            imageView.setImageResource(R.drawable.ic_room);

            ArrayList<User> users =  getMain().getUsers(getItem(position).getId());
            StringBuilder builder = new StringBuilder();
            if(users != null && users.size() > 0) {
                if (getMain().getCarMode()) {
                    builder.append(("(" + users.size() + ")"));
                } else {
                    for(int i = 0; i < users.size(); i++) {
                        builder.append(users.get(i).getName() + ",");
                    }
                    if(builder.lastIndexOf(",") > 0)
                        builder.deleteCharAt(builder.lastIndexOf(","));
                }
            }
            userView.setText(builder.toString());

            return view;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setDivider(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

    }
}
