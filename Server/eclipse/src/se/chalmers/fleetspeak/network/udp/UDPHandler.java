package se.chalmers.fleetspeak.network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPHandler extends Thread{

	private DatagramSocket socket;
	private boolean isRunning = false;
	private Logger logger;
	private DatagramPacket receivePacket;
	private int packetSize;
	private PacketReceiver receiver;
	
	public UDPHandler(DatagramSocket socket, int packetSize) {
		super("UDPHandler:port"+socket.getLocalPort());
		logger = Logger.getLogger("Debug");
		this.socket = socket;
		this.packetSize = packetSize;
		receivePacket = new DatagramPacket(new byte[this.packetSize], this.packetSize);
	}

	@Override
	public void run() {
		isRunning = true;
		logger.log(Level.INFO, "Listening to UDP traffic on port: " + socket.getLocalPort());
		while(isRunning) {
			try {
				socket.receive(receivePacket);
			} catch (IOException e) {
				logger.log(Level.WARNING, "UDP-socket " + socket.getLocalPort() + " caught an exception");
				e.printStackTrace();
			}
			handlePacket(receivePacket.getData());			
		}
	}
	
	protected void handlePacket(byte[] packet) {
		if (receiver != null) {
			receiver.handlePacket(packet);
		}
	}
	
	public void setReceiver(PacketReceiver receiver) {
		this.receiver = receiver;
	}
	
	public void terminate() {
		isRunning = false;
		socket.close();
	}
}
