package se.chalmers.fleetspeak.core.command.impl;

import java.util.logging.Logger;

public class DebugRooms extends BasicCommand{

	private Logger logger = Logger.getLogger("Debug");

	public DebugRooms(int execCode){
		super(new CommandInfo("DebugRooms", "DebugRooms", "Shows all room content so you can debug it", execCode));
	}

	@Override
	public CommandResponse execute(int requester, Object... params){
		throw new IllegalStateException("Command debugRooms is broken.");
		
//		RoomHandler handler = RoomHandler.getInstance();
//		for(Room room : handler.getRooms()){
//			logger.log(Level.FINE, "\t" + room.getName() + " [" + room.getId() + "]");
//			boolean empty = true;
//			for(Client client : handler.getClients(room)){
//				logger.log(Level.FINE,"\t\t"+client.getName()+" ["+client.getClientID()+"]");
//				empty = false;
//			}
//			
//			if(empty){
//				logger.log(Level.FINE,"\t\t-- empty --");
//			}
//		}
//		return new CommandResponse(true, "Printed the room tree");
	}

}
