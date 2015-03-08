package se.chalmers.fleetspeak.core.command;

import se.chalmers.fleetspeak.core.command.impl.CommandFactory;
import se.chalmers.fleetspeak.core.command.impl.CommandInfo;
import se.chalmers.fleetspeak.core.command.impl.CommandResponse;
import se.chalmers.fleetspeak.core.command.impl.ICommand;
import se.chalmers.fleetspeak.core.permission.PermissionLevel;
import se.chalmers.fleetspeak.core.permission.Permissions;


/**
 * A singleton class handling all the commands available on the server.
 * @author Patrik Haar
 * @revised Matz Larsson
 *
 */
public class Commands{

	private static Commands instance;
	
	private ICommand[] commands;
	private CommandInfo[] cmdInfos;
	
	private Commands() {
		commands = CommandFactory.createCommands();
		cmdInfos = new CommandInfo[commands.length];
		for (int i=0; i<commands.length; i++) {
			cmdInfos[i] = commands[i].getInfo();
		}

		Permissions.addUserLevel(-1, PermissionLevel.ADMIN_ALL);		//FIXME only for debugging
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
	public CommandResponse execute(int requester, CommandInfo cmdInfo, Object... params) {
		if(cmdInfo == null){
			return null;
		}
		
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
			if (ci.getName().equalsIgnoreCase(cmdName)) {
				return ci;
			}
		}
		return null;
	}
	
	/**
	 * Restores the Commands class to its closed state and resets the instance
	 */
	public static void terminate(){
		if(instance != null){
			instance = null;
		}
	}
}
