package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.Client;
import se.chalmers.fleetspeak.core.Room;
import se.chalmers.fleetspeak.core.RoomHandler;
import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.eventbus.EventBusEvent;
import se.chalmers.fleetspeak.util.Command;

public class GetUsers extends BasicCommand {

	public GetUsers(int execCode) {
		super(new CommandInfo("GetUsers", "GetUsers", "Sends users to users", execCode));
	}

	@Override
	public CommandResponse execute(int requester, Object... params){
		RoomHandler handler = RoomHandler.getInstance();
		EventBus bus = EventBus.getInstance();
		for(Room r: handler.getRooms()){
			for( Client c : handler.getClients(r)){
				bus.fireEvent(new EventBusEvent("broadcast", new Command("addUser", c.getName()+ "," +c.getClientID(), r.getName() +"," + r.getId()), null));
			}
		}
		
		return new CommandResponse(true, "Requested broadcast");
	}

}
