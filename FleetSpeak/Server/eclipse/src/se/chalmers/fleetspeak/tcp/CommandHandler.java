package se.chalmers.fleetspeak.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CommandHandler extends TCPHandler{
	
	ObjectOutputStream sender;
	private List<CommandListener> listeners = new ArrayList<CommandListener>();
	public CommandHandler(Socket clientSocket) {
		super(clientSocket);
		
	}

	@Override
	public void run(){
		try{
			//Listen for messages from the client
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.getInputStream()));
			sender = this.getObjectOutputStream();
			 
			 //for testing if the app can recive object from the server 
			Thread a = new Thread(new Spammer());
			 // a.start();  // uncomment this to start
			while(true){
				
				String message =reader.readLine();
				if(message != null){
					System.out.print("[COMMANDHANDLER] Command recived: ");
					if(message.startsWith(Commands.DISCONNECT.getName())){
						System.out.println(Commands.DISCONNECT);
						notifyListeners(Commands.DISCONNECT, true);
					}else if(message.startsWith(Commands.SET_NAME.getName())){
						System.out.println(Commands.SET_NAME);
						notifyListeners(Commands.SET_NAME, message.substring(6));
					}else if(message.equals(Commands.MUTE.getName())){
						System.out.println(Commands.MUTE);
						notifyListeners(Commands.MUTE, true);
					}else if(message.equals(Commands.UNMUTE.getName())){
						System.out.println(Commands.UNMUTE);
						notifyListeners(Commands.MUTE, false);
					}else if(message.equals("data")){
						//send data to client
						System.out.println("Data");
						String ss = "This string can be sent to the phone";
						sender.writeObject(ss);
						sender.flush();
					}else{
						System.out.println("Unknown command. " + message);
					}
				}
			}
		}catch(IOException e){
			System.out.println("[CommandHandler] "+e.getMessage());
			notifyConnectionLost();
		}
	}
	
	
	// sends a lot of strings to app. only for testing
	class Spammer implements Runnable{

		@Override
		public void run() {
			while(true){
			String s = "\nEverything is awesome\nEverything is cool when you're part of a team"
					+ "\nEverything is awesome when we're living our dream";
			try {
				sender.writeObject(s);
				sender.flush();
				Thread.sleep(500);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
		
	}
	
	
	public void addCommandListener(CommandListener listener){
		listeners.add(listener);
	}
	
	public void removeCommandListener(CommandListener listener){
		listeners.remove(listener);
	}
	
	private void notifyListeners(Commands command, Object value){
		for(int i = 0; i<listeners.size(); i++){
			listeners.get(i).commandChanged(command, value);
		}
	}
}
