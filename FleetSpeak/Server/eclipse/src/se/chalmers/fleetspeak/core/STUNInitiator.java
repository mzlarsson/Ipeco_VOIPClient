package se.chalmers.fleetspeak.core;

import java.net.DatagramSocket;
import java.net.SocketException;

import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.PortFactory;

/**
 * A class for initiating a UDP connection using STUN
 * (Simple Traversal of User Datagram Protocol
 * UDP through Network Address Translators NAT).
 *
 * @author Patrik Haar
 */
public class STUNInitiator implements CommandHandler{

	private TCPHandler tcp;
	private DatagramSocket udp;
	
	public STUNInitiator(TCPHandler tcp) {
		tcp.setCommandHandler(this);
		this.tcp = tcp;
		try {
			udp = new DatagramSocket(PortFactory.getInstance().getPort(), tcp.getInetAddress());
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void handleCommand(Command c) {
		// TODO Auto-generated method stub
		
	}

}
