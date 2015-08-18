import java.io.*;
import java.lang.System;
import java.net.*;
import java.util.*;
public class ServerMain{
	
    private static ArrayList<Handler> handlers = new ArrayList<Handler>();
    private static ServerSocket serverSocket;
    
    
    public static void main(String[] args) throws IOException{
    	//Setup info about connection
        int port = (args!=null&&args.length>0?Integer.parseInt(args[0]):8868);
    	System.out.println("Starting server @LAN-IP "+InetAddress.getLocalHost().getHostAddress()+":"+port);
    
    	Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
    		public void run(){
    	    	for(int i = 0; i<handlers.size(); i++){
    	    		handlers.get(i).terminate();
    	    	}
    	    	
    	    	try {
    				serverSocket.close();
    			} catch (IOException e) {}
    		}
    	}));
    	
    	new ServerMain(port);
    }
    
    public ServerMain(int port){
    	//Start the server
        try {
            serverSocket = new ServerSocket(port);
            Socket clientSocket = null;
            while(true){
            	//Create connection
                clientSocket = serverSocket.accept();
                Handler c = getHandler(clientSocket, port);
                c.start();
                //Save the handler for more interaction
                handlers.add(c);
                
                System.out.println("A new person joined ("+handlers.size()+")");
            }
        }catch(IOException e){
            System.out.println("[SERVER] "+e.getMessage());
        }
    }
    
    private static Handler getHandler(Socket socket, int port){
    	return new SoundHandler(socket);
    }
    
    
    public static int getHandlerCount(){
    	return handlers.size();
    }
    
    public static void unregisterHandler(Handler handler){
    	handlers.remove(handler);
    	System.out.println("A person left the room ("+handlers.size()+")");
    }

    public static void sendMessage(Handler sender, byte[] message){
    	OutputStream out = null;
    	for(int i = 0; i<handlers.size(); i++){
    		if(handlers.get(i) != sender){
    			try {
    				out = handlers.get(i).getOutputStream();
					out.write(message, 0, message.length);
					out.flush();
				} catch (IOException e) {
					System.out.println("Could not send message to client!");
				}
    		}
    	}
    }
}