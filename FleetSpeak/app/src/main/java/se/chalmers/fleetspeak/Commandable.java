package se.chalmers.fleetspeak;

/**
 * Created by Nieo on 20/10/14.
 * Can take commands from CommandHandler
 */
public interface Commandable {

    public void onDataUpdate(String command);

}
