package se.chalmers.fleetspeak.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.core.command.Commands;
import se.chalmers.fleetspeak.core.permission.PermissionLevel;
import se.chalmers.fleetspeak.sound.Constants;


/**
 * Listen for connections form the app
 * Creates new clients and put them on the eventbus
 * @author Nieo
 *
 */
public class ConnectionHandler{

	private Logger logger;

	private int port;

	private static ServerSocket serverSocket = null;

	private volatile boolean running;

	/**
	 * Constructs the ConnectionHandler and starts the server.
	 * @param port The port to the start server on.
	 * @throws UnknownHostException
	 */
	public ConnectionHandler(int port) throws UnknownHostException{
		logger = Logger.getLogger("Debug");
		logger.log(Level.INFO,"Starting server @LAN-IP "+InetAddress.getLocalHost().getHostAddress()+" on port "+port);
		this.running = true;
		//TODO fix this?
		Constants.setServerIP(InetAddress.getLocalHost().getHostAddress());
		Commands.getInstance();

		this.port = port;
	}
	/**
	 * Starts the ConnectionHandler. Sets up the various sockets and connections.
	 * @throws UnknownHostException
	 */
	public void start() throws UnknownHostException{
		this.running = true;
		//Instantiate the model
		RoomHandler.getInstance();
		//Start the server
		try {
			InetAddress locIP = InetAddress.getLocalHost();
			serverSocket = new ServerSocket(port, 0, locIP);
			Socket clientSocket = null;
			while(running){
				//Create connection
				clientSocket = serverSocket.accept();
				//Add to client list
				addClient(clientSocket);
			}

			if(serverSocket != null){
				serverSocket.close();
			}
		}catch(IOException e){
			logger.log(Level.SEVERE, "[SERVER] "+e.getMessage());
			terminate();
		}
	}

	/**
	 * Adds a client connection.
	 * @param clientSocket The socket from which the client's traffic is coming from.
	 * @throws IOException
	 */
	private void addClient(Socket clientSocket) throws IOException{
		//Print info in server console
		logger.log(Level.INFO, "A new person joined");

		//Create and forward client
		Client client = new Client(clientSocket);

		Commands cmds = Commands.getInstance();
		cmds.execute(-1, cmds.findCommand("AddUser"), client, PermissionLevel.ADMIN_ALL); // TODO If not all clients should have ADMIN rights this is the place.
		client.start();
	}
	/**
	 * Returns whether ConnectionHandler is running
	 * @return
	 */
	public boolean isRunning(){
		return this.running;
	}
	/**
	 * Stops ConnectionHandler and closes the  serversocket.
	 */
	public void terminate() {
		logger.log(Level.INFO, "Shuting down");
		try {
			if(serverSocket != null){
				serverSocket.close();
			}
		} catch (IOException e) {}
		RoomHandler.getInstance().terminate();
		Commands.terminate();
		running = false;
	}
}