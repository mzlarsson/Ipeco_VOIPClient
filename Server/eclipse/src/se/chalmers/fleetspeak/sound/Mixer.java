package se.chalmers.fleetspeak.sound;

public interface Mixer {

	/**
	 * Adds a stream to the mixer.
	 * @param stream Stream to add
	 */
	public void addStream(BufferedStream stream);
	
	/**
	 * Removes a stream from the mixer.
	 * NOTE: This will not close the stream.
	 * @param stream The stream to remove
	 */
	public void removeStream(BufferedStream stream);


	/**
	 * Returns the mixed byte array of data retrieved from all registered BufferedStreams to the mixer. Each channel
	 * contains the sound of all but the sound stream on that index.
	 * @param nbrOfBytes The number of bytes to mix together
	 * @return An array of the mixed sound
	 */
	public byte[][] getMixed(int nbrOfBytes);
	
}
