package se.chalmers.fleetspeak.core.command;

import se.chalmers.fleetspeak.core.TCPHandler;
import se.chalmers.fleetspeak.core.command.impl.CommandFactory;
import se.chalmers.fleetspeak.core.command.impl.CommandInfo;
import se.chalmers.fleetspeak.core.command.impl.ICommand;
import se.chalmers.fleetspeak.core.permission.PermissionLevel;
import se.chalmers.fleetspeak.core.permission.Permissions;
import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.eventbus.EventBusEvent;
import se.chalmers.fleetspeak.eventbus.IEventBusSubscriber;
import se.chalmers.fleetspeak.util.Log;


/**
 * A singleton class handling all the commands available on the server.
 * @author Patrik Haar
 * @revised Matz Larsson
 *
 */
public class Commands implements IEventBusSubscriber{

	private static Commands instance;
	
	private ICommand[] commands;
	private CommandInfo[] cmdInfos;
	
	private Commands() {
		commands = CommandFactory.createCommands();
		cmdInfos = new CommandInfo[commands.length];
		for (int i=0; i<commands.length; i++) {
			cmdInfos[i] = commands[i].getInfo();
		}

		EventBus.getInstance().addSubscriber(this);
		Permissions.addUserLevel(-1, PermissionLevel.ADMIN_ALL);		//FIXME only for debugging
	}
	
	public static void initialize(){
		Commands.getInstance();
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
		try{
			if(commands[cmdInfo.getExecCode()].execute(requester, params)){
				return true;
			}else{
				Log.logError("You do not have permission to do this");
			}
		}catch(InvalidCommandArgumentsException icae){
			Log.logError(icae.getMessage());
		}

		return false;
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
	 * Handles all calls that is received via the EventBus
	 */
	@Override
	public void eventPerformed(EventBusEvent event) {
		if(event.getReciever().equals("CommandHandler")){
			String name = getRealCommand(event.getCommand().getCommand());
			CommandInfo info = findCommand(name);
			if(info != null){
				int requester = -1;
				if(event.getActor() instanceof TCPHandler){
					requester = ((TCPHandler)event.getActor()).getClientID();
				}
				
				execute(requester, info, event.getCommand().getKey(), event.getCommand().getValue());
			}else{
				Log.logDebug("Command not found: "+name);
			}
		}else{
			System.out.println("Does not handle cmd to "+event.getReciever()+": "+event.getCommand().getCommand());
		}
	}
	
	/**
	 * Fix command names that are differently named on server/client
	 * @param cmd The command to fix
	 * @return The server command name of the given command
	 */
	private String getRealCommand(String cmd){
		if(cmd.equals("setName")){
			return "setUsername";
		}else if(cmd.equals("createAndMove")){
			return "moveUserToNewRoom";
		}else if(cmd.equals("getUsers")){
			return "broadcastUsers";
		}
		
		return cmd;
	}
	
	/**
	 * Restores the Commands class to its closed state and resets the instance
	 */
	public static void terminate(){
		if(instance != null){
			EventBus.getInstance().removeSubscriber(instance);
			instance = null;
		}
	}
}
