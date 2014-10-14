package se.chalmers.fleetspeak.tcp;

import se.chalmers.fleetspeak.Client;
import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.RoomHandler;
import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.eventbus.EventBusEvent;
import se.chalmers.fleetspeak.eventbus.IEventBusSubscriber;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.Log;

public class CommandHandler implements IEventBusSubscriber {

	private RoomHandler roomHandler;

	private EventBus eventBus;

	public CommandHandler() {
		eventBus = EventBus.getInstance();
		eventBus.addSubscriber(this);
		roomHandler = new RoomHandler();
	}

	public void addClient(Client client) {
		roomHandler.addClient(client);
		eventBus.fireEvent(new EventBusEvent("broadcast", new Command("newUser", client.getClientID(),null), this));
	}

	public void terminate() {
		eventBus.removeSubscriber(this);
		roomHandler.terminate();
	}

	@Override
	public void eventPerformed(EventBusEvent event) {

		if (event.getReciever() == "CommandHandler") {
			Command command = event.getCommand();
			String commandName = command.getCommand();
			Log.log("[CommandHandler] Got a command \"" + commandName + "\"");
			// Called when a user is disconnected.
			if (commandName.equals("disconnect")) {
				int i = (Integer) event.getCommand().getKey();
				roomHandler.removeClient(i);
				eventBus.fireEvent(new EventBusEvent("broadcast", new Command(
						"userDisconnected", i, null), null));
			// TODO This is a test command!!!!
			} else if (commandName.equals("data")) {
				Log.log("[CommandHandler] Data");
				TCPHandler t = (TCPHandler) event.getActor();
				t.sendData(new Command("HurrDurr", null, null));
			// Called when a client changes name.
			} else if (commandName.equals("setName")) {
				Client c = roomHandler.getClient((Integer) command.getKey());
				c.setName((String) command.getValue());
				eventBus.fireEvent(new EventBusEvent("broadcast", command, null));
			// Called when a client changes rooms to a new room.
			} else if (commandName.equals("createAndMove")) {
				roomHandler.moveClient((Integer)command.getKey(), new Room((String)command.getValue()));
				eventBus.fireEvent(new EventBusEvent("broadcast", command, null));
				// Called when a client changes rooms to an existing room.
			} else if (commandName.equals("move")) {
				roomHandler.moveClient((Integer)command.getKey(), (Integer)command.getValue());
				eventBus.fireEvent(new EventBusEvent("broadcast", command, null));
			}
		}
	}

}
