package se.chalmers.fleetspeak.rtp;

import com.biasedbit.efflux.packet.DataPacket;

public class SoundPacket {

	private int sequenceNumber;
	private int sequenceOffset;
	private byte[] data;
	
	private int tmpNum;
	private static int counter = 0;
	
	public SoundPacket(int sequenceOffset){
		this.sequenceOffset = sequenceOffset;
		this.tmpNum = counter;
		counter++;
		System.out.println("CREATED PACKET offset="+sequenceOffset);
	}
	
	public void setData(DataPacket packet){
		System.out.println("PACKET CHANGED");
		sequenceNumber = packet.getSequenceNumber();
		data = packet.getDataAsArray();
	}
	
	public int getRelativeSequenceNumber(){
		return this.sequenceNumber+this.sequenceOffset;
	}
	
	public byte[] getData(int minSequenceNumber){
		//System.out.print("\t[MIN: "+minSequenceNumber+" CURR: "+getRelativeSequenceNumber()+" ID: "+tmpNum+"]");
		if(minSequenceNumber<=getRelativeSequenceNumber()){
			return data;
		}else{
			return new byte[0];
		}
	}
}
