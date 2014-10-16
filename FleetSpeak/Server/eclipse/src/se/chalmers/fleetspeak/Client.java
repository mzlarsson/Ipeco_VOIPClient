package se.chalmers.fleetspeak;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import se.chalmers.fleetspeak.sound.SoundHandler;
import se.chalmers.fleetspeak.sound.SoundHandlerFactory;
import se.chalmers.fleetspeak.tcp.TCPHandler;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.IDFactory;
import se.chalmers.fleetspeak.util.Log;

public class Client{

	private String name;
	private SoundHandler rtp;
	private TCPHandler tcp;
	private boolean muted = false;
	private int clientID;
	
	private InetAddress ip;
	private int serverRtpPort;

	public Client(Socket socket, int serverRtpPort) throws IOException {
		this.clientID = IDFactory.getInstance().getID();
		Log.log("Created client with ID="+this.clientID);
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
			Log.log("Connection successfully started to IP="+ip.getHostAddress());
		}else{
			Log.log("Error: Could not start RTP connection. Already started.");
		}
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Client: name=" + name + ", clientID=" + clientID
				+ ", muted=" + muted + ", ip=" + ip;
	}
	
	
}
