package se.chalmers.fleetspeak.sound;

import java.util.concurrent.BlockingQueue;

public interface Mixer extends Runnable{

	/**
	 * Adds a stream to the mixer.
	 * @param stream Stream to add
	 * @param output The output queue to put data in
	 */
	public void addStream(BufferedAudioStream stream, BlockingQueue<byte[]> output);
	
	/**
	 * Removes a stream from the mixer.
	 * NOTE: This will not close the stream.
	 * @param stream The stream to remove
	 */
	public void removeStream(BufferedAudioStream stream);
	
	/**
	 * Closes the mixer and removes all input/output streams.
	 */
	public void close();
	
}
