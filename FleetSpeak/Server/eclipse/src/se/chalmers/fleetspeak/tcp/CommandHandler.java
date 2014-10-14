package se.chalmers.fleetspeak.tcp;

import se.chalmers.fleetspeak.Client;
import se.chalmers.fleetspeak.Command;
import se.chalmers.fleetspeak.RoomHandler;
import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.eventbus.EventBusEvent;
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
	public void eventPerformed(EventBusEvent event) {
		if(event.getReciever() == "disconnect"){
			int i = (Integer) event.getCommand().getKey();
			roomHandler.removeClient(i);
			eventBus.fireEvent(new EventBusEvent("broadcast", new Command("userDisconnected",i,null),null));
		}
		
	}
	
}
