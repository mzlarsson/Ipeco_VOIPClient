package se.chalmers.fleetspeak.fragments;

import android.app.Fragment;
import android.util.Log;

import se.chalmers.fleetspeak.util.Utils;

/**
 * A class that handles the fragment of MainActivity
 * Created by david_000 on 01/03/2015.
 */
public class FragmentHandler {
    private FragmentName currentFragment;
    private Fragment[] fragments ={ new StartFragment(), new CarStartFragment(), new JoinFragment(),
            new ChatFragment(), new DisconnectFragment() , new RequestAssistanceFragment()};
    public enum FragmentName{
        START, JOIN, CHAT, DISCONNECT , REQUEST
    }
    public FragmentName getCurrentFragment(){
        return currentFragment;
    }
    public Fragment getFragment (FragmentName name){
        currentFragment = name;
        switch (name) {
            case START:
                return Utils.getCarMode()?fragments[1]:fragments[0];
            case JOIN:
                return fragments[2];
            case CHAT:
                Log.d("FragHandler" , " recreating chatfragment");
                fragments[3] = new ChatFragment();
                return fragments[3];
            case DISCONNECT:
                return fragments[4];
            case REQUEST:
                return fragments[5];
            default:
                return  null;
        }
    }
    public void showConnectionErrorMessage(){
        if(Utils.getCarMode() && currentFragment == FragmentName.START)
            ((CarStartFragment)fragments[1]).showConnectionErrorMessage();
        else
            ((StartFragment) fragments[0]).
                    showConnectionErrorMessage();
    }

    public void showAuthenticationErrorMessage(){
        if(Utils.getCarMode() && currentFragment == FragmentName.START)
            ((CarStartFragment)fragments[1]).showAuthenticationErrorMessage();
        else
            ((StartFragment) fragments[0]).
                    showAuthenticationErrorMessage();
    }

    public  void update(FragmentName name){
        Log.d("FragHandler", "update called");
        if(name == currentFragment){
            switch (name){
                case CHAT:
                    ((ChatFragment)fragments[3]).update();
                    break;
                case JOIN:
                    ((JoinFragment) fragments[2]).update();
                    break;
                case REQUEST:
                    ((RequestAssistanceFragment) fragments[5]).update();
            }

        }
    }
    public void backPressed(MainActivity activity){
       if(currentFragment == FragmentName.CHAT){
            activity.setFragment(FragmentName.JOIN);
       }else if(currentFragment == FragmentName.JOIN) {
           activity.setFragment(FragmentName.DISCONNECT);
       }else if(currentFragment == FragmentName.DISCONNECT) {
           activity.setFragment(FragmentName.START);
           activity.disconnect();
       }else if(currentFragment == FragmentName.REQUEST){
           activity.setFragment(FragmentName.JOIN);
       }else{
           activity.finish();
       }
    }
    public void recreateFragment(FragmentName name){
        switch (name){
                case START:
                    if(Utils.getCarMode()){
                        fragments[1] = new CarStartFragment();
                    }else {
                        fragments[0] = new StartFragment();
                    }
                    break;
                case JOIN:
                    ((JoinFragment)fragments[2]).closeDialog();
                    fragments[2] = new JoinFragment();
                    break;
                case CHAT:
                    fragments[3] = new ChatFragment();
                    break;
                case DISCONNECT:
                    fragments[4] = new DisconnectFragment();
                    break;
                case REQUEST:
                    fragments[5] = new RequestAssistanceFragment();
                default:
            }

        }
    }

