package se.chalmers.fleetspeak.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import se.chalmers.fleetspeak.core.command.Commands;
import se.chalmers.fleetspeak.core.permission.PermissionLevel;
import se.chalmers.fleetspeak.core.permission.Permissions;
import se.chalmers.fleetspeak.sound.Constants;
import se.chalmers.fleetspeak.util.Log;
/**
 * Listen for connections form the app
 * Creates new clients and put them on the eventbus
 * @author Nieo
 *
 */
public class ConnectionHandler{
    
    private int port;
    
    private static ServerSocket serverSocket = null;
    
    private volatile boolean running;
    
    public ConnectionHandler(int port) throws UnknownHostException{
    	Log.log("Starting server @LAN-IP "+InetAddress.getLocalHost().getHostAddress()+" on port "+port);
    	this.running = true;
    	//TODO fix this?
    	Constants.setServerIP(InetAddress.getLocalHost().getHostAddress());
    	Commands.getInstance();

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
    
    private void addClient(Socket clientSocket) throws IOException{
        //Print info in server console
        Log.log("A new person joined");
        
        //Create and forward client
        Client client = new Client(clientSocket, port);
        
        Commands cmds = Commands.getInstance();
        Permissions.addUserLevel(client.getClientID(), PermissionLevel.ADMIN_ALL);
        cmds.execute(-1, cmds.findCommand("AddUser"), client);
    }
    
    public boolean isRunning(){
    	return this.running;
    }
    
    public void terminate() {
    	try {
    		if(serverSocket != null){
    			serverSocket.close();
    		}
		} catch (IOException e) {}
    	Commands.terminate();
    	running = false;
    }
}