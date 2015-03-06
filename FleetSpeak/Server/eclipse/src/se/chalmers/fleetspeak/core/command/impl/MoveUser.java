package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.RoomHandler;
import se.chalmers.fleetspeak.core.command.Commands;
import se.chalmers.fleetspeak.core.command.InvalidCommandArgumentsException;
import se.chalmers.fleetspeak.core.permission.Permission;
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
		super(new CommandInfo("MoveUser",
								"MoveUser [user-id] [target-room-id]",
								"Moves the user with the given user-ID to"
								+ "the room with the given room-ID",
								exCode));
	}	
	
	@Override
	public boolean execute(int requester, Object... params) throws InvalidCommandArgumentsException{
		try{
			int userID = params[0].getClass()==Integer.class ? (Integer)params[0] : Integer.parseInt((String)params[0]);
			int roomID = params[1].getClass()==Integer.class ? (Integer)params[1] : Integer.parseInt((String)params[1]);
			if((userID == requester && Permissions.isAllowed(requester, Permission.MOVE_OWN_USER)) ||
			  (userID != requester && Permissions.isAllowed(requester, Permission.MOVE_OTHER_USER))){
				RoomHandler.getInstance().moveClient(userID, roomID);
				Commands cmds = Commands.getInstance();
				cmds.execute(requester, cmds.findCommand("GetUsers"));
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