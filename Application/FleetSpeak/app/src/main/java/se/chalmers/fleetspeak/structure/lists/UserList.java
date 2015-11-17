package se.chalmers.fleetspeak.structure.lists;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserList extends Fragment {

    private UserAdapter userAdapter;
    private OnUserClickedListener onUserClickedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rvUsers);
        GridLayoutManager gm = new GridLayoutManager(this.getActivity().getApplicationContext(), 1);
        rv.setLayoutManager(gm);
        userAdapter = new UserAdapter(this.getActivity());
        if(onUserClickedListener != null){
            userAdapter.setOnUserClickedListener(onUserClickedListener);
        }
        rv.setAdapter(userAdapter);
        return view;
    }

    public void setOnUserClickedListener(OnUserClickedListener listener){
        this.onUserClickedListener = listener;
        if(userAdapter != null) {
            userAdapter.setOnUserClickedListener(listener);
        }
    }

    public void changedTruckState(boolean b) {
        if(userAdapter != null){
            userAdapter.notifyDataSetChanged(b);
        }
    }

    public void refreshData(List<User> userList) {
        if(userAdapter != null){
            userAdapter.refreshData(userList);
        }
    }

    public interface OnUserClickedListener {
        void onUserClicked(User user);
    }
}

