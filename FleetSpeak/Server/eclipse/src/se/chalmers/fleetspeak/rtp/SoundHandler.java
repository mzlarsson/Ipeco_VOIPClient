package se.chalmers.fleetspeak.rtp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import javax.media.rtp.event.ReceiveStreamEvent;

import se.chalmers.fleetspeak.Client;

public class SoundHandler extends RTPHandler{

	public SoundHandler(InetAddress ip, int port) throws IOException{
		super(ip, port);
	}
	
	@Override
	public void onClientConnect(List<Client> clients){
		System.out.println("[SoundHandler] Connect Notification");
	}
	
	@Override
	public void onClientDisconnect(List<Client> clients){
		System.out.println("[SoundHandler] Disconnect Notification");
	}

	@Override
	public void update(ReceiveStreamEvent data) {
		System.out.println("Got data!");
	}
}
