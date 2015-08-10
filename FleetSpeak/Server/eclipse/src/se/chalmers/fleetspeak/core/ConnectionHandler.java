package se.chalmers.fleetspeak.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.chalmers.fleetspeak.core.command.Commands;
import se.chalmers.fleetspeak.core.permission.PermissionLevel;
import se.chalmers.fleetspeak.audio.sound.Constants;
import se.chalmers.fleetspeak.util.Log;
import se.chalmers.fleetspeak.util.Log2;

/**
 * Listen for connections
 * @author Nieo
 *
 */
public class ConnectionHandler{

	private int port;
	private ClientCreator clientCreator;
	private Executor executor;
	private Logger logger;
	private ServerSocket serverSocket = null;

	private volatile boolean running;

	/**
	 * Constructs the ConnectionHandler and starts the server.
	 * @param port The port to the start server on.
	 * @param cc ClientCreator to pass connections to
	 * @throws UnknownHostException
	 */
	public ConnectionHandler(int port, ClientCreator cc){
		logger = Logger.getLogger("Debug");
		try {
			logger.log(Level.INFO,"Starting server @LAN-IP "+InetAddress.getLocalHost().getHostAddress()+" on port "+port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.port = port;
		this.clientCreator = cc;
		executor = Executors.newSingleThreadExecutor();
		executor.execute(connectionListener);
	}

	Runnable connectionListener = () -> {
		this.running = true;
		try {
			InetAddress locIP = InetAddress.getLocalHost();
			serverSocket = new ServerSocket(port, 0, locIP);
			Socket clientSocket = null;

			while(running){
				clientSocket = serverSocket.accept();
				clientCreator.addNewClient(clientSocket);
			}
			if(serverSocket != null){
				serverSocket.close();
			}
		}catch(IOException e){
			logger.log(Level.SEVERE, e.getMessage());
			terminate();
		}
	};



	/**
	 * Returns whether ConnectionHandler is running
	 * @return
	 */
	public boolean isRunning(){
		return this.running;
	}

	/**
	 * Stops ConnectionHandler and closes all connected resources.
	 */
	public void terminate() {
		running = false;
		try {
			if(serverSocket != null){
				serverSocket.close();
			}
		} catch (IOException e) {}
		clientCreator.terminate();
	}
}