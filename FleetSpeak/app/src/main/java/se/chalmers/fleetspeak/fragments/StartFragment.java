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
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.util.Utils;

/**
 * A fragment shows the information and option available to the user in the start up of the
 * activity when car is not in driving mode
 * Created by David Gustafsson on 22/02/2015.
 */
public class StartFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("StartFragment:", "Created view for the fragment");
        // Create a new contextThemeWrapper that will set the theme of the application
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), Utils.getThemeID());
        // Create the inflater from the contextThemeWrapper and the parameter inflater
        LayoutInflater localInflater = inflater.from(contextThemeWrapper);
        // Create a new view with the inflater with the layout start fragment
        View view = localInflater.inflate(R.layout.start_fragment, container, false);
        // Set the background of the activity so that it matches the color of the theme used
        // in the fragment
        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Utils.getThemeID()
                == R.style.Theme_Fleetspeak_light ? Color.WHITE : Color.BLACK));
        // enable the fragment to create it's own option menu
        setHasOptionsMenu(true);

        // Find the connection button in the created view
        Button connectButton = (Button) view.findViewById(R.id.connectionButton);
        // Set the onclick logic of the connection button so that it send
        // connect request onclick
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        });

        // Find the edittexts in the created view and set text to correspond to the currently
        // used user setting from utils.
        // Create new TextWatchers for all the editext in the view that will
        // change the currently used user settings to match the content of the edittexts
        // after text is changed
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
        // Get the currently used user setting and set the text in the editext to correspond
        // to them
        ipTextField.setText(Utils.getIpAdress());
        portField.setText(String.valueOf(Utils.getPort()));
        userNameField.setText(Utils.getUsername());
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
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.start_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            this.getActivity().onBackPressed();
        }
        // Check if the menu item selected in the option menu is the day and night
        // toggle button
        if (id == R.id.day_night_toggle) {
            // Change the theme currently used in the activity
            Utils.changeTheme((MainActivity)this.getActivity());
            Log.d("StartFragment", "Change Theme button pressed");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A method that sends a connect request to the main activity
     */
    private void connect(){
        Log.i("StartFragment:", "Starting connection request");
        // Check if the saveUserPref checkbox in the view is checked in that
        // case tell the activity to save the user settings
        if(((CheckBox) this.getView().findViewById(R.id.saveUserPref)).isChecked()){
            ((MainActivity) this.getActivity()).setUserSettings();
        }
        // Tell the main activity to start a connection
        ((MainActivity) this.getActivity()).startConnection();
    }

    /**
     * A method that creates and shows a connection error message in the fragment
     */
    public void showConnectionErrorMessage() {
        Log.i("StartFragment:", "showing connection error message");
        // Create a Alert dialog that will show the a connect error message
        Context context = this.getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Connection Error");
        builder.setMessage("Connection to Server failed");
        // Set the button in the Alertdialog that enables the user to close down
        // the Alert dialog
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
