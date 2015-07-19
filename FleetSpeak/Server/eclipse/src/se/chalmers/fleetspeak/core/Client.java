package se.chalmers.fleetspeak.core;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import se.chalmers.fleetspeak.core.command.Commands;
import se.chalmers.fleetspeak.core.command.impl.CommandInfo;
import se.chalmers.fleetspeak.core.command.impl.CommandResponse;
import se.chalmers.fleetspeak.sound.Router;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.IDFactory;
import se.chalmers.fleetspeak.util.Log2;
import se.chalmers.fleetspeak.util.UserInfoPacket;

/**
 * A class that handles all connectors with the app
 * @author Nieo
 * @author Patrik Haar
 */


public class Client implements CommandHandler {

	private String alias;
	private TCPHandler tcp;
	private int clientID;
	
	private InetAddress ip;	//TODO Is it necessary for the client to hold it IP?
	private Router soundRouter;

	private Map<String, CommandInfo> cmds;
	
	/**
	 * Creates a client with the functionality for sending and receiving
	 * commands and sound-streams.
	 * @param socket The socket for the tcp-connection to this client.
	 */
	public Client(int id, String alias, InetAddress ip, TCPHandler tcph) {
		this.clientID = id;
		this.alias = alias;
		this.ip = ip;
		this.tcp = tcph;
		this.tcp.setCommandHandler(this);
		this.tcp.sendData(new Command("setInfo", getInfoPacket(), null));

		this.soundRouter = new Router();
		this.tcp.sendData(new Command("useSoundPort", soundRouter.getReceivePort(), null));
		soundRouter.start();
		
		initializeAndroidCommands();
	}
	
	/**
	 * Starts the the TCPHandler
	 */
	public void start(){
		this.tcp.syncToClient();
		
	}
	
	//FIXME temporary
	public void sendToPort(Client c, int port){
		tcp.sendData(new Command("sendStuffTo", c.ip.getHostAddress(), port));
	}
	
	/**
	 * Sets up the HashMap with the commands from the Android.
	 */
	private void initializeAndroidCommands() {
		cmds = new HashMap<String, CommandInfo>();
		cmds.put("setUsername", Commands.getInstance().findCommand("setUsername"));
		cmds.put("setSoundPort", Commands.getInstance().findCommand("setSoundPort"));
		cmds.put("moveUser", Commands.getInstance().findCommand("moveUser"));
		cmds.put("createRoom", Commands.getInstance().findCommand("createRoom"));
		cmds.put("disconnect", Commands.getInstance().findCommand("disconnect"));
	}
	
	/**
	 * Translates the command from the client.
	 * @param c The command which to be executed.
	 */
	private void runAndroidCommand(Command c){

		//Do translation Android --> Server according to spec
		switch(c.getCommand().toLowerCase()){
			case "setname":
				doCommand(cmds.get("setUsername"), clientID, c.getKey());
				break;
			case "setsoundport":
				doCommand(cmds.get("setSoundPort"), clientID, c.getKey()+","+c.getValue());
				//FIXME temporary
				RoomHandler handler = RoomHandler.getInstance();
				handler.findClient((int)c.getKey()).sendToPort(handler.findClient(clientID), (int)c.getValue());break;
			case "move":
				doCommand(cmds.get("moveUser"), clientID, c.getKey());
				break;
			case "movenewroom":
				Object[] data = doCommand(cmds.get("createRoom"), c.getKey(), null);
				int roomID = (data!=null&&data.length>0?(Integer)data[0]:-1);
				doCommand(cmds.get("moveUser"), clientID, roomID);
				break;
			case "disconnect":
				doCommand(cmds.get("disconnect"), clientID, null);
				break;
			default:
				doCommand(Commands.getInstance().findCommand((c.getCommand())), c.getKey(), c.getValue());
				break;
		}
	}
	
	/**
	 * Executes the command from the client
	 * @param cmd The command to be executed.
	 * @param key First parameter for command.
	 * @param value Second parameter for command.
	 * @return
	 */
	public Object[] doCommand(CommandInfo cmd, Object key, Object value){
		if(cmd != null){
			Commands com = Commands.getInstance();
			CommandResponse r = com.execute(clientID, cmd, key, value);
			Log2.log(Level.FINER,"Got command response: ["+(r.wasSuccessful()?"Success":"Failure")+": "+r.getMessage()+"]");
			return r.getData();
		}else{
			Log2.log(Level.WARNING, "Could not find command");
			return null;
		}
	}
	
	/**
	 * Moves this client to another room symbolized by a list of clients
	 * and adds them as listeners to its stream.
	 * @param clientList A list of clients symbolizing all clients in a room.
	 */
	public synchronized void moveToRoom(List<Client> clientList){
		removeAllListeningClients();
		for (Client c : clientList) {
			requestListeningClient(c);
		}
	}

	/**
	 * Requests a port for this client to start connection
	 * @param client The client to listen to.
	 */
	public void requestListeningClient(Client client){
		tcp.sendData(new Command("requestSoundPort", client.getClientID(), null));
	}
	
	/**
	 * Adds a client to this client connection list
	 * @param remoteClient The remote client
	 * @param port The port that is used
	 */
	public void addListeningClient(Client remoteClient, int port){
		soundRouter.addClient(remoteClient.getClientID(), remoteClient.ip, port);
	}
	
	/**
	 * Removes a client that listens to this client.
	 * @param client The client to be removed.
	 */
	public void removeListeningClient(Client client) {
		soundRouter.removeClient(client.clientID);
		tcp.sendData(new Command("closeSoundPort", client.getClientID(), null));
	}
	
	/**
	 * Removes all clients that listens to this client.
	 */
	public void removeAllListeningClients() {
		soundRouter.removeAllClients();
	}
	
	/**
	 * Gets the information of the client in a bundle.
	 * @return The information of the client.
	 */
	public UserInfoPacket getInfoPacket() {
		return new UserInfoPacket(clientID, alias);
	}
	
	/**
	 * Gets the clients ID.
	 * @return The ID of the client.
	 */
	public int getClientID() {
		return clientID;
	}

	/**
	 * Gets the name of this client.
	 * @return The name of the client.
	 */
	public String getName() {
		return alias;
	}

	/**
	 * Set the name of this client.
	 * @param name The new name of the client.
	 */
	protected void setName(String name) {
		if(name != null){
			this.alias = name;
		}
	}

	/**
	 * Remove this client and all services associated with it.
	 */
	public void terminate() {
		if (soundRouter != null) {
			soundRouter.terminate();
		}
		if (tcp != null) {
			tcp.terminate();
		}
		IDFactory.getInstance().freeID(clientID);
	}

	/**
	 * Logs an error-message and terminates the client.
	 */
	public void connectionLost() {
		Log2.log(Level.INFO, "Client disconnected - closing streams");
		this.terminate();
	}

	@Override
	public void handleCommand(Command c) {
		Log2.log(Level.FINER,"[Client]userid: "+ clientID + "s Got command " + c.getCommand() + " key "+ c.getKey() + " value "+ c.getValue());
		runAndroidCommand(c);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Client: name=" + alias + ", clientID=" + clientID + ", ip=" + ip;
	}
}
