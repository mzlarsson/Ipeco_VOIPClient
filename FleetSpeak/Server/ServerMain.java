import java.io.*;
import java.lang.System;
import java.net.*;
import java.util.*;
public class ServerMain{
    private static Socket clientSocket;
    private static ArrayList<PrintStream> streams = new ArrayList<PrintStream>();
    
    
    public static void main(String[] args) throws IOException{
        int port = (args!=null&&args.length>0?Integer.parseInt(args[0]):8868);
    	System.out.println("Starting server @LAN-IP "+InetAddress.getLocalHost().getHostAddress()+":"+port);
    	
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true){
                clientSocket = serverSocket.accept();
                chatHandler c = new chatHandler(clientSocket);
                c.start();
                for(PrintStream stream : streams){
                    stream.println("New user");
                }
                
            }
            
        }catch(IOException e){
            System.out.println(e.getMessage());
            
        }
        
    }
    private static class chatHandler extends Thread{
        
        private Socket clientSocket;
        private BufferedReader streamIn;
        private PrintStream streamOut;
        
        public chatHandler(Socket clientSocket){
            super("chatHander");
            this.clientSocket = clientSocket;
        }
        
        public void run(){
            
            try{
                streamIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                streamOut = new PrintStream(clientSocket.getOutputStream(), true);
                streams.add(streamOut);
                streamOut.println("Welcome");
                System.out.println(streams.size());
                while(true){
                    String message = streamIn.readLine();
                    if(message != null){
                        for(PrintStream stream : streams){
                                stream.println(message);
                        }
                    }
                }
                
            }catch(IOException e){
                System.out.println(e.getMessage());
                
            }
            
        }
        
    }
}