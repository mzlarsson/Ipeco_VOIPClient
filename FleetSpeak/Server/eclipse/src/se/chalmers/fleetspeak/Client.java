package se.chalmers.fleetspeak;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import se.chalmers.fleetspeak.rtp.RTPHandler;
import se.chalmers.fleetspeak.rtp.SoundHandler;
import se.chalmers.fleetspeak.tcp.CommandHandler;
import se.chalmers.fleetspeak.tcp.CommandListener;

public class Client implements ConnectionListener, CommandListener{

	private String usercode;
	private int ID;
	

	private RTPHandler rtp;
	private CommandHandler cmd;
	
	public Client(Socket socket, int rtpPort, String usercode) throws IOException{
		this.rtp = new SoundHandler(socket.getInetAddress(), rtpPort);
		this.cmd = new CommandHandler(socket);
		this.cmd.start();
		this.cmd.addCommandListener(this);
		this.usercode = usercode;
	}
	
	public String getUserCode(){
		return this.usercode;
	}

	public void clientConnected(List<Client> clients){
		rtp.onClientConnect(clients);
		cmd.onClientConnect(clients);
	}
	
	public void clientDisconnected(List<Client> clients){
		rtp.onClientDisconnect(clients);
		cmd.onClientDisconnect(clients);
	}
	
	public void close(){
		if(rtp != null){
			rtp.close();
		}
		if(cmd != null){
			cmd.close();
		}
	}
	
	public void connectionLost(ConnectionHandler handler){
		System.out.println("A "+handler.getClass().getCanonicalName()+" has lost connection");
		this.close();
	}
	
	
	public static Client getClient(List<Client> clients, String usercode){
		for(int i = 0; i<clients.size(); i++){
			if(clients.get(i).getUserCode().equals(usercode)){
				return clients.get(i);
			}
		}
		
		return null;
	}

	@Override
	public void commandChanged(String key, Object oldValue, Object value) {
		if(oldValue == null){
			oldValue = "null";
		}
		System.out.println("[CLIENT] Got command: "+key+" changed like "+oldValue.toString()+" --> "+value.toString());
	}
	
	public int getClientID() {
		return ID;
	}
}
