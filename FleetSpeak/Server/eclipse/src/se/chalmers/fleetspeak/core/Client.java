package se.chalmers.fleetspeak.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import se.chalmers.fleetspeak.sound.SoundHandler;
import se.chalmers.fleetspeak.sound.SoundHandlerFactory;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.IDFactory;
import se.chalmers.fleetspeak.util.Log;

/**
 * A class that handles all connectors with the app
 * @author Nieo
 *
 */


public class Client{

	private String name;
	private SoundHandler rtp;
	private TCPHandler tcp;
	private int clientID;
	
	private InetAddress ip;
	private int serverRtpPort;
	private int latestMixerID = 1;

	public Client(Socket socket, int serverRtpPort) throws IOException {
		this.clientID = IDFactory.getInstance().getID();
		this.name = "UnknownUser";
		this.tcp = new TCPHandler(socket, clientID);
		this.tcp.start();
		this.tcp.sendData(new Command("setID", clientID, null));

		this.ip = socket.getInetAddress();
		this.serverRtpPort = serverRtpPort;
	}
	
	public void startRTPTransfer(int clientRtpPort){
		if(this.rtp == null){
			this.rtp = SoundHandlerFactory.getDefaultSoundHandler(ip, serverRtpPort, clientRtpPort);
			this.rtp.start();
			this.rtp.switchMixer(latestMixerID);
		}else{
			Log.log("Error: Could not start RTP connection. Already started.");
		}
	}
	
	public void moveToRoom(int roomID){
		if(this.rtp != null){
			this.rtp.switchMixer(roomID);
		}
		
		this.latestMixerID = roomID;
	}

	public void setName(String name) {
		if(name != null){
			this.name = name;
		}
	}

	public String getName() {
		return name;
	}
	
	public void setMuted(Client client, boolean muted){
		if(this.rtp != null){
			this.rtp.setMuted(client.rtp, muted);
		}else{
			Log.log("<error>Could not "+(muted?"mute":"unmute")+" since sound transfer is not started</error>");
		}
	}

	public boolean isMuted(Client client) {
		return (this.rtp==null || this.rtp.isMuted(client.rtp));
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Client: name=" + name + ", clientID=" + clientID + ", ip=" + ip;
	}
	
	
}
