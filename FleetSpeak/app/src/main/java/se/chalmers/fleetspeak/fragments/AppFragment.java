package se.chalmers.fleetspeak.fragments;

import android.app.Fragment;

/**
 * A Super class for the apps fragments
 * Created by David Gustafsson on 2015-07-15.
 */
public abstract class AppFragment extends Fragment {
    protected MainActivity getMain(){
        return (MainActivity) this.getActivity();
    }

    /**
     * Action that will take place if a user press the upp button
     */
    protected void onUpPressed(){

    }
    /**
     * Action that will take place if a user press the down button
     */
    protected void onDownPressed(){

    }
    /**
     * Action that will take place if a user press the right button
     */
    protected void onRightPressed(){

    }
    /**
     * Action that will take place if a user press the left button
     */
    protected void onLeftPressed(){

    }

    /**
     * Action that will take place if a user press the enter button
     */
    protected void onEnterPressed(){

    }


}
