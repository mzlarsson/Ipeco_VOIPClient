package se.chalmers.fleetspeak.fragments.NewStructure.LoginProcess;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import se.chalmers.fleetspeak.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartLogin extends AppStartFragment {
    private StartCommunicator com;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        com = (StartCommunicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_login, container, false);
        Button connectButton = (Button) view.findViewById(R.id.connectionButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        });
        final EditText userNameField = (EditText) view.findViewById(R.id.usernameField);
        String username = savedInstanceState.getString("username");
        userNameField.setText(username);
        userNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                com.changeUsername(String.valueOf(userNameField.getText()));
            }
        });

        final EditText passwordField = (EditText) view.findViewById(R.id.passwordField);
        String password = savedInstanceState.getString("password");
        passwordField.setText(password);
        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                com.changePassword(String.valueOf(passwordField.getText()));
            }
        });

        // Set the container of the edittextfield clickable so that user can unfocus the
        // application if they press outside the edittextfields
        view.findViewById(R.id.relStart_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("StartActivity", "container view is clicked");
                view.requestFocus();
                InputMethodManager imm = (InputMethodManager) view.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        return view;
    }
    private void connect(){
        if(((CheckBox) this.getView().findViewById(R.id.saveUserPref)).isChecked()){
            com.saveUserSettings();
        }
        startConnection();
    }

}
