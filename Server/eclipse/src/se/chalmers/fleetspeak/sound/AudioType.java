package se.chalmers.fleetspeak.sound;

/**
 * The format of the different AudioTypes supported.
 *
 * @author Patrik Haar
 */
public enum AudioType {

	PCMU(0, 320, 20),
	OPUS_WB(80, 80, 20),
	OPUS_NB(81, 40, 20),
	NONE(-1, 0, 0);
	
	private final int payloadType;
	private final int maxLength;
	private final int framesizeMs;
	
	private AudioType(int payloadType, int maxLength, int framesizeMs) {
		this.payloadType = payloadType;
		this.maxLength = maxLength;
		this.framesizeMs = framesizeMs;
	}
	
	/**
	 * Finds the AudioType of the given payload type
	 * @param payloadType The payloadType according to the conventions in RTP headers
	 * @return The matching AudioType
	 */
	public static AudioType getAudioType(int payloadType) {
		for(AudioType at : AudioType.values()) {
			if(at.getPayloadType() == payloadType) {
				return at;
			}
		}
		return NONE;
	}
	
	/**
	 * The payloadType is according to the conventions in RTP headers
	 * @return The payload type
	 */
	public int getPayloadType() {
		return payloadType;
	}
	
	/**
	 * The maximum length of this audiotype in nbr of bytes
	 * @return The max. nbr of bytes
	 */
	public int getMaxLength() {
		return maxLength;
	}

	/**
	 * The time between each sample/packet
	 * @return The time between each sample/packet
	 */
	public int getFramesizeMs() {
		return framesizeMs;
	}
}
