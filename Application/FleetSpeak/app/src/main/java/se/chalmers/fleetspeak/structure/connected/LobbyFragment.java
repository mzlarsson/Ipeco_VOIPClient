package se.chalmers.fleetspeak.structure.connected;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import se.chalmers.fleetspeak.model.Model;
import se.chalmers.fleetspeak.model.ModelFactory;
import se.chalmers.fleetspeak.model.Room;
import se.chalmers.fleetspeak.structure.lists.RoomList;
import se.chalmers.fleetspeak.truck.TruckModeHandlerFactory;

public class LobbyFragment extends AppConnectFragment implements createRoomDialog.cRDListener {

    private RoomList roomList;
    private LobbyFragmentHolder communicator;
    private Dialog dialog;


    private RoomList.OnRoomClickedListener onRoomClickedListener;

    public LobbyFragment(){
        super();
        roomList = new RoomList();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            communicator = (LobbyFragmentHolder) activity;
        } catch (ClassCastException cce) {
            throw new ClassCastException(activity.toString() + " must implement LobbyFragmentHolder");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_lobby, container, false);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_holder_room, roomList);
        ft.commit();

        Button createButton = (Button) view.findViewById(R.id.buttonCreateRoom);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewRoomOnClick();
            }
        });

        return view;
    }

    public void truckModeChanged(boolean b) {
        roomList.changedTruckState(b);
    }

    private void createNewRoomOnClick() {
        // If the user is not driving create a dialog that promts the user to select a room name and
        // create a room with that name if the user don't put in a room name default to the users name + "'s room"
        boolean truckMode = TruckModeHandlerFactory.getCurrentHandler().truckModeActive();
        Model model = ModelFactory.getCurrentModel();
        if (!truckMode) {
            dialog = new createRoomDialog(getContext(), this, model.getCurrentUserAlias() + "'s room" );
            dialog.show();

        } else { //When the car is driving and the user have selected "Create room" the user won't be
            //allowed to pick a name since it will take to much time.
            String newRoomName = (model.getCurrentUserAlias() + getResources().getString(R.string.sRoom));
            createAndMoveRoom(newRoomName);
        }
    }

    public void setOnRoomClickedListener(RoomList.OnRoomClickedListener listener){
        roomList.setOnRoomClickedListener(listener);
    }

    private void createAndMoveRoom(String newRoomName) {
        ModelFactory.getCurrentModel().moveNewRoom(newRoomName);
        communicator.moveToRoom();
    }

    public void closeDialog() {
        if (dialog != null) {
            dialog.cancel();
        }
    }

    public void movedToRoom(int roomID) {
        roomList.hightLightItem(roomID);
    }

    public void roomAdded(Room room) {
        roomList.addItem(room);
    }

    public void roomRemoved(Room room) {
        roomList.removeItem(room);
    }

    public void refresh() {
        Log.d("Lobby", "refresh");
        if (roomList != null) {
            Model m = ModelFactory.getCurrentModel();
            List<Room> rooms = m.getRooms();
            if(rooms != null) {
                Log.d("Lobby", rooms.size() + "");
                roomList.refreshData(rooms);
            }
        }
    }

    @Override
    public void okClick(String newRoomName) {
        createAndMoveRoom(newRoomName);
    }
    public interface LobbyFragmentHolder{
        public void moveToRoom();
    }
}