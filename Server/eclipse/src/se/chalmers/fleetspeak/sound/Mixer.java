package se.chalmers.fleetspeak.sound;

public interface Mixer {

	/**
	 * Adds a stream to the mixer.
	 * @param stream Stream to add
	 */
	public void addStream(BufferedAudioStream stream);
	
	/**
	 * Removes a stream from the mixer.
	 * NOTE: This will not close the stream.
	 * @param stream The stream to remove
	 */
	public void removeStream(BufferedAudioStream stream);


	/**
	 * Returns the mixed byte array of data retrieved from all registered BufferedAudioStreams to the mixer. Each channel
	 * contains the sound of all but the sound stream on that index.
	 * @return An array of the mixed sound
	 */
	public byte[][] getMixed();
	
}
