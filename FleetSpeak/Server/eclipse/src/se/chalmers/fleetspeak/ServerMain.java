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
    
    private static List<Client> clients = new ArrayList<Client>();
    private static ServerSocket serverSocket = null;
    
    private volatile boolean running;
    
    public static void main(String[] args) throws IOException{
    	//Setup info about connection
        
    	int tcpPort = (args!=null&&args.length>0?Integer.parseInt(args[0]):DEFAULT_PORT_TCP);
        int rtpPort = (args!=null&&args.length>1?Integer.parseInt(args[1]):DEFAULT_PORT_RTP);
    
    	Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
    		
    		public void run(){
    			
    	    	for(int i = 0; i<clients.size(); i++){
    	    		clients.get(i).close();
    	    	}
    		}
    	}));
    	
    	new ServerMain(tcpPort, rtpPort);
      
    }
    
    public ServerMain(int tcpPort, int rtpPort) throws UnknownHostException{
    	System.out.println("Starting server @LAN-IP "+InetAddress.getLocalHost().getHostAddress()+" tcp:"+tcpPort+" rtp:"+rtpPort);
    	this.running = true;
    	
    	start(tcpPort, rtpPort);
    }
    
    private void start(int tcpPort, int rtpPort){
    	//Start the server
        try {
            serverSocket = new ServerSocket(tcpPort);
            Socket clientSocket = null;
            while(running){
            	//Create connection
                clientSocket = serverSocket.accept();
                System.out.println("Receiving connection request from client...");
                //Create client
                Client client = new Client(clientSocket, rtpPort, StringUtil.generateRandomCode(16));
                //Add to client list
                addClient(client);
            }
            
            if(serverSocket != null){
            	serverSocket.close();
            }
        }catch(IOException e){
            System.out.println("[SERVER] "+e.getMessage());
            terminate();
        }
    }
    
    public static void addClient(Client client){
        //Save the handler for more interaction
        clients.add(client);

        //Print info in server console
        System.out.println("A new person joined ("+clients.size()+")");
    }
    
    public static void removeClient(Client client){
        //Remove the handler from more interaction
    	clients.remove(client);
    	
        //Print info in server console
    	System.out.println("A person left the room ("+clients.size()+")");
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