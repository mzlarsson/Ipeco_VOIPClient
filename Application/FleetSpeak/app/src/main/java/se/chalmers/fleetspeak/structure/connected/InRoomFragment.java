package se.chalmers.fleetspeak.structure.connected;


import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.model.Model;
import se.chalmers.fleetspeak.model.ModelFactory;
import se.chalmers.fleetspeak.model.User;
import se.chalmers.fleetspeak.structure.lists.UserList;

/**
 * A simple {@link Fragment} subclass.
 */
public class InRoomFragment extends AppConnectFragment {

    private UserList userList;

    public InRoomFragment(){
        userList = new UserList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_in_room, container, false);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_holder_user, userList);
        ft.commit();

        return view;
    }

    public void setOnUserClickedListener(UserList.OnUserClickedListener listener){
        userList.setOnUserClickedListener(listener);
    }

    public void truckModeChanged(boolean b) {
        userList.changedTruckState(b);
    }

    public void userAdded(User user) {
        userList.addItem(user);
    }

    public void userRemoved(User user) {
        userList.itemRemoved(user);
    }

    public void refresh() {
        Log.d("inroom", "refresh");
        Model m = ModelFactory.getCurrentModel();
        List<User> users = m.getUsers(m.getCurrentRoom());
        if(users != null) {
            Log.d("Inroom", users.size() + "");
            if (userList != null)
                userList.refreshData(users);
        }
    }
}
