package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.RoomHandler;
import se.chalmers.fleetspeak.core.permission.Permission;
import se.chalmers.fleetspeak.core.permission.Permissions;

public class CreateRoom extends BasicCommand {

	public CreateRoom(int exCode) {
		super(new CommandInfo("CreateRoom", "CreateRoom [name]", "Creates a new room with given name", exCode));
	}

	@Override
	public CommandResponse execute(int requester, Object... params) {
		try{
			int roomID = params[0].getClass()==Integer.class ? (Integer)params[0] : Integer.parseInt((String)params[0]);
			String name = (String)params[1];
			if(Permissions.isAllowed(requester, Permission.RENAME_ROOM)){
				if(RoomHandler.getInstance().setRoomName(roomID, name)){
					return new CommandResponse(true, "Renamed room to '"+name+"'");
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
