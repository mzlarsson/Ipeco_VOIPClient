package se.chalmers.fleetspeak.sound;

/**
 * Factory for retrieving instances of mixers.
 * @author Matz Larsson
 * @version 1.0
 */

public class MixerFactory {

	private MixerFactory(){}
	
	/**
	 * Retrieves the mixer with the given name. If none with that name is
	 * already started, a new one is created and started.
	 * @param name The name of the mixer
	 * @return A valid mixer instance
	 */
	public static Mixer getDefaultMixer(){
		return new OpusMixer(40);
	}
	
}
