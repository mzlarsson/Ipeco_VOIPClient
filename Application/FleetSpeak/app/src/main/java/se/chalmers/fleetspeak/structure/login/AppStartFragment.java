package se.chalmers.fleetspeak.structure.login;

import android.util.Log;

/**
 * Created by David Gustafsson on 2015-07-21.
 */
public abstract class AppStartFragment extends android.support.v4.app.Fragment {
    protected void startConnection(){
        Log.d("AppaStart","Starting connection request");
        LoginActivity login = (LoginActivity) this.getActivity();
        login.startConnectionProcess();
    }
}
