package se.chalmers.fleetspeak.core;

import se.chalmers.fleetspeak.network.udp.RTPHandler;
import se.chalmers.fleetspeak.util.Command;

public interface NetworkUser {

	public void sendCommand(Command c);
	
	public void setCommandHandler(CommandHandler ch);
}
