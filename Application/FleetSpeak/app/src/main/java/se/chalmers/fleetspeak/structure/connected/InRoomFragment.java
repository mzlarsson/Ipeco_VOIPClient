package se.chalmers.fleetspeak.structure.connected;


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
import se.chalmers.fleetspeak.structure.lists.UserList;

/**
 * A simple {@link Fragment} subclass.
 */
public class InRoomFragment extends AppConnectFragment {

    private UserList userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        if(userList != null)
        userList.resetList(list);
    }


    public interface InRoomFragmentHolder extends UserList.UserListHolder{

    }
}
