package se.chalmers.fleetspeak.newgui.core;

import se.chalmers.fleetspeak.util.Command;

public interface CommandHandler {

	public void commandReceived(Command cmd);
	
}
