package se.chalmers.fleetspeak.rtp;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

import se.chalmers.fleetspeak.ConnectionHandler;
import se.chalmers.fleetspeak.TmpConnector;

import com.biasedbit.efflux.packet.DataPacket;
import com.biasedbit.efflux.participant.RtpParticipant;
import com.biasedbit.efflux.participant.RtpParticipantInfo;
import com.biasedbit.efflux.session.MultiParticipantSession;
import com.biasedbit.efflux.session.RtpSession;
import com.biasedbit.efflux.session.RtpSessionDataListener;

public abstract class RTPHandler implements ConnectionHandler, RtpSessionDataListener{
	
	private RtpSession session;
	private static int CLIENT_RTP_DATA_PORT = 1024;
	private static int CLIENT_RTP_CTRL_PORT = 1025;
	
	//TODO THIS MUST BE SET FOR TESTING!
	public static final String SERVER_IP = TmpConnector.SERVER;		//Only temporary solution

	public RTPHandler(InetAddress clientIP, int serverPort, int payloadType) throws IOException{
		initRTPSession(clientIP, serverPort, payloadType);
		startHandler();
	}
	
	private void initRTPSession(InetAddress clientIP, int serverPort, int payloadType){
		String sessionid = "fleetspeak_connection"; // you need to set this
		
		RtpParticipant server = getParticipant(SERVER_IP, serverPort, serverPort+1);
		RtpParticipant client = getParticipant(clientIP, CLIENT_RTP_DATA_PORT, CLIENT_RTP_CTRL_PORT);
		session = new MultiParticipantSession(sessionid, payloadType, server);
		
		session.addReceiver(client);
		session.addDataListener(this);
		session.init();
	}
	private RtpParticipant getParticipant(InetAddress ip, int dataport, int ctrlport){
		return getParticipant(ip.getHostAddress(), dataport, ctrlport);
	}
	private RtpParticipant getParticipant(String ip, int dataport, int ctrlport){
		return RtpParticipant.createReceiver(ip, dataport, ctrlport);
	}
    	
	public void startHandler(){
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Scanner in = new Scanner(System.in);
				String s = "";
				while(true){
					s = in.nextLine();
					session.sendData(s.getBytes(), 123456789, true);
				}
			}
		});
		thread.start();
	}
	

	public void close(){
		session.terminate();
	}
}