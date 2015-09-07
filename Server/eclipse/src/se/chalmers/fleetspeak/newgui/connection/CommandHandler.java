package se.chalmers.fleetspeak.newgui.connection;

import se.chalmers.fleetspeak.util.Command;

public interface CommandHandler {

	public void commandReceived(Command cmd);
	
}
