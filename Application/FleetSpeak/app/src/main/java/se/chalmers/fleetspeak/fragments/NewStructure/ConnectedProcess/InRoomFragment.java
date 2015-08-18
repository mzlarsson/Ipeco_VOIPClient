package se.chalmers.fleetspeak.fragments.NewStructure.ConnectedProcess;


import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.User;
import se.chalmers.fleetspeak.fragments.NewStructure.Lists.UserAdapter;
import se.chalmers.fleetspeak.fragments.NewStructure.Lists.UserList;

/**
 * A simple {@link Fragment} subclass.
 */
public class InRoomFragment extends AppConnectFragment {
    private UserList userList;
    private ConnectedCommunicator communicator;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (ConnectedCommunicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_in_room, container, false);
        userList = new UserList();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_holder_user, userList);
        ft.commit();

        return view;
    }
    public void truckModeChanged(boolean b){
        userList.changedTruckState(b);
    }
    public void userAdded(User user){
        userList.addItem(user);
    }
    public void userRemoved(User user){
        userList.itemRemoved(user);
    }
    public void resetList(List<User> list){
        userList.resetList(list);
    }


}
