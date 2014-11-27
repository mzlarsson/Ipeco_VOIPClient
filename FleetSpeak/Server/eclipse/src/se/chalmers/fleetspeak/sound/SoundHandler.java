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
	 * Changes the mixer that this SoundHandler is connected to.
	 * @param mixerID The ID of the mixer to use.
	 */
	public void switchMixer(int mixerID);
	
	
	
	/**
	 * Terminates the SoundHandler and releases resources
	 */
	public void terminate();
	
}
