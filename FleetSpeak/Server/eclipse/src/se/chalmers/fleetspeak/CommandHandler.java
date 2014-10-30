package se.chalmers.fleetspeak;

import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.eventbus.EventBusEvent;
import se.chalmers.fleetspeak.eventbus.IEventBusSubscriber;
import se.chalmers.fleetspeak.tcp.TCPHandler;
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
	
	private void wrongFormat(ServerCommand sc) {
		Log.log("Wrong format:\n\t" + sc.getInfo());
	}
	
	private void serverCommands(String cmdString, Object actor) {
		// Called to initiate the rtp sound transfer.
		if (cmdString.startsWith(ServerCommand.SET_RTP_PORT.getName()+" ")) {
			String[] data = cmdString.split(" ");
			int clientID = -1, clientPort = -1;
			try {
				clientID = Integer.parseInt(data[1]);
				clientPort = Integer.parseInt(data[2]);
				Log.log("Starting RTP with port: "+clientPort);
				Client c = roomHandler.findClient((Integer) clientID);
				c.startRTPTransfer(clientPort);
			} catch (NumberFormatException ex) {
				wrongFormat(ServerCommand.SET_RTP_PORT);
			} catch (ArrayIndexOutOfBoundsException ex) {
				wrongFormat(ServerCommand.SET_RTP_PORT);
			}
		// Called to clear the server console window.
		} else if (cmdString.equals(ServerCommand.CLEAR.getName())) {
			Log.flushLog();
		// Called to move a user from a room to an existing room.
		} else if(cmdString.startsWith(ServerCommand.MOVE_USER.getName()+" ")){
			String[] data = cmdString.split(" ");
			int clientID = -1, roomID = -1;
			try {
				clientID = Integer.parseInt(data[1]);
				roomID = Integer.parseInt(data[2]);
				roomHandler.moveClient(clientID, roomID);
				eventBus.fireEvent(new EventBusEvent("broadcast", new Command("move", clientID, roomID), null));
			} catch (NumberFormatException ex) {
				wrongFormat(ServerCommand.MOVE_USER);
			} catch (ArrayIndexOutOfBoundsException ex) {
				wrongFormat(ServerCommand.MOVE_USER);
			}
		//	Called to move a user from a room to a new room.
		} else if(cmdString.startsWith(ServerCommand.MOVE_USER_NEW_ROOM.getName()+" ")){
			String[] data = cmdString.split(" ");
			int clientID = -1;
			try {
				clientID = Integer.parseInt(data[1]);
				String roomName = data[2];
				Room r = new Room(roomName);
				roomHandler.moveClient(clientID, r);
				eventBus.fireEvent(new EventBusEvent("broadcast", new Command("createAndMove", clientID, r.getName()+","+r.getId()), null));
			} catch (NumberFormatException ex) {
				wrongFormat(ServerCommand.MOVE_USER_NEW_ROOM);
			} catch (ArrayIndexOutOfBoundsException ex) {
				wrongFormat(ServerCommand.MOVE_USER_NEW_ROOM);
			}
		//	Called to change the name of a room.
		} else if(cmdString.startsWith(ServerCommand.KICK.getName()+"")){
			String[] data = cmdString.split(" ");
			int clientID = Integer.parseInt(data[1]);
			eventBus.fireEvent(new EventBusEvent("CommandHandler", new Command("disconnect", clientID, null), this));
		} else if(cmdString.startsWith(ServerCommand.SET_ROOM_NAME.getName()+" ")){
			String[] data = cmdString.split(" ");
			int roomID = -1;
			try {
				roomID = Integer.parseInt(data[1]);
				String roomName = data[2];
				roomHandler.setRoomName(roomID, roomName);
				eventBus.fireEvent(new EventBusEvent("broadcast", new Command("setRoomName", roomID, roomName), null));
			} catch (NumberFormatException ex) {
				wrongFormat(ServerCommand.SET_ROOM_NAME);
			} catch (ArrayIndexOutOfBoundsException ex) {
				wrongFormat(ServerCommand.SET_ROOM_NAME);
			}
		//	Called to change the name of a user.
		} else if(cmdString.startsWith(ServerCommand.SET_USER_NAME.getName()+" ")){
			String[] data = cmdString.split(" ");
			int userID = -1;
			try {
				userID = Integer.parseInt(data[1]);
				String Username = data[2];
				roomHandler.setUsername(userID, Username);
				eventBus.fireEvent(new EventBusEvent("broadcast", new Command("setName", userID, Username), null));
			} catch (NumberFormatException ex) {
				wrongFormat(ServerCommand.SET_USER_NAME);
			} catch (ArrayIndexOutOfBoundsException ex) {
				wrongFormat(ServerCommand.SET_USER_NAME);
			}
		//	Called to close the server.
		} else if (cmdString.equals(ServerCommand.CLOSE.getName())) {
			if (actor.getClass()==ServerGUI.class) {
				((ServerGUI)actor).stop();
			}
		//	Called in dire situations.
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
		// Called to get all the information regarding rooms and users.
		} else if (cmdString.equals(ServerCommand.ROOM_INFO.getName())) {
			Log.log(roomHandler.getRoomInfo());
		} else {
			Log.log(("<error>ERROR:</error> \"<b>" + cmdString + "</b>\" <error>is not supported</error>"));
		}
	}

	@Override
	public void eventPerformed(EventBusEvent event) {

		if (event.getReciever().equals("CommandHandler")) {
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
				roomHandler.setUsername((Integer)command.getKey(), (String)command.getValue());
				eventBus.fireEvent(new EventBusEvent("broadcast", command, null));
			// Called when a client changes rooms to a new room.
			} else if (commandName.equals("createAndMove")) {
				Room newRoom = new Room((String)command.getValue());
				roomHandler.moveClient((Integer)command.getKey(), newRoom);
				eventBus.fireEvent(new EventBusEvent("broadcast", new Command("createAndMove", command.getKey(), newRoom.getName() + "," + newRoom.getId()), null));
			// Called when a client changes rooms to an existing room.
			} else if (commandName.equals("moveUser")) {
				roomHandler.moveClient((Integer)command.getKey(), (Integer)command.getValue());
				eventBus.fireEvent(new EventBusEvent("broadcast", command, null));
			// Called when commands are manually entered into the command line.
			} else if (commandName.equals("mute")) {
				Log.logDebug("Got mute command");
				Client muter = roomHandler.findClient((Integer)command.getKey());
				Client muteObject = roomHandler.findClient((Integer)command.getValue());
				muter.setMuted(muteObject, true);
				//Called to unmute user
			} else if (commandName.equals("unmute")) {
				Log.logDebug("Got unmute command");
				Client muter = roomHandler.findClient((Integer)command.getKey());
				Client muteObject = roomHandler.findClient((Integer)command.getValue());
				muter.setMuted(muteObject, false);
				// Called when commands are manually entered into the command line.
			} else if (commandName.equals("consoleCommand")) {
				serverCommands((String)command.getValue(), event.getActor());
			} else if(commandName.equals("getUsers")){
				for(Room r: roomHandler.getRooms()){
					for( Client c : roomHandler.getClients(r)){
						eventBus.fireEvent(new EventBusEvent("broadcast", new Command("addUser", c.getName()+ "," +c.getClientID(), r.getName() +"," + r.getId()), event.getActor()));
					}
				}
			}else if(commandName.equals("setRtpPort")){
				Client c = roomHandler.findClient((Integer)command.getKey());
				c.startRTPTransfer((Integer)command.getValue());
			}else{
				Log.logError("Got unrecognized command: <i>"+command.getCommand()+"</i>");
			}
		}
	}

}
