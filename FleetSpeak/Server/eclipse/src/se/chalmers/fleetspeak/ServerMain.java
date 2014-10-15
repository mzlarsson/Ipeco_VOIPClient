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
	
    private static int DEFAULT_PORT_TCP = 8867;
    private static int DEFAULT_PORT_RTP = 8868;
    
    private int tcpPort;
    private int rtpPort;
    
    
    private static CommandHandler commandHandler;
    private static ServerSocket serverSocket = null;
    
    private volatile boolean running;
    
    public static void main(String[] args) throws IOException{
    	//Setup info about connection
        
    	int tcpPort = (args!=null&&args.length>0?Integer.parseInt(args[0]):DEFAULT_PORT_TCP);
        int rtpPort = (args!=null&&args.length>1?Integer.parseInt(args[1]):DEFAULT_PORT_RTP);
    
    	Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
    		@Override
    		public void run(){
    			commandHandler.terminate();
    			try {
    	    		if(serverSocket != null){
    	    			serverSocket.close();
    	    		}
    			} catch (IOException e) {}
    		}
    	}));
    	
    	new ServerMain(tcpPort, rtpPort).start();;
      
    }
    
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
                //Create client
                Client client = new Client(clientSocket, rtpPort);
                //Add to client list
                addClient(client);
            }
            
            if(serverSocket != null){
            	serverSocket.close();
            }
        }catch(IOException e){
            Log.log("[SERVER] "+e.getMessage());
            terminate();
        }
    }
    
    public static void addClient(Client client){
        //give the client to the commandHandler
        commandHandler.addClient(client);
        
        //Print info in server console
        Log.log("A new person joined ");
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