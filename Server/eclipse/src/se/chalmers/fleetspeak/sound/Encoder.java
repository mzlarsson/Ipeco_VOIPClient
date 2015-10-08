package se.chalmers.fleetspeak.sound;

public interface Encoder {

	/**
	 * Attempt to encode the data.
	 * @param indata the data to be encoded.
	 * @return the encoded data.
	 */
	public byte[] encode(byte[] indata);
	
	/**
	 * Terminates the Encoder and frees used resources.
	 */
	public void terminate();
	
}
