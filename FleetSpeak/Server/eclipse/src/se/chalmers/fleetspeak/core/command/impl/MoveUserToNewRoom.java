package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.Room;
import se.chalmers.fleetspeak.core.RoomHandler;
import se.chalmers.fleetspeak.core.command.Commands;
import se.chalmers.fleetspeak.core.permission.Permission;
import se.chalmers.fleetspeak.core.permission.Permissions;
import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.util.Command;


public class MoveUserToNewRoom extends BasicCommand{
	
	public MoveUserToNewRoom(int execCode) {
		super(new CommandInfo("MoveUserToNewRoom",
							   "MoveUserToNewRoom [user-id] [room-name]",
							   "Moves user to a new room with the given name.",
							   execCode));
	}

	@Override
	public CommandResponse execute(int requester, Object... params){
		try{
			int userID = params[0].getClass()==Integer.class ? (Integer)params[0] : Integer.parseInt((String)params[0]);
			String roomName = (String)params[1];
			if((userID == requester && Permissions.isAllowed(requester, Permission.MOVE_OWN_USER)) ||
			  (userID != requester && Permissions.isAllowed(requester, Permission.MOVE_OTHER_USER))){
				//Do the create and move
				Room room = new Room(roomName);
				RoomHandler.getInstance().moveClient(userID, room);
				//Announce to app users
				EventBus.postEvent("broadcast", new Command("createAndMove", userID, room.getName() + "," + room.getId()), null);
				//Announce change
				Commands cmds = Commands.getInstance();
				cmds.execute(requester, cmds.findCommand("GetUsers"));
				return new CommandResponse(true, "Created room and moved user");
			}else{
				return new CommandResponse(false, "Insuffient permissions. Action denied.");
			}
		}catch(NumberFormatException | NullPointerException e){
			return new CommandResponse(false, "Invalid command use: '"+getInfo().getFormat()+"'");
		}
	}

}
