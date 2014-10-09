package se.chalmers.fleetspeak.rtp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import se.chalmers.fleetspeak.Client;

import com.biasedbit.efflux.packet.DataPacket;
import com.biasedbit.efflux.participant.RtpParticipantInfo;
import com.biasedbit.efflux.session.RtpSession;

public class SoundHandler extends RTPHandler{
	
	private static final int PAYLOAD_TYPE = 0;		//http://www.iana.org/assignments/rtp-parameters/rtp-parameters.xml

	public SoundHandler(InetAddress clientIP, int serverPort) throws IOException{
		super(clientIP, serverPort, PAYLOAD_TYPE);
	}
	
	@Override
	public void dataPacketReceived(RtpSession session, RtpParticipantInfo participant, DataPacket packet) {
		System.out.println("Server got packet: '"+new String(packet.getDataAsArray())+"'");
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
