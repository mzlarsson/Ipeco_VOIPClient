package se.chalmers.fleetspeak.sound;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * A class for containing the most recent data in a RTP connection.
 * 
 * @author Matz Larsson
 */

public class RTPSoundPacket {
	
	private int sequenceNumber;
	private int sequenceOffset;
	private byte[] data;
	
	StringBuffer log = new StringBuffer();
	
	private long sourceID;
	
	/**
	 * Creates a new instance that is assigned with basic attributes
	 * @param sourceID The source ID (given by RTPConnector) of the client that sent this data
	 * @param sequenceOffset The number of packets the client is behind when connecting
	 */
	public RTPSoundPacket(long sourceID, int sequenceOffset){
		this.sourceID = sourceID;
		this.sequenceOffset = sequenceOffset;
	}
	
	/**
	 * Updates the current data that is stored
	 * @param sequenceNumber The sequenceNumber of the packet that was received
	 * @param data The data that was received
	 */
	public void setData(int sequenceNumber, byte[] data){
		if(this.data==null){
			sequenceOffset -= sequenceNumber;
		}

		this.sequenceNumber = sequenceNumber;
		this.data = data;
		
		for(int i = 0; i<data.length; i++){
			System.out.println(Integer.toBinaryString((int)data[i])+" --> "+((int)data[i]));
			log.append((int)data[i]).append(" ");
		}
	}
	
	public void saveLog(String folder){
		String filename = folder+this.sourceID+".log";
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			out.write(log.toString());
			out.flush();
			out.close();
		}catch(IOException ioe){
			System.out.println("Could not write to file: "+filename);
		}
	}
	
	/**
	 * The source ID (given by RTPConnector) of the client bound to this data
	 * @return The source ID of the client bound to this data
	 */
	public long getSourceID(){
		return this.sourceID;
	}
	
	/**
	 * Retrieves the relative sequence number (sequence number is synced to consider the
	 * amount of packets it was behind when connecting.)
	 * @return The relative sequence number
	 */
	public int getRelativeSequenceNumber(){
		return this.sequenceNumber+this.sequenceOffset;
	}
	
	/**
	 * Retrieves the most recent data if the sequence number is greater than the given one.
	 * Otherwise an empty byte array with length 0.
	 * @param minSequenceNumber The minimum acceptable sequence number
	 * @return The most recent data. Empty byte array data (length=0) if the data is outdated.
	 */
	public byte[] getData(int minSequenceNumber){
		if(data != null){	//minSequenceNumber<=getRelativeSequenceNumber() && 
			return data;
		}else{
			return new byte[0];
		}
	}

	/**
	 * Searches for a packet by the given source ID
	 * @param packets The list of packets to search through
	 * @param sourceID The source ID to search for
	 * @return The packet with the given source ID. null if not found.
	 */
	public static RTPSoundPacket getPacket(List<RTPSoundPacket> packets, long sourceID){
		for(int i = 0; i<packets.size(); i++){
			if(packets.get(i).getSourceID() == sourceID){
				return packets.get(i);
			}
		}
		
		return null;
	}
}
