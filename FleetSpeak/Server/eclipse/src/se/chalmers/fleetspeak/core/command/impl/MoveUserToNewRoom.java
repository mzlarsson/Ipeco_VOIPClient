package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.Room;
import se.chalmers.fleetspeak.core.RoomHandler;
import se.chalmers.fleetspeak.core.command.Commands;
import se.chalmers.fleetspeak.core.command.InvalidCommandArgumentsException;
import se.chalmers.fleetspeak.core.permission.Permission;
import se.chalmers.fleetspeak.core.permission.Permissions;
import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.eventbus.EventBusEvent;
import se.chalmers.fleetspeak.util.Command;


public class MoveUserToNewRoom extends BasicCommand{
	
	public MoveUserToNewRoom(int execCode) {
		super(new CommandInfo("MoveUserToNewRoom",
							   "MoveUserToNewRoom [user-id] [room-name]",
							   "Moves user to a new room with the given name.",
							   execCode));
	}

	@Override
	public boolean execute(int requester, Object... params) throws InvalidCommandArgumentsException{
		System.out.println("In MUTNR");
		try{
			int userID = params[0].getClass()==Integer.class ? (Integer)params[0] : Integer.parseInt((String)params[0]);
			String roomName = (String)params[1];
			System.out.println("Parameters: userID="+userID+" roomName="+roomName);
			if((userID == requester && Permissions.isAllowed(requester, Permission.MOVE_OWN_USER)) ||
			  (userID != requester && Permissions.isAllowed(requester, Permission.MOVE_OTHER_USER))){
				//Do the create and move
				Room room = new Room(roomName);
				RoomHandler.getInstance().moveClient(userID, room);
				//Announce to app users
				EventBusEvent event = new EventBusEvent("broadcast", new Command("createAndMove", userID, room.getName() + "," + room.getId()), null);
				EventBus.getInstance().fireEvent(event);
				//Announce change
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
