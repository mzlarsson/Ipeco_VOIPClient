package se.chalmers.fleetspeak.core.command;

/**
 * 
 * @author Patrik Haar
 *
 */
public interface CommandListener {

	public boolean moveUser(int requester, int userID, int roomID);
	public boolean removeUser();
	public boolean setUsername();
}
