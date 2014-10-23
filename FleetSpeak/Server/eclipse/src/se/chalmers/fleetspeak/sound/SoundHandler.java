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
	 * Mutes/Unmutes the given SoundHandler for the current SoundHandler
	 * @param handler The handler to mute/unmute
	 * @param muted If mute or unmute
	 */
	public void setMuted(SoundHandler handler, boolean muted);
	
	/**
	 * Checks whether the given SoundHandler is muted for the current SoundHandler
	 * @param handler The handler to check
	 * @return If the given SoundHandler is muted
	 */
	public boolean isMuted(SoundHandler handler);
	
	
	/**
	 * Terminates the SoundHandler and releases resources
	 */
	public void terminate();
	
}
