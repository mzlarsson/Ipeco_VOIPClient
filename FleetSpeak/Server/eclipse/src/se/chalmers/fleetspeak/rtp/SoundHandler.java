package se.chalmers.fleetspeak.rtp;

import java.io.IOException;
import java.net.InetAddress;

public class SoundHandler extends RTPHandler{
	
	private static final int PAYLOAD_TYPE = 0;		//http://www.iana.org/assignments/rtp-parameters/rtp-parameters.xml

	private SoundMixer mixer;
	private int currSeqNumber = 0;
	
	public SoundHandler(InetAddress clientIP, int serverPort) throws IOException{
		super(clientIP, serverPort, PAYLOAD_TYPE);
		mixer = SoundMixer.getInstance();
	}

	@Override
	public void run() {
		while(this.isAlive()){
			currSeqNumber = Math.max(mixer.getCurrentSequenceOffset(), currSeqNumber);
			if(RTPConnector.sendData(mixer.getMixedSound(getParticipant().getInfo(), currSeqNumber), getParticipant())){
				currSeqNumber++;
			}
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {}
		}
	}
}
