package se.chalmers.fleetspeak.structure.lists;


import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.User;
import se.chalmers.fleetspeak.structure.connected.CarModeInteractive;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserList extends Fragment {
    private UserAdapter userAdapter;
    private UserListHolder communicator;

    @Override
    public void onAttach(Activity activity){
        try{
            communicator = (UserListHolder)activity;
        }catch(ClassCastException cce){
            throw new ClassCastException(activity.toString() + " must implement UserListHolder");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        RecyclerView rv = (RecyclerView)view.findViewById(R.id.rvUsers);
        GridLayoutManager gm = new GridLayoutManager(this.getActivity().getApplicationContext(),1);
        rv.setLayoutManager(gm);
        userAdapter = new UserAdapter(this.getActivity(), communicator);
        rv.setAdapter(userAdapter);
        return view;
    }
    public void changedTruckState(boolean b){
        userAdapter.notifyDataSetChanged(b);
    }
    public void itemChanged(int p){
        userAdapter.notifyItemChanged(p);
    }
    public void itemRemoved(int p){
        userAdapter.deleteItem(p);
    }
    public void itemRemoved(User user){
        userAdapter.deleteItem(user);
    }
    public void addItem(User user){
        userAdapter.addItem(user);
    }
    public void addItem(User user, int pos){
        userAdapter.addItem(user, pos);
    }
    public User accessItem(int pos){
        return userAdapter.acessItem(pos);
    }
    public void resetList(List<User> userList){
        userAdapter.resetList(userList);
    }


    public interface UserListHolder extends CarModeInteractive{
        List<User> getCurrentRoomsUsers();
        void sendUserClicked(User user);
        Resources getResources();
    }
}

