package se.chalmers.fleetspeak.tcp;

import java.io.BufferedReader;
import java.io.ObjectOutputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.fleetspeak.Client;

public class CommandHandler extends TCPHandler{
	
	
	private List<CommandListener> listeners = new ArrayList<CommandListener>();
	public CommandHandler(Socket clientSocket) {
		super(clientSocket);
		
	}

	@Override
	public void run(){
		try{
			//Listen for messages from the client
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.getInputStream()));
			ObjectOutputStream sender = this.getObjectOutputStream();
			
			while(true){
				
				String message = reader.readLine();
				if(message != null){
					System.out.print("Command recived: ");
					if(message.startsWith(Commands.DISCONNECT.toString())){
						System.out.println(Commands.DISCONNECT);
						notifyListeners(Commands.DISCONNECT, true);
					}else if(message.startsWith(Commands.SET_NAME.toString())){
						System.out.println(Commands.SET_NAME);
						notifyListeners(Commands.SET_NAME, message.substring(6));
					}else if(message.equals(Commands.MUTE.toString())){
						System.out.println(Commands.MUTE);
						notifyListeners(Commands.MUTE, true);
					}else if(message.equals(Commands.UNMUTE.toString())){
						System.out.println(Commands.UNMUTE);
						notifyListeners(Commands.MUTE, false);
					}else if(message.equals("data")){
						//send data to client
						System.out.println("Data");
						String s = "This string can be sent to the phone";
						sender.writeObject(s);
						sender.flush();
					}else{
						System.out.println("Unknown command. " + message);
					}
				}
			}
		}catch(IOException e){
			System.out.println("[CommandHandler] "+e.getMessage());
			notifyConnectionLost(this);
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
	

	@Override
	public void onClientConnect(List<Client> clients) {
		System.out.println("[CommandHandler] Someone connected");
	}

	@Override
	public void onClientDisconnect(List<Client> clients) {
		System.out.println("[CommandHandler] Someone disconnected");
	}
}
