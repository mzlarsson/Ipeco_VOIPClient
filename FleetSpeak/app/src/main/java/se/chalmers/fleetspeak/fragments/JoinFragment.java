package se.chalmers.fleetspeak.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.User;
import se.chalmers.fleetspeak.util.Utils;

/**
 * A fragment that shows the information and options available to the user when the
 * user has been connected to the server but has yet chosen a room
 * Created by David Gustafsson on 22/02/2015.
 */
public class JoinFragment extends Fragment{
    // A Arraylist that stores the available rooms that the fragment shall show
    private ArrayList<Room> rooms = new ArrayList<Room>();
    // A ArrayAdapter that enables the fragment view to show the rooms in rooms
    private ArrayAdapter<Room> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("Joinfragment:","view created");
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), Utils.getThemeID());

        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.join_fragment, container, false);
        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Utils.getThemeID() == R.style.Theme_Fleetspeak_light ? Color.WHITE : Color.BLACK));

        ListView roomView = ((ListView)view.findViewById(R.id.roomView));
        rooms.addAll(getMain().getRooms());
        adapter = new JoinRoomAdapter(getMain(), rooms);

        Log.d("JoinFragment","Adapter" +  adapter);
        roomView.setAdapter(adapter);
        roomView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                joinRoom(((Room) adapterView.getItemAtPosition(position)).getId());
            }
        });

        // TODO

        Button createRoom = (Button) view.findViewById(R.id.buttonCreateRoom);
        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewRoomOnClick();
            }
        });

        setHasOptionsMenu(true);



        return view;
    }
    private MainActivity getMain(){
        return (MainActivity) this.getActivity();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.day_night_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case  R.id.day_night_toggle:
                Utils.changeTheme(getMain());

                return true;
            case android.R.id.home:
                getMain().setFragment(FragmentHandler.FragmentName.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void update(){
        rooms.clear();
        rooms.addAll(getMain().getRooms());
        adapter.notifyDataSetChanged();
    }
    private void createNewRoomOnClick() {
        // If the user is not driving create a dialog that promts the user to select a room name and
        // create a room with that name if the user don't put in a room name default to the users name + "'s room"
        if(!Utils.getCarMode()){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getMain());
            alertDialog.setTitle("Choose a name for your room");
            final EditText input = new EditText(getMain());
            input.setHint(Utils.getUsername() + "'s room");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);

            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newRoomName;
                    if(input.getText().length() == 0){
                        newRoomName = input.getHint().toString();
                    } else {
                        newRoomName = input.getText().toString();
                    }
                    createAndMoveRoom(newRoomName);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        } else{ //When the car is driving and the user have selected "Create room" the user won't be
            //allowed to pick a name since it will take to much time.
            String newRoomName = (Utils.getUsername() + "'s room");
            createAndMoveRoom(newRoomName);
        }
    }
    private void createAndMoveRoom(String newRoomName){
        getMain().createAndMoveRoom(newRoomName);
    }
    private void joinRoom(int roomID){
        getMain().moveToRoom(roomID);
    }

    /**
     * Inner class, Adapter for the Room's ListView
     * The adapter handles all the items in the ListView
     */
    private class JoinRoomAdapter extends ArrayAdapter<Room> {
        private JoinRoomAdapter(Context context, ArrayList<Room> values) {
            super(context, R.layout.list_item_rooms, values);
            Log.d("JoinFragment", "Creating join room adapter");
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            View view = inflater.inflate(Utils.getCarMode()?R.layout.list_item_rooms_while_driving:
                    R.layout.list_item_rooms, parent, false);
            TextView roomView = (TextView) view.findViewById(R.id.roomName);
            ImageView imageView = (ImageView) view.findViewById(R.id.roomIcon);
            TextView userView = (TextView) view.findViewById(R.id.list_item_users);

            String whatRoom = getItem(position).getName();

            roomView.setText(whatRoom);
            imageView.setImageResource(R.drawable.ic_room);

            ArrayList<User> users =  getMain().getUsers(getItem(position).getId());
            StringBuilder builder = new StringBuilder();
            if(users != null) {
                if (Utils.getCarMode()) {
                    builder.append(("(" + users.size() + ")"));
                } else {
                    if(users.size() > 0) {
                        builder.append(users.get(0));
                        int i = 1;
                        while (i < users.size()) {
                            builder.append(users.get(i));
                        }
                    }
                }
            }
            userView.setText(builder.toString());

            return view;
        }
    }
}
