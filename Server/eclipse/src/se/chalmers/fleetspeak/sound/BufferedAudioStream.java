package se.chalmers.fleetspeak.sound;

public interface BufferedAudioStream {

	/**
	 * Reads the next available byte-array of audio data from the stream,
	 * if none is available it returns null instead.
	 * @return A byte-array of audio data if available, null if not.
	 */
	public byte[] read();
}
