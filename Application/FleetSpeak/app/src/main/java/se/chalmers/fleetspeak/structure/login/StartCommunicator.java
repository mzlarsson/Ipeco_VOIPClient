package se.chalmers.fleetspeak.structure.login;

/**
 * Created by David Gustafsson on 2015-07-21.
 */
public interface StartCommunicator {
    public void changeUsername(String a);
    public void changePassword(String b);
    public void saveUserSettings();
}