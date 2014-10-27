package se.chalmers.fleetspeak;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import se.chalmers.fleetspeak.sound.Constants;
import se.chalmers.fleetspeak.tcp.CommandHandler;
import se.chalmers.fleetspeak.util.Log;
/**
 * Listen for connections form the app
 * Creates new clients and put them on the eventbus
 * @author Nieo
 *
 */
public class ConnectionHandler{
    
    private int port;
    
    
    private static CommandHandler commandHandler;
    private static ServerSocket serverSocket = null;
    
    private volatile boolean running;
    
    public ConnectionHandler(int port) throws UnknownHostException{
    	Log.log("Starting server @LAN-IP "+InetAddress.getLocalHost().getHostAddress()+" on port "+port);
    	this.running = true;
    	commandHandler = new CommandHandler();
    	//TODO fix this?
    	Constants.setServerIP(InetAddress.getLocalHost().getHostAddress());
    	

    	this.port = port;
    }
    
    public void start() throws UnknownHostException{
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
            Log.log("[SERVER] "+e.getMessage());
            terminate();
        }
    }
    
    public void addClient(Socket clientSocket) throws IOException{
        //Print info in server console
        Log.log("A new person joined");
        
        //Create and forward client
        Client client = new Client(clientSocket, port);
        commandHandler.addClient(client);
    }
    
    public void terminate() {
    	try {
    		if(serverSocket != null){
    			serverSocket.close();
    		}
		} catch (IOException e) {}
    	commandHandler.terminate();
    	running = false;
    }
}