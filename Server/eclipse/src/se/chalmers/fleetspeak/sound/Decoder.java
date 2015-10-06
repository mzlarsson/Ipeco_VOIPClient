package se.chalmers.fleetspeak.sound;

public interface Decoder {

	/**
	 * Attempt to decode the data, if null then it will try to predict what it was.
	 * @param indata Encoded audio to be decoded or null if packet was dropped.
	 * @return the decoded audio if successful, null if not.
	 */
	public byte[] decode(byte[] indata);
	
	/**
	 * Terminates the Decoder and frees used resources.
	 */
	public void terminate();
	
}
