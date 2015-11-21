package se.chalmers.fleetspeak.gui.connected;

/**
 * Created by David Gustafsson on 2015-07-23.
 */
public abstract class AppConnectFragment extends android.support.v4.app.Fragment {

    public abstract void truckModeChanged(boolean b);
    public abstract void refresh();

}
