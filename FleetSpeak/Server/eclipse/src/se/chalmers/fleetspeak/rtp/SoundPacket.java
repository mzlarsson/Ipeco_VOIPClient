package se.chalmers.fleetspeak.rtp;

import com.biasedbit.efflux.packet.DataPacket;

public class SoundPacket {

	private int sequenceNumber;
	private byte[] data;
	
	public void setData(DataPacket packet){
		sequenceNumber = packet.getSequenceNumber();
		data = packet.getDataAsArray();
	}
	
	public int getSequenceNumber(){
		return this.sequenceNumber;
	}
	
	public byte[] getData(int minSequenceNumber){
		if(minSequenceNumber<=sequenceNumber){
			return data;
		}else{
			return new byte[0];
		}
	}
}
