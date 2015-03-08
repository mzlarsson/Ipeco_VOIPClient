package se.chalmers.fleetspeak.core.command.impl;


/**
 * An interface for executable commands.
 * @author Patrik Haar
 *
 */
public interface ICommand {

	/**
	 * Executes the command using the given parameters and returns true if
	 * the command was successfully executed.
	 * @param requester The id of the one requesting the command, used for permission checks.
	 * @param params The parameters sent to the command.
	 * @return A response with data about the outcome
	 */
	public CommandResponse execute(int requester, Object... params);
	
	/**
	 * Gets the info-object holding the information about the command.
	 * @return the info-object for the command.
	 */
	public CommandInfo getInfo();
}
