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
	private static int PACKET_SIZE = 172;
	
	public UDPHandler(DatagramSocket socket) {
		super("RTPHandler:port"+socket.getLocalPort());
		logger = Logger.getLogger("Debug");
		this.socket = socket;
		receivePacket = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE);
	}

	@Override
	public void run() {
		isRunning = true;
		logger.log(Level.INFO, "Listening to UDP traffic on port: " + socket.getLocalPort());
		while(isRunning) {
			try {
				socket.receive(receivePacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
