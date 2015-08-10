package se.chalmers.fleetspeak.fragments.NewStructure.LoginProcess;

import android.app.Fragment;

/**
 * Created by David Gustafsson on 2015-07-21.
 */
public abstract class AppStartFragment extends Fragment {
    protected void startConnection(){
        LoginActivity login = (LoginActivity) this.getActivity();
        login.startConnectionProcess();
    }
}
