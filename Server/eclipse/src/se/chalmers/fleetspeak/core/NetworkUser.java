package se.chalmers.fleetspeak.core;

import java.io.IOException;


public interface NetworkUser {

	public void sendCommand(String command) throws IOException;

	public void setCommandHandler(CommandHandler ch);
}
