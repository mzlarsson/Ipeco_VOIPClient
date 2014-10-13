package se.chalmers.fleetspeak;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import se.chalmers.fleetspeak.rtp.RTPHandler;
import se.chalmers.fleetspeak.rtp.SoundHandler;
import se.chalmers.fleetspeak.tcp.CommandHandler;
import se.chalmers.fleetspeak.tcp.OLDCommandHandler;
import se.chalmers.fleetspeak.tcp.CommandListener;
import se.chalmers.fleetspeak.tcp.Commands;
import se.chalmers.fleetspeak.tcp.TCPHandler;

public class Client {

	private String name;
	private RTPHandler rtp;
	private TCPHandler tcp;
	private boolean muted = false;
	private int clientID;

	public Client(Socket socket, int rtpPort) throws IOException {
		this.clientID = IDFactory.getInstance().getID();
		this.rtp = new SoundHandler(socket.getInetAddress(), rtpPort);
		this.rtp.start();
		this.tcp = new TCPHandler(socket);
		this.tcp.start();

	}

	private void setName(String name) {
		// TODO add filter
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean isMuted() {
		return muted;
	}

	public void close() {
		if (rtp != null) {
			rtp.terminate();
		}
		if (tcp != null) {
			tcp.terminate();
		}
	}

	public void connectionLost() {
		Log.log("Client disconnected - closing streams");
		this.close();
	}

	public int getClientID() {
		return clientID;
	}
}
