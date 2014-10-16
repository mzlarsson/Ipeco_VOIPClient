package se.chalmers.fleetspeak.tcp;

import se.chalmers.fleetspeak.Client;
import se.chalmers.fleetspeak.Room;
import se.chalmers.fleetspeak.RoomHandler;
import se.chalmers.fleetspeak.ServerCommand;
import se.chalmers.fleetspeak.ServerGUI;
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
		eventBus.fireEvent(new EventBusEvent("broadcast", new Command("newUser", client.getClientID(),null), null));
	}

	public void terminate() {
		eventBus.removeSubscriber(this);
		roomHandler.terminate();
	}
	
	private void serverCommands(String cmdString, Object actor) {
		// Called to initiate the rtp sound transfer.
		if (cmdString.startsWith(ServerCommand.SET_RTP_PORT.getName())) {
			String[] data = cmdString.split(" ");
			int clientID = Integer.parseInt(data[1]);
			int clientPort = Integer.parseInt(data[2]);
			Log.log("Starting RTP with port: "+clientPort);
			Client c = roomHandler.getClient((Integer) clientID);
			c.startRTPTransfer(clientPort);
		// Called to clear the server console window.
		} else if (cmdString.startsWith(ServerCommand.CLEAR.getName())) {
			Log.flushLog();
		} else if(cmdString.startsWith(ServerCommand.MOVE_USER.getName())){
			String[] data = cmdString.split(" ");
			int clientID = Integer.parseInt(data[1]);
			int roomID = Integer.parseInt(data[2]);
			roomHandler.moveClient(clientID, roomID);
		} else if(cmdString.startsWith(ServerCommand.MOVE_USER_NEW_ROOM.getName())){
			String[] data = cmdString.split(" ");
			int clientID = Integer.parseInt(data[1]);
			String roomName = data[2];
			roomHandler.moveClient(clientID, new Room(roomName));
		} else if (cmdString.startsWith(ServerCommand.CLOSE.getName())) {
			if (actor.getClass()==ServerGUI.class) {
				((ServerGUI)actor).stop();
			}
		} else if (cmdString.startsWith(ServerCommand.HELP.getName())) {
			if (cmdString.length()>5) {
				ServerCommand sc = ServerCommand.getCommand(cmdString.substring(5));
				if (sc!=null) {
					Log.log(sc.getInfo());
				} else {
					Log.log("The command \"<b>" + cmdString.substring(5) + "</b>\" doesn't exist. Try \"<b>help</b>\" to see possible commands.");
				}
			} else {
				Log.log("Possible commands are:");
				for (ServerCommand cmd: ServerCommand.values()) {
					Log.log("\t"+cmd.getName());
				}
				Log.log(ServerCommand.HELP.getInfo());
			}
		} else if (cmdString.startsWith(ServerCommand.ROOM_INFO.getName())) {
			Log.log(roomHandler.getRoomInfo());
		} else {
			Log.log(("<error>ERROR:</error> \"<b>" + cmdString + "</b>\" <error>is not supported</error>"));
		}
	}

	@Override
	public void eventPerformed(EventBusEvent event) {

		if (event.getReciever() == "CommandHandler") {
			Command command = event.getCommand();
			String commandName = command.getCommand();
			// Called when a user is disconnected.
			if (commandName.equals("disconnect")) {
				int i = (Integer) event.getCommand().getKey();
				roomHandler.removeClient(i, true);
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
			// Called when commands are manually entered into the command line.
			} else if (commandName.equals("consoleCommand")) {
				serverCommands((String)command.getValue(), event.getActor());
			}else if(commandName.equals("getRooms")){
				for(Room r: roomHandler.getRooms()){
					for( Client c : roomHandler.getClients(r)){
						eventBus.fireEvent(new EventBusEvent("broadcast", new Command("addUser", c.getClientID(),c.getName()), event.getActor()));
					}
				}
			}else if(commandName.equals("setRtpPort")){
				Client c = roomHandler.getClient((Integer)command.getKey());
				c.startRTPTransfer((Integer)command.getValue());
			}
		}
	}

}
