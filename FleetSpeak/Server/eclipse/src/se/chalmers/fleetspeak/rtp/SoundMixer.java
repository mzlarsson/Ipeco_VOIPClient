package se.chalmers.fleetspeak.rtp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.biasedbit.efflux.packet.DataPacket;
import com.biasedbit.efflux.participant.RtpParticipant;
import com.biasedbit.efflux.participant.RtpParticipantInfo;
import com.biasedbit.efflux.session.RtpSession;
import com.biasedbit.efflux.session.RtpSessionDataListener;

public class SoundMixer implements RtpSessionDataListener{

	private static SoundMixer instance;
	
	private Map<RtpParticipantInfo, SoundPacket> data;
	
	private SoundMixer(){
		data = new HashMap<RtpParticipantInfo, SoundPacket>();
		RTPConnector.addDataListener(this);
	}
	
	public static SoundMixer getInstance(){
		if(instance == null){
			instance = new SoundMixer();
		}
		
		return instance;
	}

	public byte[] getMixedSound(RtpParticipantInfo client, int minSequenceNumber){
		List<RtpParticipantInfo> participants = new ArrayList<RtpParticipantInfo>(data.keySet());
		if(participants.size()>0){
			System.out.println("Getting mixed sound: "+minSequenceNumber);
			byte[] output = new byte[100];		//FIXME fix the set size
			byte[] tmp = null;
			for(int i = 0; i<participants.size(); i++){
				if(participants.get(i) != client){
					tmp = data.get(participants.get(i)).getData(minSequenceNumber);
					for(int j = 0; j<tmp.length&&j<output.length; j++){
						output[j] = (byte)((output[j]+tmp[j])/2);
					}
				}
			}
			
			for(int i = 0; i<output.length; i++){
				if(output[i]!=0){
					return output;
				}
			}
			
			return new byte[0];
		}else{
			return new byte[0];
		}
	}
	
	@Override
	public void dataPacketReceived(RtpSession session, RtpParticipantInfo participant, DataPacket packet) {
		SoundPacket soundPacket = data.get(participant);
		if(soundPacket == null){
			soundPacket = new SoundPacket(getCurrentSequenceOffset());
		}
		soundPacket.setData(packet);
		
		data.put(participant, soundPacket);
		System.out.println("PACKET: '"+new String(packet.getDataAsArray())+"'");
	}
	
	public int getSequenceNumber(RtpParticipant participant){
		SoundPacket p = data.get(participant.getInfo());
		if(p != null){
			return p.getRelativeSequenceNumber();
		}else{
			return 0;
		}
	}
	
	public int getCurrentSequenceOffset(){
		int offset = 0;
		for(SoundPacket packet : data.values()){
			offset = Math.max(offset, packet.getRelativeSequenceNumber());
		}
		
		return offset;
	}
}
