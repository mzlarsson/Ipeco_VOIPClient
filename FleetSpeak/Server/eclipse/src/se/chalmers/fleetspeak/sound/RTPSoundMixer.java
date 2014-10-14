package se.chalmers.fleetspeak.sound;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for mixing sound based on RTP packages.
 * 
 * NOTE: This class uses properties from the se.chalmers.fleetspeak.sound.Constants class.
 * 			* Constants.RTP_PACKET_SIZE
 * 
 * INFO: Use the mixer in this way.
 * 			1. Make sure your client is added to RTPConnector as a participant
 * 			2. Call getMixer(connector, identifier) to retrieve a correct RTPSoundMixer instance
 * 			3. Call addClientToMixer(sourceID) with the source ID provided by RTPConnector
 * 			4. Call getMixedSound(sourceID, minSequenceNum) to fetch the current mixed sound data
 * 			(5. Call getCurrentSequenceOffset() to keep the sequence number sync in fase)
 * 
 * @author Matz Larsson
 *
 */

public class RTPSoundMixer implements RTPListener{

	private static List<RTPSoundMixer> mixers = new ArrayList<RTPSoundMixer>();
	
	private List<RTPSoundPacket> data;
	private RTPConnector connector;
	private int identifier;
	
	/**
	 * Creates a new mixer with the given RTPConnector
	 * @param connector The connector to bind to this mixer.
	 * @param identifier A unique identifier for this mixer
	 */
	private RTPSoundMixer(RTPConnector connector, int identifier){
		this.data = new ArrayList<RTPSoundPacket>();
		this.connector = connector;
		this.identifier = identifier;
	}
	
	/**
	 * Adds a client with the given source ID (provided by RTPConnector) to this mixer
	 * @param sourceID The source ID of the client (provided by RTPConnector)
	 */
	public void addClientToMixer(long sourceID){
		//Start listen for messages via RTP
		connector.setRTPListener(sourceID, this);
		
		//Adds an empty packet to be filled with data later
		data.add(new RTPSoundPacket(sourceID, getCurrentSequenceOffset()));
	}

	/**
	 * Removes the client with the given source ID (provided by RTPConnector) from this mixer
	 * @param sourceID The source ID of the client (provided by RTPConnector)
	 */
	public void removeClientFromMixer(long sourceID){
		//Stop listen for messages via RTP
		connector.removeRTPListener(sourceID);
		
		//Remove the collected data from the client (if found)
		RTPSoundPacket packet = RTPSoundPacket.getPacket(this.data, sourceID);
		if(packet != null){
			this.data.remove(packet);
		}
	}

	/**
	 * Retrieves a byte array describing the mixed sound of all clients except the given one
	 * @param sourceID The source ID of the client requesting the mix (this client's sound data will not be mixed)
	 * @param minSequenceNumber The minimum (relative) sequence number that is acceptable
	 * @return A sound mix of all clients except the one that requests the data
	 */
	public byte[] getMixedSound(long sourceID, int minSequenceNumber){
		if(data.size()>0){
			byte[] output = new byte[Constants.RTP_PACKET_SIZE];
			byte[] tmp = null;
			for(int i = 0; i<data.size(); i++){
				if(data.get(i).getSourceID() != sourceID){
					tmp = data.get(i).getData(minSequenceNumber);
					for(int j = 0; j<tmp.length&&j<output.length; j++){
						output[j] = (byte)((output[j]+tmp[j]));
					}
				}
			}

			return output;
		}else{
			return new byte[0];
		}
	}

	/**
	 * Called when a RTP packet has been received. Stores the data to be used later in the mix.
	 * @param sourceID The source ID of the client who sent the packet
	 * @param sequenceNumber The sequence number of the RTP packet
	 * @param data The data from the received packet
	 */
	@Override
	public void dataPacketReceived(long sourceID, int sequenceNumber, byte[] data) {
		RTPSoundPacket soundPacket = RTPSoundPacket.getPacket(this.data, sourceID);
		
		if(soundPacket != null){
			soundPacket.setData(sequenceNumber, data);
		}
	}
	
	/**
	 * Retrieves the current sequence number of the specified client
	 * @param source The source ID of the actual client (retrieved from RTPConnector)
	 * @return The current sequence number of the specified client. 0 if not found.
	 */
	public int getSequenceNumber(long sourceID){
		RTPSoundPacket p = RTPSoundPacket.getPacket(data, sourceID);
		if(p != null){
			return p.getRelativeSequenceNumber();
		}else{
			return 0;
		}
	}
	
	/**
	 * Retrieves the highest (relative) sequence number of the clients connected to this mixer
	 * @return The highest sequence number in this mixer
	 */
	public int getCurrentSequenceOffset(){
		int offset = 0;
		for(int i = 0; i<data.size(); i++){
			offset = Math.max(offset, data.get(i).getRelativeSequenceNumber());
		}
		
		return offset;
	}
	
	/**
	 * Removes all data from this mixer and untracks itself as a SoundMixer.
	 */
	public void close(){
		for(int i = 0; i<data.size(); i++){
			connector.removeRTPListener(data.get(i).getSourceID());
		}
		
		data.clear();
		connector = null;
		mixers.remove(this);
	}
	
	/**
	 * Retrieves the SoundMixer with the given connector and identifier. Creates a new instance
	 * with these attributes if no matching instance was found.
	 * @param connector The RTPConnector to use for the mixer
	 * @param identifier The identifier of the mixer
	 * @return A valid SoundMixer with the given connector and identifier
	 */
	public static RTPSoundMixer getSoundMixer(RTPConnector connector, int identifier){
		RTPSoundMixer mixer = findMixer(connector, identifier);
		if(mixer == null){
			mixer = new RTPSoundMixer(connector, identifier);
			mixers.add(mixer);
		}
		
		return mixer;
	}
	
	/**
	 * Searches for a SoundMixer with the given attributes. Returns null if none was found.
	 * @param connector The connector to use
	 * @param identifier The identifier to use
	 * @return A found SoundMixer with the given attributes. null if such instance was not found.
	 */
	private static RTPSoundMixer findMixer(RTPConnector connector, int identifier){
		for(int i = 0; i<mixers.size(); i++){
			if(mixers.get(i).identifier == identifier && mixers.get(i).connector.equals(connector)){
				return mixers.get(i);
			}
		}
		
		return null;
	}
}
