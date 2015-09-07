package se.chalmers.fleetspeak.core;


public interface NetworkUser {

	public void sendCommand(String command);

	public void setCommandHandler(CommandHandler ch);
}
