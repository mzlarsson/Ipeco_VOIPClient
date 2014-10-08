package se.chalmers.fleetspeak.rtp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import se.chalmers.fleetspeak.Client;

import com.biasedbit.efflux.packet.DataPacket;
import com.biasedbit.efflux.participant.RtpParticipant;
import com.biasedbit.efflux.participant.RtpParticipantInfo;
import com.biasedbit.efflux.session.RtpSession;
import com.biasedbit.efflux.session.RtpSessionDataListener;
import com.biasedbit.efflux.session.SingleParticipantSession;

public class SoundHandler extends RTPHandler{
	
	private static final int PAYLOAD_TYPE = 0;		//http://www.iana.org/assignments/rtp-parameters/rtp-parameters.xml

	public SoundHandler(InetAddress ip, int port) throws IOException{
		super(ip, port, PAYLOAD_TYPE);
		test();
	}
	
	public void test(){
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
