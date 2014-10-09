package se.chalmers.fleetspeak.rtp;

import java.io.IOException;
import java.net.InetAddress;

import com.biasedbit.efflux.packet.DataPacket;
import com.biasedbit.efflux.participant.RtpParticipantInfo;
import com.biasedbit.efflux.session.RtpSession;
import com.biasedbit.efflux.session.RtpSessionDataListener;

public class SoundHandler extends RTPHandler implements RtpSessionDataListener{
	
	private static final int PAYLOAD_TYPE = 0;		//http://www.iana.org/assignments/rtp-parameters/rtp-parameters.xml

	private SoundMixer mixer;
	private int currSeqNumber = 0;
	
	public SoundHandler(InetAddress clientIP, int serverPort) throws IOException{
		super(clientIP, serverPort, PAYLOAD_TYPE);
		RTPConnector.addDataListener(this);
		mixer = SoundMixer.getInstance();
	}

	@Override
	public void run() {
		while(this.isAlive()){
			RTPConnector.sendData(mixer.getMixedSound(getParticipant().getInfo(), currSeqNumber+1), getParticipant());
			currSeqNumber = mixer.getCurrentSequenceOffset();
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {}
		}
	}
	
	@Override
	public void terminate(){
		RTPConnector.removeDataListener(this);
		super.terminate();
	}
	
	@Override
	public void dataPacketReceived(RtpSession session, RtpParticipantInfo participant, DataPacket packet) {
		System.out.println("Server got packet: '"+new String(packet.getDataAsArray())+"'");
    }
}
