package se.chalmers.fleetspeak.rtp;

import java.util.List;

import com.biasedbit.efflux.packet.DataPacket;
import com.biasedbit.efflux.participant.RtpParticipantInfo;

public class SoundPacket {

	private int sequenceNumber;
	private int sequenceOffset;
	private byte[] data;
	
	private RtpParticipantInfo participant;
	
	public SoundPacket(RtpParticipantInfo participant, int sequenceOffset){
		this.participant = participant;
		this.sequenceOffset = sequenceOffset;
	}
	
	public void setData(DataPacket packet){
		sequenceNumber = packet.getSequenceNumber();
		data = packet.getDataAsArray();
	}
	
	public RtpParticipantInfo getParticipant(){
		return this.participant;
	}
	
	public int getRelativeSequenceNumber(){
		return this.sequenceNumber+this.sequenceOffset;
	}
	
	public byte[] getData(int minSequenceNumber){
		if(minSequenceNumber<=getRelativeSequenceNumber() && data != null){
			return data;
		}else{
			return new byte[0];
		}
	}

	public static SoundPacket getPacket(List<SoundPacket> packets, RtpParticipantInfo participant){
		for(int i = 0; i<packets.size(); i++){
			if(packets.get(i).getParticipant().getSsrc() == participant.getSsrc()){
				return packets.get(i);
			}
		}
		
		return null;
	}
}
