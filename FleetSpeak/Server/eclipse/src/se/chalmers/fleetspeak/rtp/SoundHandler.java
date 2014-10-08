package se.chalmers.fleetspeak.rtp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import se.chalmers.fleetspeak.Client;

public class SoundHandler extends RTPHandler{
	
	private static final int PAYLOAD_TYPE = 0;		//http://www.iana.org/assignments/rtp-parameters/rtp-parameters.xml

	public SoundHandler(InetAddress clientIP, int serverPort) throws IOException{
		super(clientIP, serverPort, PAYLOAD_TYPE);
	}
	
	@Override
	public void onClientConnect(List<Client> clients){
		System.out.println("[SoundHandler] Connect Notification");
	}
	
	@Override
	public void onClientDisconnect(List<Client> clients){
		System.out.println("[SoundHandler] Disconnect Notification");
	}
}
