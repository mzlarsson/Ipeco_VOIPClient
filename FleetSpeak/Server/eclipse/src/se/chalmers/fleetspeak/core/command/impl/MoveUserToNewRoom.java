package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.Room;
import se.chalmers.fleetspeak.core.RoomHandler;
import se.chalmers.fleetspeak.core.permission.Permission;
import se.chalmers.fleetspeak.core.permission.Permissions;


public class MoveUserToNewRoom extends BasicCommand implements ICommand {
	
	public MoveUserToNewRoom(int execCode) {
		super(new CommandInfo("MoveUserToNewRoom",
							   "MoveUserToNewRoom [user-id] [room-name]",
							   "Moves user to a new room with the given name.",
							   execCode));
	}

	@Override
	public boolean execute(int requester, Object... params) {
		try{
			int userID = params[0].getClass()==Integer.class ? (Integer)params[0] : Integer.parseInt((String)params[0]);
			String roomName = (String)params[1];
			if((userID == requester && Permissions.isAllowed(requester, Permission.MOVE_OWN_USER)) ||
			  (userID != requester && Permissions.isAllowed(requester, Permission.MOVE_OTHER_USER))){
				RoomHandler.getInstance().moveClient(userID, new Room(roomName));
				return true;
			}
		}catch(NumberFormatException nfe){}
		
		return false;
	}

}
