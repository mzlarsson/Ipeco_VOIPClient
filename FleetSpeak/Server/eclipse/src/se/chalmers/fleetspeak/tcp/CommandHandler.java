package se.chalmers.fleetspeak.tcp;

import se.chalmers.fleetspeak.Client;
import se.chalmers.fleetspeak.Command;
import se.chalmers.fleetspeak.RoomHandler;
import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.eventbus.IEventBusSubscriber;

public class CommandHandler implements IEventBusSubscriber{

	private RoomHandler roomHandler;
	
	private EventBus eventBus;
	
	public CommandHandler(){
		eventBus = EventBus.getInstance();
		eventBus.addSubscriber(this);
		roomHandler = new RoomHandler();
	}
	
	public void addClient(Client client){
		roomHandler.addClient(client);
	}
	
	
	@Override
	public void eventPerformed(Command command) {
		if(command.getCommand() == "disconnect"){
			int i = (Integer) command.getKey();
			roomHandler.removeClient(i);
			eventBus.fireEvent(new Command("broadcastUserDisconnected",i,null));
		}
		
	}
	
}
