package se.chalmers.fleetspeak.network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.core.CommandHandler;
import se.chalmers.fleetspeak.network.tcp.TCPHandler;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.PortFactory;

/**
 * A class for initiating a UDP connection using STUN
 * (Simple Traversal of User Datagram Protocol
 * UDP through Network Address Translators NAT).
 *
 * Initiation process:
 * Server: "initiateSoundPort" + port + control-code --> Client (tcp)
 * Client: control-code --> Server (udp)
 * Server: "udpTestPacket" + control-code --> Client (tcp)
 * Server: control-code --> Client (udp with repeated tries)
 * Client: "UDPTestOk" --> Server (udp 1+ tries)
 * 
 * @author Patrik Haar
 */
public class STUNInitiator extends Thread implements CommandHandler{

	private TCPHandler tcp;
	private DatagramSocket udp;
	private byte ctrlCode;
	private int responseTimeoutTime = 2000, nbrOfResponseAttempts = 10;
	private long delayInMilliBetweenAttempts = 50;
	private volatile boolean isWaitingForResponse;
	private Logger logger;

	public STUNInitiator(TCPHandler tcp, int connectionID) {
		super("STUNInitiator:id"+connectionID);
		logger = Logger.getLogger("Debug");
		tcp.setCommandHandler(this);
		this.tcp = tcp;
		try {
			udp = new DatagramSocket(PortFactory.getInstance().getPort());
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		logger.log(Level.INFO, "Initiating STUN-protocol to client: " + tcp.getInetAddress() + " from port: " + udp.getLocalPort());
		ctrlCode = (byte)new Random().nextInt();
		tcp.sendCommand(new Command("initiateSoundPort", udp.getLocalPort(), ctrlCode));
		DatagramPacket verificationPacket = new DatagramPacket(new byte[1], 1);
		logger.log(Level.FINER, "Sent control code to client and waiting for response on port: " + udp.getLocalPort());
		try {
			udp.setSoTimeout(responseTimeoutTime); // If it exceeds this time we assume the response packet was blocked on the way.
			udp.receive(verificationPacket);
			udp.setSoTimeout(0);
			if (verificationPacket.getData()[0]==ctrlCode) {
				udp.connect(verificationPacket.getSocketAddress());
				logger.log(Level.FINER, "Sending test udp-packets to client: " + udp.getRemoteSocketAddress() + " from port: " + udp.getLocalPort());
				tcp.sendCommand(new Command("udpTestPacket", ctrlCode, null));
				verificationPacket = new DatagramPacket(new byte[] {ctrlCode}, 1, udp.getRemoteSocketAddress());
				isWaitingForResponse = true;
				for(int i=0; isWaitingForResponse && i<nbrOfResponseAttempts; i++) { // Sending the message multiple times due to possible packet-loss.
					udp.send(verificationPacket);
					Thread.sleep(delayInMilliBetweenAttempts);
				}
			}
		} catch (SocketTimeoutException e) {	// The UDP-packet from the client never made it to the server. 
			// TODO Let the client know that his response never made it to the server.
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!isWaitingForResponse) { // UDP-connection correctly established.
			logger.log(Level.FINER, "UDP-connection successfully established on port: " + udp.getLocalPort()
					+ " to client: " + udp.getRemoteSocketAddress());
			// TODO Return the socket to ClientCreator
		} else {	// Failed to establish a connection.
			logger.log(Level.WARNING, "STUN protocol falied to establish a connection on port: " + udp.getLocalPort());
			udp.close();
			PortFactory.getInstance().freePort(udp.getLocalPort());
		}
	}

	@Override
	public void handleCommand(Command c) {
		if(isWaitingForResponse && c.getCommand().equalsIgnoreCase("udptestok")) {
			if ((byte)c.getKey() == ctrlCode) {
				logger.log(Level.FINER, "Client: " + udp.getRemoteSocketAddress() + " recieved testpacket from port: " + udp.getLocalPort());
				isWaitingForResponse = false;
			}
		}
	}
}
