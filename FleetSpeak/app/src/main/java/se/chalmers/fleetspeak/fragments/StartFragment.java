package se.chalmers.fleetspeak.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.util.Utils;

/**
 * Created by david_000 on 22/02/2015.
 */
public class StartFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("StartFragment","isDark=" + (Utils.getThemeID() == R.style.Theme_Fleetspeak_dark));
        Log.i("StartFragment","isLight=" + (Utils.getThemeID() == R.style.Theme_Fleetspeak_light));

        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), Utils.getThemeID());
        LayoutInflater localInflater = inflater.from(contextThemeWrapper);

        View view = localInflater.inflate(R.layout.start_fragment, container, false);

        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Utils.getThemeID() == R.style.Theme_Fleetspeak_light ? Color.WHITE : Color.BLACK));
        setHasOptionsMenu(true);

        Button connectButton = (Button) view.findViewById(R.id.connectionButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        });


        // Set up the textfield of the application
        final EditText ipTextField = (EditText) view.findViewById(R.id.ipField);
        ipTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
            Utils.setIpAdress(String.valueOf(ipTextField.getText()));
            }
        });
        final EditText portField = (EditText) view.findViewById(R.id.portField);
        portField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    Utils.setPortNumber(Integer.parseInt(String.valueOf(portField.getText())));
                }catch (NumberFormatException e){

                }
            }
        });
        final EditText userNameField = (EditText) view.findViewById(R.id.usernameField);
        userNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Utils.setUsername(String.valueOf(userNameField.getText()));
            }
        });
        ipTextField.setText(Utils.getIpAdress());
        portField.setText(String.valueOf(Utils.getPort()));
        userNameField.setText(Utils.getUsername());


        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        menuInflater.inflate(R.menu.day_night_menu, menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.day_night_toggle) {
            Utils.changeTheme((MainActivity)this.getActivity());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setNewUserSettings(){
        ((MainActivity) this.getActivity()).setUserSettings();
    }
    private void connect(){
        View view = this.getView();
        if(((CheckBox) view.findViewById(R.id.saveUserPref)).isChecked()){
            setNewUserSettings();
        }
        ((MainActivity) this.getActivity()).startConnection();
    }

    /**
     * A method that creates and shows a connection error message
     */
    public void showConnectionErrorMessage() {
        Context context = this.getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Connection Error");
        builder.setMessage("Connection to Server failed");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog connectionError = builder.create();
        connectionError.show();
    }
}
