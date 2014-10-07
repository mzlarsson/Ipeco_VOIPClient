package se.chalmers.fleetspeak.tcp;

import java.io.BufferedReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
			ObjectOutputStream sender = this.getObjectOutputStream();
			
			while(true){
				
				String message = reader.readLine();
				if(message != null){
					System.out.print("Command recived: ");
					if(message.startsWith(Commands.DISCONNECT)){
						Command.setValue("disconnect", true);
					}else if(message.startsWith(Commands.SET_NAME)){
						Command.setValue("nick", message.substring(6));
					}else if(message.equals(Commands.MUTE)){
						System.out.println("Mute");
						Command.setValue("mute", true);
					}else if(message.equals(Commands.UNMUTE)){
						Command.setValue("mute", false);
						System.out.println("Unmute");
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

	@Override
	public void onClientConnect(List<Client> clients) {
		System.out.println("[CommandHandler] Someone connected");
	}

	@Override
	public void onClientDisconnect(List<Client> clients) {
		System.out.println("[CommandHandler] Someone disconnected");
	}
}
