package se.chalmers.fleetspeak;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import se.chalmers.fleetspeak.rtp.RTPHandler;
import se.chalmers.fleetspeak.rtp.SoundHandler;
import se.chalmers.fleetspeak.tcp.CommandHandler;
import se.chalmers.fleetspeak.tcp.CommandListener;
import se.chalmers.fleetspeak.tcp.Commands;

public class Client implements ConnectionListener, CommandListener {

	private String usercode;
	private String name;
	private RTPHandler rtp;
	private CommandHandler cmd;
	private boolean muted = false;
	private int clientID;
	
	public Client(Socket socket, int rtpPort, String usercode)
			throws IOException {
		this.rtp = new SoundHandler(socket.getInetAddress(), rtpPort);
		this.rtp.start();

		this.cmd = new CommandHandler(socket);
		this.cmd.start();
		this.cmd.addConnectionListener(this);
		this.cmd.addCommandListener(this);
		this.usercode = usercode;
	}
	
	private void setName(String name) {
		// TODO add filter
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public String getUserCode() {
		return this.usercode;
	}
	
	public boolean isMuted() {
		return muted;
	}
	
	public void clientConnected(List<Client> clients) {
//		rtp.onClientConnect(clients);
//		cmd.onClientConnect(clients);
	}
	
	public void clientDisconnected(List<Client> clients) {
//		rtp.onClientDisconnect(clients);
//		cmd.onClientDisconnect(clients);
	}
	
	public void close() {
		if (rtp != null) {
			rtp.terminate();
		}
		if (cmd != null) {
			cmd.terminate();
		}
		
		ServerMain.removeClient(this);
	}

	public void connectionLost() {
		Log.log("Client disconnected - closing streams");
		this.close();
	}

	public static Client getClient(List<Client> clients, String usercode) {
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getUserCode().equals(usercode)) {
				return clients.get(i);
			}
		}

		return null;
	}

	@Override
	public void commandChanged(Commands key, Object value) {
		Log.log("[CLIENT] Got command: " + key + " changed to: " + value.toString());
		switch (key) {
		case DISCONNECT:
			// TODO
			break;
		case SET_NAME:
			this.setName((String) value);
			Log.log("Current name " + name);
			break;
		case MUTE:
			this.muted = true;
			Log.log("is muted: " + muted);
			break;
		case UNMUTE:
			this.muted = false;
			Log.log("is muted: " + muted);
			break;
		default:
			Log.log(key.getName() + " is not implemented");
			break;
		}
		
	}

	
	public int getClientID() {
		return clientID;
	}
}
