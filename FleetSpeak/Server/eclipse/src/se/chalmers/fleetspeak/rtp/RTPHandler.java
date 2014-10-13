package se.chalmers.fleetspeak.rtp;
import java.io.IOException;
import java.net.InetAddress;

import com.biasedbit.efflux.participant.RtpParticipant;

public abstract class RTPHandler extends Thread{
	
	private RtpParticipant participant;
	
	private static String SERVER_IP = "127.0.0.1";		//Only temporary solution

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
	
	
	public static void setServerIP(String serverIP){
		RTPHandler.SERVER_IP = serverIP;
	}
}