package se.chalmers.fleetspeak.rtp;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.Locale;

import se.chalmers.fleetspeak.Log;

import com.biasedbit.efflux.participant.RtpParticipant;
import com.biasedbit.efflux.session.MultiParticipantSession;
import com.biasedbit.efflux.session.RtpSession;
import com.biasedbit.efflux.session.RtpSessionDataListener;

public class RTPConnector {

	private static final String SESSION_ID = "fleetspeak_connection";
	private static final int CLIENT_RTP_DATA_PORT = 1024;
	private static final int CLIENT_RTP_CTRL_PORT = 1025;
	
	private static RtpSession session;
	
	public static void start(String serverIP, int serverPort, int payloadType){
		System.out.println("[Starting IP @ "+serverIP+":"+serverPort+"]");
		if(session == null){
			RtpParticipant server = getParticipant(serverIP, serverPort, serverPort+1);
			session = new MultiParticipantSession(SESSION_ID, payloadType, server);
			
			session.init();
		}else{
			Log.log("[RTPConnector] Warning! Session has already been started.");
		}
	}
	
	public static RtpParticipant addClient(InetAddress clientIP){
		if(isStarted()){
			RtpParticipant client = getParticipant(clientIP, CLIENT_RTP_DATA_PORT, CLIENT_RTP_CTRL_PORT);
			if(client != null){
				session.addReceiver(client);
			}
			return client;
		}else{
			throw new IllegalArgumentException("[RTPConnector] Session has not been started.");
		}
	}
	
	public static void removeClient(RtpParticipant participant){
		if(isStarted() && participant != null){
			session.removeReceiver(participant);
			Log.log("UDP: "+session.getRemoteParticipants().size());
		}else{
			throw new IllegalArgumentException("[RTPConnector] Session has not been started.");
		}
	}
	
	public static void addDataListener(RtpSessionDataListener listener){
		if(isStarted()){
			session.addDataListener(listener);
		}else{
			throw new IllegalArgumentException("[RTPConnector] Session has not been started.");
		}
	}
	
	public static void removeDataListener(RtpSessionDataListener listener){
		if(isStarted()){
			session.removeDataListener(listener);
		}else{
			throw new IllegalArgumentException("[RTPConnector] Session has not been started.");
		}
	}
	
	public static boolean isStarted(){
		return session != null;
	}
	
	public static boolean sendData(byte[] data, RtpParticipant participant){
		if(isStarted()){
			long timestamp = Calendar.getInstance(Locale.GERMANY).getTime().getTime();
			session.sendData(data, timestamp, true, participant);		//TODO Fix the boolean value?
			return data.length>0;
		}else{
			throw new IllegalArgumentException("[RTPConnector] Session has not been started.");
		}
	}

	private static RtpParticipant getParticipant(InetAddress ip, int dataport, int ctrlport){
		return getParticipant(ip.getHostAddress(), dataport, ctrlport);
	}
	
	private static RtpParticipant getParticipant(String ip, int dataport, int ctrlport){
		return RtpParticipant.createReceiver(ip, dataport, ctrlport);
	}
	
}
