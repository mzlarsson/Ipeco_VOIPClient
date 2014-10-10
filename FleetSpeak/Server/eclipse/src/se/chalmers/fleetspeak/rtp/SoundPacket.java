package se.chalmers.fleetspeak.rtp;

import se.chalmers.fleetspeak.Log;

import com.biasedbit.efflux.packet.DataPacket;

public class SoundPacket {

	private int sequenceNumber;
	private int sequenceOffset;
	private byte[] data;
	
	private static int counter = 0;
	
	public SoundPacket(int sequenceOffset){
		this.sequenceOffset = sequenceOffset;
		counter++;
	}
	
	public void setData(DataPacket packet){
		sequenceNumber = packet.getSequenceNumber();
		data = packet.getDataAsArray();
	}
	
	public int getRelativeSequenceNumber(){
		return this.sequenceNumber+this.sequenceOffset;
	}
	
	public byte[] getData(int minSequenceNumber){
		//System.out.print("\t[MIN: "+minSequenceNumber+" CURR: "+getRelativeSequenceNumber()+"]");
		if(minSequenceNumber<=getRelativeSequenceNumber()){
			return data;
		}else{
			return new byte[0];
		}
	}
}
