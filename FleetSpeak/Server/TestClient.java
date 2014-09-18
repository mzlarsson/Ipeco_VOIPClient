import java.io.*;
import java.lang.System;
import java.net.*;

public class TestClient{
    public static void main(String[] args) throws IOException, InterruptedException{
        try{
            Socket socket = new Socket("localhost", 1235);
            
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            
            DisplayMessage d = new DisplayMessage(in);
            d.start();
            
            String messageSent, messageRecived;
            
            while(true){
                if((messageSent = keyboard.readLine()) != null){
                    out.println(messageSent);
                }
                
                
            }
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    
    private static class DisplayMessage extends Thread{
        private BufferedReader br;
        
            public DisplayMessage(BufferedReader br){
                super("DisplayMessage");
                this.br = br;
            }
            public void run(){
                try{
                while(true){
                    System.out.print(br.readLine()+ "\n");
                    
                }
                }catch(IOException e){
                    System.out.println(e.getMessage());
                }
            }
        
        
    }
    
    
}