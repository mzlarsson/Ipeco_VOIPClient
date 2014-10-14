package se.chalmers.fleetspeak;

import java.io.IOException;
import java.net.Socket;

import se.chalmers.fleetspeak.sound.SoundHandler;
import se.chalmers.fleetspeak.sound.SoundHandlerFactory;
import se.chalmers.fleetspeak.tcp.TCPHandler;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.IDFactory;
import se.chalmers.fleetspeak.util.Log;

public class Client {

	private String name;
	private SoundHandler rtp;
	private TCPHandler tcp;
	private boolean muted = false;
	private int clientID;

	public Client(Socket socket, int rtpPort) throws IOException {
		this.clientID = IDFactory.getInstance().getID();
		this.rtp = SoundHandlerFactory.getDefaultSoundHandler(socket.getInetAddress(), rtpPort);
		this.rtp.start();
		this.tcp = new TCPHandler(socket);
		this.tcp.start();
		this.tcp.sendData(new Command("setID",clientID,null ));

	}

	public void setName(String name) {
		if(name != null){
			this.name = name;
		}
	}

	public String getName() {
		return name;
	}

	public boolean isMuted() {
		return muted;
	}

	public void terminate() {
		if (rtp != null) {
			rtp.terminate();
		}
		if (tcp != null) {
			tcp.terminate();
		}
	}

	public void connectionLost() {
		Log.log("Client disconnected - closing streams");
		this.terminate();
	}

	public int getClientID() {
		return clientID;
	}
}
