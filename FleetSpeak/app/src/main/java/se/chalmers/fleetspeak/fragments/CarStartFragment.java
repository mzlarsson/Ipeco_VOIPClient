package se.chalmers.fleetspeak.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import se.chalmers.fleetspeak.R;

/**
 * A fragment that shows the information and option available to the user in the start up of the
 * activity when the car is in driving mode
 * Created by David Gustafsson on 22/02/2015.
 */
public class CarStartFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.i("CarStartFragment:", "fragment created");
        // Create a new view with the inflater with the layout start fragment
        View view = inflater.inflate(R.layout.car_start_fragment, container, false);
        // enable the fragment to create it's own option menu
        setHasOptionsMenu(true);

        // Set the onclick logic of the connection button so that it send
        // connect request onclick
        Button connect = (Button)view.findViewById(R.id.connectionButton);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        });

        // Find the textview in the created view and set the text of the textview
        // to correspond to the userssetting in main activity
        ((TextView) view.findViewById(R.id.IpAdress)).setText(getMain().getIpAdress());
        Log.d("DOESITWORK", view.findViewById(R.id.userName)==null?"nope":"yes");
        ((TextView) view.findViewById(R.id.userName)).setText(getMain().getUsername());
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        menuInflater.inflate(R.menu.start_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A method that sends a connection request to the main activity
     */
    private void connect(){
        Log.i("CarStartFragment:", "Starting connection request");
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
    private MainActivity getMain(){
        return (MainActivity)getActivity();
    }
}
