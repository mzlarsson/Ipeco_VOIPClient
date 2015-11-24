package se.chalmers.fleetspeak.gui.login;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import se.chalmers.fleetspeak.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarStartLogin extends AppStartFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_car_start_login, container, false);
        final Button connectButton = (Button)view.findViewById(R.id.connectionButton);
        connectButton.requestFocus();//Set to focus so that the user only need to press enter to connect
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConnection();
            }
        });

        Bundle savedInformation = this.getArguments();
        String username = savedInformation.getString("username");
        ((TextView) view.findViewById(R.id.userName)).setText(username);
        TextView errorView = (TextView) view.findViewById(R.id.error_text);
        String errorText = savedInformation.getString("error");
        if(errorText != null){
            errorView.setText(errorText);
        }else{
            errorView.setVisibility(View.INVISIBLE);
        }
        return view;
    }
    public void changeUsername(String username){
        View view = this.getView();
        ((TextView) view.findViewById(R.id.userName)).setText(username);

    }
    public void changePassword(String password){
        View view = this.getView();
        ((TextView) view.findViewById(R.id.passwordField)).setText(password);
    }


}
