import java.net.*;
import java.io.*;
import java.util.*;

public class ServerBroadcast extends Thread{
    
    List<Socket> clients = Collections.synchronizedList(new ArrayList<Socket>());
    List<BufferedReader> in = Collections.synchronizedList(new ArrayList<BufferedReader>());
    List<PrintWriter> out = Collections.synchronizedList(new ArrayList<PrintWriter>());
    
    public ServerBroadcast() {
       
    }
    public void run(){
        while(true){
            readIn();
            try{
            Thread.sleep(10);
            }catch(InterruptedException e){
                
            }
        }
    }
    private  void readIn(){
        try{
            String input;
            
            for(BufferedReader br: in){
                if((input = br.readLine()) != null){
                    System.out.println("got message");
                    broadcast(input);
                }
            }
            
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    public void addClient(Socket s){
        
        clients.add(s);
        try{
            in.add(new BufferedReader(new InputStreamReader(s.getInputStream())));
            out.add(new PrintWriter(s.getOutputStream(), true));
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
        System.out.println("Connected" + s.toString()+ " " + out.size());
    }
    public void broadcast(String s){
        for (PrintWriter pw: out){
            pw.println(s);
        }
    }

}