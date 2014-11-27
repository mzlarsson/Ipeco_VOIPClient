package se.chalmers.fleetspeak.sound;

/**
 * Basic interface for listener who wishes to receive data from a RTP connection.
 * 
 * @author Matz Larsson
 */

public interface RTPListener {

	/**
	 * Called when a packet has been received
	 * @param sourceID The source ID of the client who sent the packet
	 * @param sequenceNumber The sequence number of the RTP packet
	 * @param data The data from the received packet
	 */
	public void dataPacketReceived(long sourceID, long timestamp, byte[] data);
	
}
