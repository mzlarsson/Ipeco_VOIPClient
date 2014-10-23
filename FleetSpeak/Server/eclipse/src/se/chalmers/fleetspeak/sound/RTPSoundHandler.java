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
	protected RTPSoundHandler(InetAddress clientIP, int serverPort, int clientPort) throws IllegalArgumentException{
		super(clientIP, serverPort, clientPort, Constants.RTP_SOUND_PAYLOAD_TYPE);
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
	 * Sets the mute/unmute mode for a specific SoundHandler
	 * @param handler The handler to mute/unmute
	 * @param muted If the handler should be muted or unmuted
	 */
	@Override
	public void setMuted(SoundHandler handler, boolean muted){
		if(handler instanceof RTPSoundHandler){
			mixer.setMuted(getParticipantSourceID(), ((RTPSoundHandler)handler).getParticipantSourceID(), muted);
		}
	}

	/**
	 * Checks whether the client with the given SoundHandler is muted for this user
	 * @param handler The handler to check
	 * @return If the given handler is muted for this user
	 */
	@Override
	public boolean isMuted(SoundHandler handler){
		if(handler instanceof RTPSoundHandler){
			return mixer.isMuted(getParticipantSourceID(), ((RTPSoundHandler)handler).getParticipantSourceID());
		}else{
			return false;
		}
	}

	/**
	 * Runs the actual functionality of this handler. Sends a mix of all known sound source
	 * to the client with a set time interval. See Constants.RTP_UPDATE_INTERVAL.
	 */
	@Override
	public void run() {
		while(this.isAlive()){
			//Checks if connected to any room
			if(mixer != null){
				//Sync the most updated sequence number
				currSeqNumber = Math.max(mixer.getCurrentSequenceOffset(), currSeqNumber);
				//Fetch the data from all other sound sources
				byte[] data = mixer.getMixedSound(getParticipantSourceID(), currSeqNumber);
				//Send data
				if(data != null && data.length>0){
					if(getConnector().sendData(getParticipantSourceID(), data)){
						//Increase sequence number if any data was sent (no double sending)
						currSeqNumber++;
					}
				}
			}
			
			//Wait
			try {
				Thread.sleep(Constants.RTP_UPDATE_INTERVAL);
			} catch (InterruptedException e) {}
		}
	}
	
	/**
	 * Closes this handler and releases all resources.
	 */
	@Override
	public void terminate(){
		if(mixer != null){
			mixer.removeClientFromMixer(getParticipantSourceID());
		}
		
		super.terminate();
	}
}
