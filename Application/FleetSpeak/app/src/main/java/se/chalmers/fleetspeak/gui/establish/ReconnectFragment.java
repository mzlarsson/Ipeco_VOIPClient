package se.chalmers.fleetspeak.gui.establish;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import se.chalmers.fleetspeak.R;

public class ReconnectFragment extends Fragment {
    ReconnectFragmentHolder communicator;

    public ReconnectFragment() {
        super();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            communicator = (ReconnectFragmentHolder) activity;
        } catch (ClassCastException cce) {
            throw new ClassCastException(activity.toString() + " must implement LobbyFragmentHolder");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_reconnect, container, false);
        Button button = (Button) view.findViewById(R.id.reconnectButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communicator.reconnect();
            }
        });
        return view;
    }
    public interface ReconnectFragmentHolder{
        public void reconnect();
    }

}
