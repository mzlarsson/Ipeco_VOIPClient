package se.chalmers.fleetspeak.rtp;
import java.io.IOException;
import java.net.InetAddress;

import se.chalmers.fleetspeak.TmpConnector;

import com.biasedbit.efflux.participant.RtpParticipant;

public abstract class RTPHandler extends Thread{
	
	private RtpParticipant participant;
	
	//TODO THIS MUST BE SET FOR TESTING!
	public static final String SERVER_IP = TmpConnector.SERVER;		//Only temporary solution

	public RTPHandler(InetAddress clientIP, int serverPort, int payloadType) throws IOException{
		if(!RTPConnector.isStarted()){
			RTPConnector.start(SERVER_IP, serverPort, payloadType);
		}
		
		participant = RTPConnector.addClient(clientIP);
	}
	
	protected RtpParticipant getParticipant(){
		return this.participant;
	}

	public void terminate(){
		RTPConnector.removeClient(participant);
		this.interrupt();
	}
}