package se.chalmers.fleetspeak.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.core.command.Commands;
import se.chalmers.fleetspeak.sound.Constants;

/**
 * Listen for connections form the app
 * Creates new clients and put them on the eventbus
 * @author Nieo
 *
 */
public class ConnectionHandler{
    
    private int port;
    private ClientCreator clientCreator;
    
    private Logger logger;
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

    	//TODO fix this?
    	Constants.setServerIP(InetAddress.getLocalHost().getHostAddress());
    	Commands.getInstance();
    	
    	this.port = port;
    	this.clientCreator = new ClientCreator();
    	this.running = true;
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
    	try {
    		if(serverSocket != null){
    			serverSocket.close();
    		}
		} catch (IOException e) {}
    	clientCreator.terminate();
    	RoomHandler.getInstance().terminate();
    	Commands.terminate();
    	running = false;
    }
}