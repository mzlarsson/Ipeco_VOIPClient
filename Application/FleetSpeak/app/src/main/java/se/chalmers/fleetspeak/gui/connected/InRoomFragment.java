package se.chalmers.fleetspeak.gui.connected;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.model.Model;
import se.chalmers.fleetspeak.model.ModelFactory;
import se.chalmers.fleetspeak.model.User;
import se.chalmers.fleetspeak.gui.lists.UserList;

/**
 * A simple {@link Fragment} subclass.
 */
public class InRoomFragment extends AppConnectFragment {

    private UserList userList;
    private UserList.OnUserClickedListener onUserClickedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_in_room, container, false);
        this.userList = new UserList();
        this.userList.setOnUserClickedListener(onUserClickedListener);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_holder_user, userList);
        ft.commit();

        return view;
    }

    public void setOnUserClickedListener(UserList.OnUserClickedListener listener){
        this.onUserClickedListener = listener;
        if(userList != null) {
            userList.setOnUserClickedListener(listener);
        }
    }

    public void truckModeChanged(boolean b) {
        if(userList != null) {
            userList.changedTruckState(b);
        }
    }

    public void refresh() {
        Model m = ModelFactory.getCurrentModel();
        List<User> users = m.getUsers(m.getCurrentRoom());
        if(users != null && userList != null) {
            userList.refreshData(users);
        }
    }
}
