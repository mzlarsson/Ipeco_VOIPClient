package se.chalmers.fleetspeak.core;

import se.chalmers.fleetspeak.util.Command;

/**
 * Interface for classes which are able to receive and handle commands.
 *
 * @author Patrik Haar
 */
public interface CommandHandler {
	
	/**
	 * Receive and handle the given Command.
	 * @param c The Command to be handled.
	 */
	public void handleCommand(Command c);
}
