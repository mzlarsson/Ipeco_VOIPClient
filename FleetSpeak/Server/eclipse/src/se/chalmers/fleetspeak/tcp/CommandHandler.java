package se.chalmers.fleetspeak.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.fleetspeak.Command;
import se.chalmers.fleetspeak.Log;

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
			ObjectInputStream reader = new ObjectInputStream(this.getInputStream());
			sender = this.getObjectOutputStream();
			 
			 //for testing if the app can recive object from the server 
			Thread a = new Thread(new Spammer());
			 // a.start();  // uncomment this to start
			while(true){
				
				Command c = (Command) reader.readObject();
				String command = c.getCommand();
				if(command != null){
					System.out.print("[COMMANDHANDLER] Command recived: ");
					if(command.startsWith(Commands.DISCONNECT.getName())){
						Log.log(Commands.DISCONNECT.toString());
						notifyListeners(Commands.DISCONNECT, true);
					}else if(command.startsWith(Commands.SET_NAME.getName())){
						Log.log(Commands.SET_NAME.toString());
						notifyListeners(Commands.SET_NAME, command.substring(6));
					}else if(command.equals(Commands.MUTE.getName())){
						Log.log(Commands.MUTE.toString());
						notifyListeners(Commands.MUTE, true);
					}else if(command.equals(Commands.UNMUTE.getName())){
						Log.log(Commands.UNMUTE.toString());
						notifyListeners(Commands.MUTE, false);
					}else if(command.equals("data")){
						//send data to client
						Log.log("Data");
						String ss = "This string can be sent to the phone";
						sender.writeObject(new Command(ss,null,null));
						sender.flush();
					}else{
						Log.log("Unknown command. " + command);
					}
				}
			}
		}catch(IOException e){
			Log.log("[CommandHandler] "+e.getMessage());
			notifyConnectionLost();
		}catch(ClassNotFoundException e){
			Log.log("[CommandHandler] "+e.getMessage());
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
