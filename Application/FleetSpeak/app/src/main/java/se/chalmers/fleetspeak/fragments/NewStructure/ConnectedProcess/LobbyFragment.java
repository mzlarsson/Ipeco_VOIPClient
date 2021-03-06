package se.chalmers.fleetspeak.fragments.NewStructure.ConnectedProcess;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.List;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.fragments.NewStructure.Lists.RoomList;

/**
 * A simple {@link Fragment} subclass.
 */
public class LobbyFragment extends AppConnectFragment {
    private RoomList roomList;
    private ConnectedCommunicator communicator;
    private AlertDialog dialog;
    private View mainView;
    private View altView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (ConnectedCommunicator) this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_lobby, container, false);
        roomList = new RoomList();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_holder_room, roomList);
        ft.commit();

        mainView = view.findViewById(R.id.mainView);
        Log.d("LobyFragment", " MainView is null = "  + (null == mainView));
        altView = view.findViewById(R.id.altView);

        altView.setVisibility(View.INVISIBLE);
        Button button = (Button) view.findViewById(R.id.reconnectButton);
        communicator = (ConnectedCommunicator)this.getActivity();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communicator.reconnect();
            }
        });
        Button createButton = (Button) view.findViewById(R.id.buttonCreateRoom);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewRoomOnClick();
            }
        });

        return view;
    }
    public void truckModeChanged(boolean b){
        roomList.changedTruckState(b);
    }
    private void createNewRoomOnClick() {
        // If the user is not driving create a dialog that promts the user to select a room name and
        // create a room with that name if the user don't put in a room name default to the users name + "'s room"
        if(!communicator.getTruckState()){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("Choose a name for your room");
            final EditText input = new EditText(getActivity());
            input.setHint(communicator.getUsername() + "'s room");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);
            Resources res = communicator.getResources();
            alertDialog.setPositiveButton(res.getString(R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newRoomName;
                    if(input.getText().length() == 0){
                        newRoomName = input.getHint().toString();
                    } else {
                        newRoomName = input.getText().toString();
                    }
                    Log.d("LobbyFragment","Create and move new room");
                    createAndMoveRoom(newRoomName);
                }
            });
            alertDialog.setNegativeButton(res.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog = alertDialog.show();

        } else{ //When the car is driving and the user have selected "Create room" the user won't be
            //allowed to pick a name since it will take to much time.
            String newRoomName = (communicator.getUsername() + getResources().getString(R.string.sRoom));
            createAndMoveRoom(newRoomName);
        }
    }
    private void createAndMoveRoom(String newRoomName){
        communicator.createAndMoveRoom(newRoomName);
    }
    public void closeDialog(){
        if(dialog != null){
            dialog.cancel();
        }
    }
    public void movedToRoom(int roomID){
        roomList.hightLightItem(roomID);
    }
    public void roomAdded(Room room){
        roomList.addItem(room);
    }
    public void roomRemoved(Room room){
        roomList.removeItem(room);
    }
    public void resetList(List<Room> list){
        if(roomList != null) {
            roomList.resetList(list);
        }
    }
    public void isConnected(boolean b){
        if(b){
            mainView.setVisibility(View.VISIBLE);
            altView.setVisibility(View.INVISIBLE);
        }
        else{
            mainView.setVisibility(View.INVISIBLE);
            altView.setVisibility(View.VISIBLE);
        }
    }
}
