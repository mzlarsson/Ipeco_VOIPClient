package se.chalmers.fleetspeak.network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;

import se.chalmers.fleetspeak.core.CommandHandler;
import se.chalmers.fleetspeak.network.tcp.TCPHandler;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.PortFactory;

/**
 * A class for initiating a UDP connection using STUN
 * (Simple Traversal of User Datagram Protocol
 * UDP through Network Address Translators NAT).
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

	public STUNInitiator(TCPHandler tcp, int connectionID) {
		super("STUNInitiator:id"+connectionID);
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
		ctrlCode = (byte)new Random().nextInt();
		tcp.sendCommand(new Command("initiateSoundPort", udp.getLocalPort(), ctrlCode));
		DatagramPacket verificationPacket = new DatagramPacket(new byte[1], 1);
		try {
			udp.setSoTimeout(responseTimeoutTime); // If it exceeds this time we assume the response packet was blocked on the way.
			udp.receive(verificationPacket);
			if (verificationPacket.getData()[0]==ctrlCode) {
				udp.connect(verificationPacket.getSocketAddress());
				tcp.sendCommand(new Command("soundPortTest", ctrlCode, null));
				verificationPacket = new DatagramPacket(new byte[] {ctrlCode}, 1, udp.getRemoteSocketAddress());
				isWaitingForResponse = true;
				for(int i=0; isWaitingForResponse && i<nbrOfResponseAttempts; i++) { // Sending the message multiple times due to possible packet-loss.
					udp.send(verificationPacket);
					Thread.sleep(delayInMilliBetweenAttempts);
				}
			}
		} catch (SocketTimeoutException e) {
			// TODO Let the client know that his response never made it to the server.
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void handleCommand(Command c) {
		if(isWaitingForResponse && c.getCommand().equalsIgnoreCase("receivedporttestpacket")) {
			if ((byte)c.getKey() == ctrlCode) {
				isWaitingForResponse = false;
				// FIXME Needs to let ClientCreator know of the success
			}
		}
	}
}
