package se.chalmers.fleetspeak;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import se.chalmers.fleetspeak.sound.Constants;
import se.chalmers.fleetspeak.tcp.CommandHandler;
import se.chalmers.fleetspeak.util.Log;

public class ServerMain{
    
    private int tcpPort;
    private int rtpPort;
    
    
    private static CommandHandler commandHandler;
    private static ServerSocket serverSocket = null;
    
    private volatile boolean running;
    
    public ServerMain(int tcpPort, int rtpPort) throws UnknownHostException{
    	Log.log("Starting server @LAN-IP "+InetAddress.getLocalHost().getHostAddress()+
    			" tcp:"+tcpPort+" rtp:"+rtpPort);
    	this.running = true;
    	commandHandler = new CommandHandler();
    	//TODO fix this?
    	Constants.setServerIP(InetAddress.getLocalHost().getHostAddress());
    	

    	this.tcpPort = tcpPort;
    	this.rtpPort = rtpPort;
    }
    
    public void start() throws UnknownHostException{
        this.running = true;
    	//Start the server
        try {
        	InetAddress locIP = InetAddress.getLocalHost();
            serverSocket = new ServerSocket(tcpPort, 0, locIP);
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
        Client client = new Client(clientSocket, rtpPort);
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