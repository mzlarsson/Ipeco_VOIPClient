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
import se.chalmers.fleetspeak.core.NetworkUser;
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
 * Server: control-code --> Client (udp with repeated tries)
 * Client: "clientUdpTestOk" --> Server (tcp)
 * 
 * @author Patrik Haar
 */
public class STUNInitiator extends Thread implements CommandHandler{

	private NetworkUser tcp;
	private DatagramSocket udp;
	private CommandHandler ch;
	private byte ctrlCode;
	private int responseTimeoutTime = 2000, nbrOfResponseAttempts = 20;
	private long delayInMilliBetweenAttempts = 50;
	private volatile boolean udpVerified = false;
	private Logger logger;

	public STUNInitiator(NetworkUser nu, int connectionID) {
		super("STUNInitiator:id"+connectionID);
		logger = Logger.getLogger("Debug");
		this.tcp = nu;
		tcp.setCommandHandler(this);
		try {
			udp = new DatagramSocket(PortFactory.getInstance().getPort());
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		logger.log(Level.INFO, "Initiating STUN-protocol to client from port: " + udp.getLocalPort());
		ctrlCode = (byte)new Random().nextInt();
		boolean isWaitingForResponse = false;
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
				verificationPacket = new DatagramPacket(new byte[] {ctrlCode}, 1, udp.getRemoteSocketAddress());
				udpVerified = false;
				isWaitingForResponse = true;
				for(int i=0; !udpVerified && i<nbrOfResponseAttempts; i++) { // Sending the message multiple times due to possible packet-loss.
					udp.send(verificationPacket);
					Thread.sleep(delayInMilliBetweenAttempts);
				}
			} else {
				logger.log(Level.WARNING, "Control codes does not match, received code: " + verificationPacket.getData()[0]);
			}
		} catch (SocketTimeoutException e) {	// The UDP-packet from the client never made it to the server. 
			logger.log(Level.WARNING, "Timed out while waiting for udp response from client.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (udpVerified && isWaitingForResponse) { // UDP-connection correctly established.
			logger.log(Level.FINER, "UDP-connection successfully established on port: " + udp.getLocalPort()
					+ " to client: " + udp.getRemoteSocketAddress());
			ch.handleCommand(new Command("DatagramSocketSTUN", tcp, udp));
		} else {	// Failed to establish a connection.
			logger.log(Level.WARNING, "STUN protocol falied to establish a connection on port: " + udp.getLocalPort());
			udp.close();
			PortFactory.getInstance().freePort(udp.getLocalPort());
		}
	}

	public void addCommandHandler(CommandHandler ch) {
		this.ch = ch;
	}
	
	@Override
	public void handleCommand(Command c) {
		if(c.getCommand().equalsIgnoreCase("clientudptestok")) {
			logger.log(Level.FINER, "Client: " + udp.getRemoteSocketAddress() + " recieved testpacket from port: " + udp.getLocalPort());
			udpVerified = true;
		}
	}
}
