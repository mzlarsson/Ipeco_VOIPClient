package se.chalmers.fleetspeak.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

import se.chalmers.fleetspeak.Client;

public class CommandHandler extends TCPHandler{

	public CommandHandler(Socket clientSocket) {
		super(clientSocket);
	}

	@Override
	public void run(){
		try{
			//Listen for messages from the client
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.getInputStream()));
			while(true){
				
				String message = reader.readLine();
				if(message != null){
					System.out.println("Test");
					if(message.startsWith("/disconnect")){
						Command.setValue("disconnect", true);
					}else if(message.startsWith("/nick")){
						Command.setValue("nick", message.substring(6));
					}else if(message.equals("/mute")){
						System.out.println("Mute");
						Command.setValue("mute", true);
					}else if(message.equals("/unmute")){
						Command.setValue("mute", false);
						System.out.println("Unmute");
					}
				}
			}
		}catch(IOException e){
			System.out.println("[CommandHandler] "+e.getMessage());
			notifyConnectionLost(this);
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
