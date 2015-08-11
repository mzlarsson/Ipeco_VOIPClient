package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.RoomHandler;
import se.chalmers.fleetspeak.core.permission.PermissionType;
import se.chalmers.fleetspeak.core.permission.Permissions;


public class SetUsername extends BasicCommand{

	public SetUsername(int execCode){
		super(new CommandInfo("SetUsername", "SetUsername [user-id] [user-name]", "Sets the name of the user with the given ID", execCode));
	}
	
	@Override
	public CommandResponse execute(int requester, Object... params){
		try{
			int userID = (params[0].getClass()==Integer.class||params[0].getClass()==int.class?(Integer)params[0]:Integer.parseInt((String)params[0]));
			String username = (String)params[1];
			if((userID == requester && Permissions.isAllowed(requester, PermissionType.RENAME_OWN_USER)) ||
			  (userID != requester && Permissions.isAllowed(requester, PermissionType.RENAME_OTHER_USER))){
				if(RoomHandler.getInstance().setUsername(userID, username)){
					return new CommandResponse(true, "Renamed user to '"+username+"'");
				}else{
					return new CommandResponse(false, "Internal error occured. Failed to perform action.");
				}
			}else{
				return new CommandResponse(false, "Insuffient permissions. Action denied.");
			}
		}catch(NumberFormatException | NullPointerException | ClassCastException e){
			return new CommandResponse(false, "Invalid command use: '"+getInfo().getFormat()+"'");
		}
	}

}
