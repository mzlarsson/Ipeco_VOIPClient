package se.chalmers.fleetspeak.sound;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for retrieving instances of mixers.
 * @author Matz Larsson
 * @version 1.0
 */

public class MixerFactory {
	
	private static Map<String, Mixer> mixers = new HashMap<String, Mixer>();

	private MixerFactory(){}
	
	/**
	 * Retrieves the mixer with the given name. If none with that name is
	 * already started, a new one is created and started.
	 * @param name The name of the mixer
	 * @return A valid mixer instance
	 */
	public static Mixer getMixer(String name){
		Mixer m = mixers.get(name);
		if(m == null){
			m = createNewMixer();
			mixers.put(name, m);
		}
		
		return m;
	}
	
	/**
	 * Creates a new mixer and starts it.
	 * @return The new mixer
	 */
	private static Mixer createNewMixer(){
		return new OpusMixer();
	}
	
}
