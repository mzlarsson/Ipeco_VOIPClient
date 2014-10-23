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

	public static List<RTPSoundMixer> mixers = new ArrayList<RTPSoundMixer>();
	
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
		if(connector != null){
			connector.removeRTPListener(sourceID);
		}
		
		//Remove the collected data from the client (if found)
		RTPSoundPacket packet = RTPSoundPacket.getPacket(this.data, sourceID);
		if(packet != null){
			this.data.remove(packet);
		}

		if(this.data.isEmpty()){
			this.close();
		}
	}
	
	/**
	 * Mutes/Unmutes a client for another client
	 * @param sourceID The client that requests the mute/unmute
	 * @param muteID The client to mute/unmute
	 * @param muted If the client should be muted or unmuted
	 */
	public void setMuted(long sourceID, long muteID, boolean muted){
		RTPSoundPacket packet = RTPSoundPacket.getPacket(this.data, sourceID);
		if(packet != null){
			packet.setMuted(muteID, muted);
		}
	}
	
	/**
	 * Checks whether a client is muted for a certain client
	 * @param sourceID The client that is affected by the mute
	 * @param muteID The client that is muted/unmuted
	 * @return If the client with muteID as ID is muted for the client with sourceID as ID
	 */
	public boolean isMuted(long sourceID, long muteID){
		RTPSoundPacket packet = RTPSoundPacket.getPacket(this.data, sourceID);
		if(packet != null){
			return packet.isMuted(muteID);
		}else{
			return false;
		}
	}
	
	

	/**
	 * Retrieves a byte array describing the mixed sound of all clients except the given one
	 * @param sourceID The source ID of the client requesting the mix (this client's sound data will not be mixed)
	 * @param minSequenceNumber The minimum (relative) sequence number that is acceptable
	 * @return A sound mix of all clients except the one that requests the data
	 */	
	public byte[] getMixedSound(long sourceID, int minSequenceNumber){
		if(data.size()>1){
			RTPSoundPacket requester = RTPSoundPacket.getPacket(this.data, sourceID);
			byte[][] bytedata = new byte[data.size()-1][Constants.RTP_PACKET_SIZE];
			int foundUsers = 0;
			for(int i = 0; i<data.size(); i++){
				if(data.get(i) != null){
					if(data.get(i).getSourceID() != sourceID && !requester.isMuted(data.get(i).getSourceID())){
						bytedata[foundUsers] = data.get(i).getData(minSequenceNumber);
						foundUsers++;
					}
				}
			}
			
			
			
			if(foundUsers>0){
				if(foundUsers==1){
					return bytedata[0];
				}else{
					byte[] mix = new byte[Constants.RTP_PACKET_SIZE];
					short sum = 0;
					for(int i = 0; i<Constants.RTP_PACKET_SIZE; i++) {
						sum = 0;
						for(int j = 0; j<foundUsers; j++){
							if(bytedata[j].length>i){
								sum += byteToShort(bytedata[j][i]);
								sum = dynamicRangeCompression(cutNoise(sum),(short)4000);
								System.out.println(sum);
							}
						}
						
	//					sum /= bytedata.length;		//Lower all volume. IMPORTANT! This value effects MUCH!
		
						mix[i] = shortToByte((short)Math.max(-128, Math.min(127, sum)));
					}
					
					return mix;
				}
			}else{
				return new byte[0];
			}
		}else{
			return new byte[0];
		}
	}

	/**
	 * Decodes a PCM byte to a short
	 * @param b The byte to decode
	 * @return The decoded byte
	 */
	private short byteToShort(byte b){
		return PCMUtil.decode(b);
	}

	/**
	 * Encodes a PCM short to a byte
	 * @param s The short to encode
	 * @return The encoded short
	 */
	private byte shortToByte(short s){
		return PCMUtil.encode(s);
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
	 * A method that compress Short data linearly. When the sound data is over a certain threshold  the method compress it.
	 * @param s The short value to be compressed
	 * @param t The threshold 
	 * @return
	 */
	public static short dynamicRangeCompression(short s, short t){
		int range = Short.MAX_VALUE;
		if(Math.abs(s)>t && s!=0){
		short tmp = (short) ((s/Math.abs(s))*((float)((float)(range-t)/((float)(2*range)-t)*(Math.abs(s)-t))+t));
		return tmp;
		}else{
			return s;
		}
	}
	/**
	 * Cuts low amplitude noise 
	 * @param s The sound data
	 * @return The modified sound data
	 */
	public static short cutNoise(short s){
		if(Math.abs(s)<500){
			return 0;
		}else{
			if(s<0){
				return (short) (s-4000);
			}else{
				return (short) (s+4000);
			}
		}
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
