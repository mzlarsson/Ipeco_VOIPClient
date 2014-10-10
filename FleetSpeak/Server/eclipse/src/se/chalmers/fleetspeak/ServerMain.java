package se.chalmers.fleetspeak;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
public class ServerMain{
	
    private static int DEFAULT_PORT_TCP = 8867;
    private static int DEFAULT_PORT_RTP = 8868;
    
    private int tcpPort;
    private int rtpPort;
    
    private static RoomInterface room;
    private static RoomHandler roomhandler;
    private static ServerSocket serverSocket = null;
    
    private volatile boolean running;
    
    public static void main(String[] args) throws IOException{
    	//Setup info about connection
        
    	int tcpPort = (args!=null&&args.length>0?Integer.parseInt(args[0]):DEFAULT_PORT_TCP);
        int rtpPort = (args!=null&&args.length>1?Integer.parseInt(args[1]):DEFAULT_PORT_RTP);
    
    	Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
    		@Override
    		public void run(){
    	    	for(int i = 0; i<room.getNbrOfUsers(); i++){
    	    		room.getUser(i).close();
    	    	}
    		}
    	}));
    	
    	new ServerMain(tcpPort, rtpPort);
      
    }
    
    public ServerMain(int tcpPort, int rtpPort) throws UnknownHostException{
    	Log.log("Starting server @LAN-IP "+InetAddress.getLocalHost().getHostAddress()+
    			" tcp:"+tcpPort+" rtp:"+rtpPort);
    	this.running = true;
    	room = new FleetRoom(1);
    	roomhandler = new RoomHandler();
    	roomhandler.addRoom(room);

    	this.tcpPort = tcpPort;
    	this.rtpPort = rtpPort;
    	
    	
    	start(tcpPort, rtpPort);
    }
    
    public void start() throws UnknownHostException{
        this.running = true;
    	//Start the server
        try {
            serverSocket = new ServerSocket(tcpPort);
            Socket clientSocket = null;
            while(running){
            	//Create connection
                clientSocket = serverSocket.accept();
                //Create client
                Client client = new Client(clientSocket, rtpPort, StringUtil.generateRandomCode(16));
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
        //Save the handler for more interaction
        room.addUser(client);
        
        roomhandler.addClient(client, 1);//FIXME Generate better room id
       
        
        int nbrOfClients = roomhandler.getClients(roomhandler.findRoom(1)).length;
        
        for(int i = 0; i<nbrOfClients;i++){
        	if(roomhandler.findRoom(1).getUsers().get(i).equals(client)){
        		
        	}
        }
        
        //Notice about change in clients
        for(int i = 0; i<room.getNbrOfUsers(); i++){
        	if(room.getUser(i)!=client){
	        	room.getUser(i).clientConnected(room.getUsers());
        	}
        }

        //Print info in server console
        Log.log("A new person joined ("+room.getNbrOfUsers()+")");
    }
    
    public static void removeClient(Client client){
        //Remove the handler from more interaction
    	room.removeUser(client);
    	
        //Notice about change in clients
        for(int i = 0; i<room.getNbrOfUsers(); i++){
        	if(room.getUser(i)!=client){
	        	room.getUser(i).clientDisconnected(room.getUsers());
        	}
        }
    	
        //Print info in server console
    	Log.log("A person left the room ("+room.getNbrOfUsers()+")");
    }
    
    public void terminate() {
    	try {
    		if(serverSocket != null){
    			serverSocket.close();
    		}
		} catch (IOException e) {}
    	
    	running = false;
    }
}