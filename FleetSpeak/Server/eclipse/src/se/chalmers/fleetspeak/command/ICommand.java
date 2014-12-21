package se.chalmers.fleetspeak.command;

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
	 * @return true if successfully executed, false if not.
	 */
	public boolean execute(int requester, Object... params);
	
	/**
	 * Gets the info-object holding the information about the command.
	 * @return the info-object for the command.
	 */
	public CommandInfo getInfo();
	
	/**
	 * Add a CommandListener to this command, it will be called with all the 
	 * specific instructions needed to execute this command. 
	 * @param cl The CommandListener to be added.
	 */
	public void addCommandListener(CommandListener cl);
	
	/**
	 * Removes a CommandListener from this command, it will no longer be called
	 * with all the specific instructions needed to execute this command. 
	 * @param cl The CommandListener to be removed.
	 */
	public void removeCommandListener(CommandListener cl);
}
