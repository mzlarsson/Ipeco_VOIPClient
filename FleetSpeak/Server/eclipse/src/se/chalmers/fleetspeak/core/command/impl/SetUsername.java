package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.RoomHandler;
import se.chalmers.fleetspeak.core.command.InvalidCommandArgumentsException;
import se.chalmers.fleetspeak.core.permission.Permission;
import se.chalmers.fleetspeak.core.permission.Permissions;


public class SetUsername extends BasicCommand{

	public SetUsername(int execCode){
		super(new CommandInfo("SetUsername", "SetUsername [user-id] [user-name]", "Sets the name of the user with the given ID", execCode));
	}
	
	@Override
	public boolean execute(int requester, Object... params) throws InvalidCommandArgumentsException{
		try{
			int userID = (params[0].getClass()==Integer.class||params[0].getClass()==int.class?(Integer)params[0]:Integer.parseInt((String)params[0]));
			String username = (String)params[1];
			if((userID == requester && Permissions.isAllowed(requester, Permission.RENAME_OWN_USER)) ||
			  (userID != requester && Permissions.isAllowed(requester, Permission.RENAME_OTHER_USER))){
				RoomHandler.getInstance().setUsername(userID, username);
				return true;
			}
		}catch(NumberFormatException nfe){
			throw new InvalidCommandArgumentsException(getInfo());
		}catch(NullPointerException nfe){
			throw new InvalidCommandArgumentsException(getInfo());
		}
		
		return false;
	}

}
