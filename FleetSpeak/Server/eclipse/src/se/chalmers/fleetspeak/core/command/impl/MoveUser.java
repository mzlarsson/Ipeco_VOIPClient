package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.RoomHandler;
import se.chalmers.fleetspeak.core.permission.PermissionType;
import se.chalmers.fleetspeak.core.permission.Permissions;


/**
 * Moves an existing user to an existing room.
 * Parameters:	1. The ID of the user to be moved.
 * 				2. The ID of the targeted room.
 * @author Patrik Haar
 *
 */
class MoveUser extends BasicCommand{
	
	/**
	 * Constructor for the command, creates the relevant
	 * info-object describing this command.
	 * @param exCode The execution-code used to locate this command.
	 */
	public MoveUser(int exCode) {
		super(new CommandInfo("MoveUser", "MoveUser [user-id] [target-room-id]", "Moves the user as given by the arguments", exCode));
	}	
	
	@Override
	public CommandResponse execute(int requester, Object... params){
		try{
			int userID = params[0].getClass()==Integer.class ? (Integer)params[0] : Integer.parseInt((String)params[0]);
			int roomID = params[1].getClass()==Integer.class ? (Integer)params[1] : Integer.parseInt((String)params[1]);
			if((userID == requester && Permissions.isAllowed(requester, PermissionType.MOVE_OWN_USER)) ||
			  (userID != requester && Permissions.isAllowed(requester, PermissionType.MOVE_OTHER_USER))){
				if(RoomHandler.getInstance().moveClient(userID, roomID)){
					return new CommandResponse(true, "Moved user to room");
				}else{
					return new CommandResponse(false, "Internal error occured. Failed to perform action.");
				}
			}else{
				return new CommandResponse(false, "Insuffient permissions. Action denied.");
			}
		}catch(NumberFormatException | NullPointerException e){
			e.printStackTrace();
			return new CommandResponse(false, "Invalid command use: '"+getInfo().getFormat()+"'");
		}
	}

}
