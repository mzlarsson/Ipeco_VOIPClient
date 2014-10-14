package se.chalmers.fleetspeak.sound;

import java.net.InetAddress;

/**
 * Factory for creating SoundHandlers with different protocols. 
 * NOTE: Currently RTP are the only protocol that are supported.
 * 
 * @author Matz Larsson
 */

public class SoundHandlerFactory {

	//Used to disable instances of this class to be created
	private SoundHandlerFactory(){}
	
	/**
	 * Retrieves the default (recommended) SoundHandler and initiates it with given attributes
	 * @param clientIP The IP of the client to connect to
	 * @param serverPort The port that the server should send/listen data to/from
	 * @return A valid SoundHandler initiated with the given values
	 */
	public static SoundHandler getDefaultSoundHandler(InetAddress clientIP, int serverPort, int clientPort){
		return getSoundHandler(Protocol.RTP, clientIP, serverPort, clientPort);
	}
	
	/**
	 * Retrieves a SoundHandler using the given protocol and initiates it with given attributes
	 * @param protocol The protocol to use
	 * @param clientIP The IP of the client to connect to
	 * @param serverPort The port that the server should send/listen data to/from
	 * @return A valid SoundHandler initiated with the given values
	 */
	public static SoundHandler getSoundHandler(Protocol protocol, InetAddress clientIP, int serverPort, int clientPort){
		switch(protocol){
			case RTP: return new RTPSoundHandler(clientIP, serverPort, clientPort);
			default: return null;
		}
	}
	
	/**
	 * Enum for handling different types of protocols for sound handling.
	 * 
	 * @author Matz Larsson
	 */
	public enum Protocol{
		RTP;
	}
	
}
