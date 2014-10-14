package se.chalmers.fleetspeak.sound;

/**
 * Interface for setting up the basic needs for SoundHandlers.
 * 
 * @author Matz Larsson
 *
 */

public interface SoundHandler {

	/**
	 * Starts the SoundHandler
	 */
	public void start();
	
	/**
	 * Terminates the SoundHandler and releases resources
	 */
	public void terminate();
	
}
