package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.Client;
import se.chalmers.fleetspeak.core.Room;
import se.chalmers.fleetspeak.core.RoomHandler;
import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.eventbus.EventBusEvent;
import se.chalmers.fleetspeak.util.Command;

public class BroadcastUsers extends BasicCommand{

	public BroadcastUsers(int execCode){
		super(new CommandInfo("BroadcastUsers", "BroadcastUsers", "Broadcasts the users via the event bus", execCode));
	}
	
	@Override
	public boolean execute(int requester, Object... params){
		EventBus bus = EventBus.getInstance();
		for(Room r: RoomHandler.getInstance().getRooms()){
			for( Client c : RoomHandler.getInstance().getClients(r)){
				bus.fireEvent(new EventBusEvent("broadcast", new Command("addUser", c.getName()+ "," +c.getClientID(), r.getName() +"," + r.getId()), params.length>0?params[0]:null));
			}
		}
		
		return true;
	}

}
