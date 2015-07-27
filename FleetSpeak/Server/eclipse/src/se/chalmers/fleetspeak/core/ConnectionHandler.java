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

/**
 * Listen for connections form the app
 * @author Nieo
 *
 */
public class ConnectionHandler{

	private int port;
	private ClientCreator clientCreator;
	private Executor executor;
	private Logger logger;
	private static ServerSocket serverSocket = null;

	private volatile boolean running;

	/**
	 * Constructs the ConnectionHandler and starts the server.
	 * @param port The port to the start server on.
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
	};


	/**
	 * Adds a client connection.
	 * @param clientSocket The socket from which the client's traffic is coming from.
	 */
	private void addClient(Socket clientSocket){
		clientCreator.addNewClient(clientSocket);
	}

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