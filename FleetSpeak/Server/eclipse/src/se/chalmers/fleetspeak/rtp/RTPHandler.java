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

public abstract class RTPHandler implements ConnectionHandler{
	
	private RtpSession session;

	public RTPHandler(InetAddress ip, int port, int payloadType) throws IOException{
		init(ip, port, payloadType);
	}
	
	public void init(final InetAddress ip, final int port, final int payloadType) throws IOException{
		fml();
	}
    	
	public void fml(){
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				initRTPSession();

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
	
	private void initRTPSession(){
		String sessionid = "uid_here"; // you need to set this

		RtpParticipant server = getParticipant(TmpConnector.SERVER, 1028, 1029);
		RtpParticipant client = getParticipant(TmpConnector.SERVER, 1024, 1025);
		session = new MultiParticipantSession(sessionid, 0, server);
		
		session.addReceiver(client);
		
		session.addDataListener(new RtpSessionDataListener() {
		    @Override
		    public void dataPacketReceived(RtpSession session, RtpParticipantInfo participant, DataPacket packet) {
		    	System.out.println("Server got packet: '"+new String(packet.getDataAsArray())+"'");
		    }
		});

		session.init();
	}
	private RtpParticipant getParticipant(InetAddress ip, int dataport, int ctrlport){
		return getParticipant(ip.getHostAddress(), dataport, ctrlport);
	}
	private RtpParticipant getParticipant(String ip, int dataport, int ctrlport){
		return RtpParticipant.createReceiver(ip, dataport, ctrlport);
	}

	public void close(){
		session.terminate();
	}
}