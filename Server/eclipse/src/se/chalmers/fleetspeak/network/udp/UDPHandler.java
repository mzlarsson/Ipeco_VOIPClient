package se.chalmers.fleetspeak.network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.util.PortFactory;

/**
 * Handles incoming and outgoing UDP traffic to a client.
 *
 * @author Patrik Haar
 */
public class UDPHandler extends Thread{

	private DatagramSocket socket;
	private volatile boolean isRunning = false;
	private Logger logger;
	private DatagramPacket receivePacket, outgoingPacket;
	private PacketReceiver receiver;
	
	/**
	 * Constructor for the UDPHandler.
	 * @param socket The DatagramSocket already connected to the remote client.
	 * @param packetSize The size of the incoming packets.
	 */
	public UDPHandler(DatagramSocket socket, int packetSize) {
		super("UDPHandler:port"+socket.getLocalPort());
		logger = Logger.getLogger("Debug");
		this.socket = socket;
		receivePacket = new DatagramPacket(new byte[packetSize], packetSize);
		if (socket.isConnected()) {
			outgoingPacket = new DatagramPacket(new byte[packetSize], packetSize, socket.getRemoteSocketAddress());
		} else {
			throw new IllegalArgumentException("The socket is not connected to a remote adress.");
		}
	}

	@Override
	public void run() {
		isRunning = true;
		logger.log(Level.INFO, "Listening to UDP traffic on port: " + socket.getLocalPort());
		while(isRunning) {
			try {
				socket.receive(receivePacket);
			} catch (IOException e) {
				if (!isRunning) {
					logger.log(Level.INFO, "Stopped listening on socket: " + this.getName());
				} else {
					logger.log(Level.WARNING, "UDP-socket " + this.getName() + " caught an exception");					
				}
			}
			handlePacket(receivePacket.getData());			
		}
	}
	
	/**
	 * Sends the given byte-array-packet to the address connected to the socket.
	 * @param packet byte-array of the data to be sent.
	 */
	public void sendPacket(byte[] packet) {
		outgoingPacket.setData(packet);
		try {
			if(!socket.isClosed()) {
				socket.send(outgoingPacket);
			} else {
				logger.log(Level.WARNING, "UDP-socket " + this.getName() + " tried to send when the socket is closed");
			}
		} catch (IOException e) {
			logger.log(Level.WARNING, "Could not send UDP packet ("+(packet!=null?"not ":"")+" null) : "+e.getMessage());
		}
	}
	
	private void handlePacket(byte[] packet) {
		if (receiver != null) {
			receiver.handlePacket(packet);
		}
	}
	
	/**
	 * Set the size of the packets the UDPHandler will read from the incoming udp-traffic.
	 * @param packetSize The size of the read-array in number of bytes.
	 */
	public void setPacketSize(int packetSize) {
		receivePacket.setData(new byte[packetSize]);
	}
	
	/**
	 * Sets where the UDPHandler sends incoming packets.
	 * @param receiver The receiver to which incoming traffic will be sent.
	 */
	public void setReceiver(PacketReceiver receiver) {
		this.receiver = receiver;
	}
	
	/**
	 * Free active resources and terminate the connection.
	 */
	public void terminate() {
		isRunning = false;
		PortFactory.getInstance().freePort(socket.getLocalPort());
		socket.close();
	}
}
