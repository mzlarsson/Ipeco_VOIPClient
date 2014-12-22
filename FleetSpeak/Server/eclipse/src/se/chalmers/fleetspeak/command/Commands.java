package se.chalmers.fleetspeak.command;

/**
 * A singleton class handling all the commands available on the server.
 * @author Patrik Haar
 *
 */
public class Commands {

	private static Commands instance;
	
	private ICommand[] commands;
	private CommandInfo[] cmdInfos;
	
	private Commands() {
		commands = null; //TODO Unimplemented, need to check what commands are available on the file-level.
		cmdInfos = new CommandInfo[commands.length];
		for (int i=0; i<commands.length; i++) {
			cmdInfos[i] = commands[i].getInfo();
		}
	}
	
	/**
	 * Returns the singleton instance of the Commands-class.
	 * @return the singleton-instance.
	 */
	public static Commands getInstance(){
		if (instance == null) {
			instance = new Commands();
		}
		return instance;
	}
	
	/**
	 * Execute the command corresponding to the given CommandInfo with the given parameters.
	 * @param requester The id of the one requesting the command, used for permission checks.
	 * @param cmdInfo The CommandInfo of the command to be executed.
	 * @param params The parameters to the command in question.
	 * @return true if successfully executed, false if denied of failed.
	 */
	public boolean execute(int requester, CommandInfo cmdInfo, Object... params) {
		return commands[cmdInfo.getExecCode()].execute(requester, params);
	}
	
	
	/**
	 * Returns the info for all available commands.
	 * @return an array with the info of all available commands.
	 */
	public CommandInfo[] getCommands() {
		return cmdInfos.clone();
	}
	
	/**
	 * Finds and returns the CommandInfo-object which name matches the one given. Returns
	 * null if no such command was found.
	 * @param cmdName The name of the command searched for.
	 * @return The CommandInfo-object if found, null otherwise.
	 */
	public CommandInfo findCommand(String cmdName) {
		for (CommandInfo ci : cmdInfos) {
			if (ci.getName().equals(cmdName)) {
				return ci;
			}
		}
		return null;
	}
	
	/**
	 * Add a CommandListener to all commands, it will be called with all the 
	 * specific instructions needed to execute the called commands. 
	 * @param cl The CommandListener to be added.
	 */
	public void addCommandListener(CommandListener cl) {
		for (ICommand c : commands) {
			c.addCommandListener(cl);
		}
	}
	
	/**
	 * Removes a CommandListener from all commands, it will no longer be called
	 * with all the specific instructions needed to execute the called commands. 
	 * @param cl The CommandListener to be removed.
	 */
	public void removeCommandListener(CommandListener cl) {
		for (ICommand c : commands) {
			c.removeCommandListener(cl);
		}
	}
}
