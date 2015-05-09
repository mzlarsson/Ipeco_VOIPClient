package se.chalmers.fleetspeak.tcp;

import se.chalmers.fleetspeak.util.Command;

@FunctionalInterface
public interface CommandHandler {
	public void handleCommand(Command command);
}
