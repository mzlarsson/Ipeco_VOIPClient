package se.chalmers.fleetspeak.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

import se.chalmers.fleetspeak.Client;

public class CommandHandler extends TCPHandler{
	
	private CommandData cmd;

	public CommandHandler(Socket clientSocket) {
		super(clientSocket);
		this.cmd = new CommandData();
	}

	@Override
	public void run(){
		try{
			//Listen for messages from the client
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.getInputStream()));
			while(true){
				
				String message = reader.readLine();
				if(message != null){
					System.out.print("Command recived: ");
					if(message.startsWith("/disconnect")){
						setCommandValue("disconnect", true);
					}else if(message.startsWith("/nick ")){
						setCommandValue("nick", message.substring(6));
					}else if(message.equals("/mute")){
						System.out.println("Mute");
						setCommandValue("mute", true);
					}else if(message.equals("/unmute")){
						setCommandValue("mute", false);
						System.out.println("Unmute");
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
		cmd.addCommandListener(listener);
	}
	
	public void removeCommandListener(CommandListener listener){
		cmd.removeCommandListener(listener);
	}
	
	private void setCommandValue(String command, Object value){
		cmd.setValue(command, value);
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
