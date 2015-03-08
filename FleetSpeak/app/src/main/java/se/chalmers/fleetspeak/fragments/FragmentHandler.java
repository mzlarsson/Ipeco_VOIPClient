package se.chalmers.fleetspeak.fragments;

import android.app.Fragment;

import se.chalmers.fleetspeak.util.Utils;

/**
 * A class that handles the fragment of MainActivity
 * Created by david_000 on 01/03/2015.
 */
public class FragmentHandler {
    private FragmentName currentFragment;
    private int roomID = -1;
    private Fragment[] fragments ={ new StartFragment(), new CarStartFragment(), new JoinFragment(), new ChatFragment()};
    public enum FragmentName{
        START, JOIN, CHAT;
    }
    public void setRoomID(int i){roomID = i;}
    public FragmentName getCurrentFragment(){
        return currentFragment;
    }
    public Fragment getFragment (FragmentName name){
        currentFragment = name;
        switch (name) {
            case START:
                return Utils.getCarMode()?fragments[1]:fragments[0];

            case JOIN:
                JoinFragment fragment = (JoinFragment)fragments[2];
                fragment.updateRooms();
                return fragment;

            case CHAT:
                ChatFragment chatFragment = ((ChatFragment) fragments[3]);
                chatFragment.setRoomID(roomID);
                return  chatFragment;

            default:
                return  null;
        }
    }
    public void showConnecting(){
        ((StartFragment)fragments[0]).showConnecting(Utils.getCarMode());
        ((CarStartFragment)fragments[1]).showConnecting(Utils.getCarMode());
    }
    public void showConnectionErrorMessage(){
        if(Utils.getCarMode())
            ((CarStartFragment)fragments[1]).showConnectionErrorMessage();
        else
            ((StartFragment) fragments[0]).showConnectionErrorMessage();
    }
    public void upDate(){
        if(currentFragment == FragmentName.CHAT){

        }else if(currentFragment == FragmentName.JOIN){

        }
    }
    public void backPressed(MainActivity activity){
       if(currentFragment == FragmentName.CHAT){
            activity.setFragment(FragmentName.JOIN);
       }else if(currentFragment == FragmentName.JOIN){
            activity.setFragment(FragmentName.START);
       }else{
           activity.finish();
       }


    }
}
