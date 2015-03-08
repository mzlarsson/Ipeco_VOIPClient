package se.chalmers.fleetspeak.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import se.chalmers.fleetspeak.R;
import se.chalmers.fleetspeak.util.Utils;

/**
 * Created by david_000 on 22/02/2015.
 */
public class CarStartFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), Utils.getThemeID());

        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        View view = localInflater.inflate(R.layout.car_start_fragment, container, false);

        final Button connect = (Button)view.findViewById(R.id.connectionButton);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        });
        // Set the textViews
        ((TextView) view.findViewById(R.id.IpAdress)).setText(Utils.getIpAdress());
        ((TextView) view.findViewById(R.id.userName)).setText(Utils.getPort());

        setHasOptionsMenu(true);

        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        menuInflater.inflate(R.menu.day_night_menu, menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.day_night_toggle) {
            Utils.changeTheme((MainActivity)this.getActivity(), this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void connect(){
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
