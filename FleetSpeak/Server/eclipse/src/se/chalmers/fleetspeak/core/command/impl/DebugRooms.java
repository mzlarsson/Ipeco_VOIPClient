package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.Client;
import se.chalmers.fleetspeak.core.Room;
import se.chalmers.fleetspeak.core.RoomHandler;
import se.chalmers.fleetspeak.util.Log;

public class DebugRooms extends BasicCommand{
	
	public DebugRooms(int execCode){
		super(new CommandInfo("DebugRooms", "DebugRooms", "Shows all room content so you can debug it", execCode));
	}

	@Override
	public CommandResponse execute(int requester, Object... params){
		RoomHandler handler = RoomHandler.getInstance();
		for(Room room : handler.getRooms()){
			Log.log("\t"+room.getName()+" ["+room.getId()+"]");
			boolean empty = true;
			for(Client client : handler.getClients(room)){
				Log.log("\t\t"+client.getName()+" ["+client.getClientID()+"]");
				empty = false;
			}
			
			if(empty){
				Log.log("\t\t-- empty --");
			}
		}
		return new CommandResponse(true, "Printed the room tree");
	}

}
