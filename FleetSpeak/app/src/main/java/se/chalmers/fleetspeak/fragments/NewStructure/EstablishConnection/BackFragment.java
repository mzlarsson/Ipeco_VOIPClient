package se.chalmers.fleetspeak.fragments.NewStructure.EstablishConnection;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.fragments.NewStructure.ConnectedProcess.AppConnectFragment;
import se.chalmers.fleetspeak.fragments.NewStructure.ConnectedProcess.ConnectedCommunicator;

/**
 * A simple {@link Fragment} subclass.
 */
public class BackFragment extends AppConnectFragment {
    ConnectedCommunicator communicator;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (ConnectedCommunicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_back, container, false);
        Button yes = (Button) view.findViewById(R.id.yesButton);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.onBackYes();
            }
        });
        Button no = (Button) view.findViewById(R.id.noButton);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.onBackNo();
            }
        });
        return view;
    }



}
