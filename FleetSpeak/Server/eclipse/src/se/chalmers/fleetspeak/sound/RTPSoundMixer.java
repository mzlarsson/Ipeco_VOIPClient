package se.chalmers.fleetspeak.sound;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	private HashMap<Long,JitterBuffer> buffers;
	private RTPConnector connector;
	private int identifier;
	
	/**
	 * Creates a new mixer with the given RTPConnector
	 * @param connector The connector to bind to this mixer.
	 * @param identifier A unique identifier for this mixer
	 */
	private RTPSoundMixer(RTPConnector connector, int identifier){
		buffers = new HashMap<Long,JitterBuffer>();
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
		buffers.put(sourceID, new JitterBuffer());
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
		buffers.remove(sourceID);
		if(buffers.isEmpty()){
			this.close();
		}
	}
	
	
	
	
	

	/**
	 * Retrieves a byte array describing the mixed sound of all clients except the given one
	 * @param sourceID The source ID of the client requesting the mix (this client's sound data will not be mixed)
	 * @param minSequenceNumber The minimum (relative) sequence number that is acceptable
	 * @return A sound mix of all clients except the one that requests the data
	 */	
	public byte[] getMixedSound(long sourceID){
		if(buffers.size()>1){
			
			int foundUsers = 0;
			for(Long key : buffers.keySet()){
				if(key != sourceID)
					return buffers.get(key).read();
			
			}
		}
//			RTPSoundPacket requester = RTPSoundPacket.getPacket(this.data, sourceID);
//			byte[][] bytedata = new byte[data.size()-1][Constants.RTP_PACKET_SIZE];
//			int foundUsers = 0;
//			for(int i = 0; i<data.size(); i++){
//				if(data.get(i) != null){
//					if(data.get(i).getSourceID() != sourceID && requester != null && !requester.isMuted(data.get(i).getSourceID())){
//						bytedata[foundUsers] = data.get(i).getData(minSequenceNumber);
//						foundUsers++;
//					}
//				}
//			}
			
//			if(foundUsers>0){
//				if(foundUsers==1){
//					return bytedata[0];
//				}else{
//					byte[] mix = new byte[Constants.RTP_PACKET_SIZE];
//					short sum = 0;
//					for(int i = 0; i<Constants.RTP_PACKET_SIZE; i++) {
//						sum = 0;
//						for(int j = 0; j<foundUsers; j++){
//							if(bytedata[j].length>i){
//								sum += byteToShort(bytedata[j][i]);
//								sum = dynamicRangeCompression(cutNoise(sum),(short)4000);
//							}
//						}
//						
//	//					sum /= bytedata.length;		//Lower all volume. IMPORTANT! This value effects MUCH!
//		
//						mix[i] = shortToByte((short)Math.max(-128, Math.min(127, sum)));
//					}
//					
//					return mix;
//				}
//			}else{
//				return new byte[0];
//			}
		
		return new byte[0];
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
	public void dataPacketReceived(long sourceID, long timestamp, byte[] data) {
		
		buffers.get(sourceID).write(data, timestamp);
		
	}

	
	/**
	 * Removes all data from this mixer and untracks itself as a SoundMixer.
	 */
	public void close(){
		for(long l : buffers.keySet()){
			connector.removeRTPListener(l);
		}
		
		buffers.clear();
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
