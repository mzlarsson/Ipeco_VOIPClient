package se.chalmers.fleetspeak.sound;

import java.net.InetAddress;

/**
 * Basic setup for a handler based on RTP.
 * NOTE: This class automatically registers the client to the correct RTPConnector.
 * 
 * NOTE: This class uses properties from the se.chalmers.fleetspeak.sound.Constants class.
 * 			* Constants.getServerIP()
 * 
 * @author Matz Larsson
 *
 */

public abstract class RTPHandler extends Thread{

	private RTPConnector connector;
	private long participantSourceID;

	/**
	 * Creates a new RTPHandler and registers a client to the RTPConnector class
	 * @param clientIP The IP of the client
	 * @param serverPort The port on the server to send/listen for RTP data.
	 * @param payloadType The expected payload type
	 * @throws IllegalArgumentException If Constants.getServerIP() contains null
	 */
	protected RTPHandler(InetAddress clientIP, int serverPort, int payloadType) throws IllegalArgumentException{
		if(Constants.getServerIP() == null){
			throw new IllegalArgumentException("The server IP has not yet been set");
		}
		
		RTPConnector connector = RTPConnector.getConnector(Constants.getServerIP(), serverPort, payloadType);	
		participantSourceID = connector.addParticipant(clientIP);
	}
	
	/**
	 * Retrieves the RTPConnector that this handler is registered to
	 * @return The RTPConnector that this handler is registered to
	 */
	protected RTPConnector getConnector(){
		return this.connector;
	}
	
	/**
	 * Retrieves the source ID of this client
	 * @return The source ID of this client
	 */
	protected long getParticipantSourceID(){
		return this.participantSourceID;
	}

	/**
	 * Closes this handler thread and unregisters the client from the RTPConnector.
	 */
	public void terminate(){
		connector.removeParticipant(participantSourceID);
		this.interrupt();
	}
}