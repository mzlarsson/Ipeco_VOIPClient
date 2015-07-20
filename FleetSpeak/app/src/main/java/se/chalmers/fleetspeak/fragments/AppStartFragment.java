package se.chalmers.fleetspeak.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import se.chalmers.fleetspeak.R;

/**
 * A super class for the start fragments
 * Created by David Gustafsson on 2015-07-16.
 */
public abstract class AppStartFragment extends AppFragment {
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
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
     * A method that creates and shows a connection error message in the fragment
     */
    protected void showConnectionErrorMessage() {
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
