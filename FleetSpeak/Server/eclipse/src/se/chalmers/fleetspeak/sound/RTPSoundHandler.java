package se.chalmers.fleetspeak.sound;

import java.net.InetAddress;

/**
 * A subclass to the RTPHandler specialized in mixing RTP messages into one sound array
 * and sending this to the client.
 * NOTE: This class has a close cooperation with the RTPSoundMixer class.
 * 
 * NOTE: This class uses properties from the se.chalmers.fleetspeak.sound.Constants class.
 * 			* Constants.RTP_SOUND_PAYLOAD_TYPE
 * 			* Constants.RTP_UPDATE_INTERVAL
 * 
 * @author Matz Larsson.
 */

public class RTPSoundHandler extends RTPHandler implements SoundHandler{
	
	private RTPSoundMixer mixer;
	private int currSeqNumber = 0;
	
	/**
	 * Creates a new SoundHandler based on RTP with the given data.
	 * @param clientIP The IP of the client
	 * @param serverPort The port on the server to send/listen for RTP data.
	 * @throws IllegalArgumentException If Constants.getServerIP() contains null
	 */
	protected RTPSoundHandler(InetAddress clientIP, int serverPort) throws IllegalArgumentException{
		super(clientIP, serverPort, Constants.RTP_SOUND_PAYLOAD_TYPE);
		switchMixer(0);
	}
	
	/**
	 * Changes the mixer that this SoundHandler is connected to.
	 * @param mixerID The ID of the mixer to use.
	 */
	@Override
	public void switchMixer(int mixerID){
		if(mixer != null){
			mixer.removeClientFromMixer(getParticipantSourceID());
		}
		
		mixer = RTPSoundMixer.getSoundMixer(getConnector(), mixerID);
		mixer.addClientToMixer(getParticipantSourceID());
	}

	/**
	 * Runs the actual functionality of this handler. Sends a mix of all known sound source
	 * to the client with a set time interval. See Constants.RTP_UPDATE_INTERVAL.
	 */
	@Override
	public void run() {
		while(this.isAlive()){
			//Sync the most updated sequence number
			currSeqNumber = Math.max(mixer.getCurrentSequenceOffset(), currSeqNumber);
			//Fetch the data from all other sound sources
			byte[] data = mixer.getMixedSound(getParticipantSourceID(), currSeqNumber);
			//Send data
			if(getConnector().sendData(getParticipantSourceID(), data)){
				//Increase sequence number if any data was sent (no double sending)
				currSeqNumber++;
			}
			
			//Wait
			try {
				Thread.sleep(Constants.RTP_UPDATE_INTERVAL);
			} catch (InterruptedException e) {}
		}
	}
}
