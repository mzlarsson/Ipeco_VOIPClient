package se.chalmers.fleetspeak.rtp;

import com.biasedbit.efflux.packet.DataPacket;

public class SoundPacket {

	private int sequenceNumber;
	private int sequenceOffset;
	private byte[] data;
	
	public SoundPacket(int sequenceOffset){
		this.sequenceOffset = sequenceOffset;
	}
	
	public void setData(DataPacket packet){
		sequenceNumber = packet.getSequenceNumber();
		data = packet.getDataAsArray();
	}
	
	public int getRelativeSequenceNumber(){
		return this.sequenceNumber+this.sequenceOffset;
	}
	
	public byte[] getData(int minSequenceNumber){
		if(minSequenceNumber<=getRelativeSequenceNumber()){
			return data;
		}else{
			return new byte[0];
		}
	}
}
