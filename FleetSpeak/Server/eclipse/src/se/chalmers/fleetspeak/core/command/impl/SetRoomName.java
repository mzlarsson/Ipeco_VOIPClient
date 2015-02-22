package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.RoomHandler;
import se.chalmers.fleetspeak.core.command.InvalidCommandArgumentsException;
import se.chalmers.fleetspeak.core.permission.Permission;
import se.chalmers.fleetspeak.core.permission.Permissions;


public class SetRoomName extends BasicCommand{

	public SetRoomName(int execCode){
		super(new CommandInfo("SetRoomName", "SetRoomName [room-id] [room-name]", "Sets the name of the room with the given ID.", execCode));
	}
	
	@Override
	public boolean execute(int requester, Object... params) throws InvalidCommandArgumentsException{
		try{
			if(Permissions.isAllowed(requester, Permission.RENAME_ROOM)){
				int roomID = (params[0].getClass()==Integer.class||params[0].getClass()==int.class?(Integer)params[0]:Integer.parseInt((String)params[0]));
				String roomName = (String)params[1];
				RoomHandler.getInstance().setRoomName(roomID, roomName);
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
