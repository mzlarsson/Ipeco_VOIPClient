package se.chalmers.fleetspeak.core;


/**
 * Interface for classes which are able to receive and handle commands.
 *
 * @author Patrik Haar
 */
public interface CommandHandler {

	/**
	 * Receive and handle the given Command.
	 * @param command The Command to be handled.
	 * @param sender The instance that sent the command.
	 */
	public void handleCommand(String command, Object sender);
}
