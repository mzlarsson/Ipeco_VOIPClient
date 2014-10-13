package se.chalmers.fleetspeak.rtp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.biasedbit.efflux.packet.DataPacket;
import com.biasedbit.efflux.participant.RtpParticipant;
import com.biasedbit.efflux.participant.RtpParticipantInfo;
import com.biasedbit.efflux.session.RtpSession;
import com.biasedbit.efflux.session.RtpSessionDataListener;

public class SoundMixer implements RtpSessionDataListener{

	private static SoundMixer instance;
	
	private List<SoundPacket> data;
	
	private SoundMixer(){
		data = new ArrayList<SoundPacket>();
		RTPConnector.addDataListener(this);
	}
	
	public static SoundMixer getInstance(){
		if(instance == null){
			instance = new SoundMixer();
		}
		
		return instance;
	}

	public byte[] getMixedSound(RtpParticipantInfo client, int minSequenceNumber){
		return new byte[0];
		/*if(data.size()>0){
			return data.get(0).getData(minSequenceNumber);
		}
		
		if(data.size()>0){
			byte[] output = new byte[160];		//FIXME fix the set size
			byte[] tmp = null;
			for(int i = 0; i<data.size(); i++){
				if(data.get(i).getParticipant() != client){
					tmp = data.get(i).getData(minSequenceNumber);
					for(int j = 0; j<tmp.length&&j<output.length; j++){
						output[j] = (byte)((output[j]+tmp[j]));
					}
				}
			}
			
			for(int i = 0; i<output.length; i++){
				if(output[i]==0){
					return Arrays.copyOf(output, i);
				}
			}

			return output;
		}else{
			return new byte[0];
		}*/
	}
	
	@Override
	public void dataPacketReceived(RtpSession session, RtpParticipantInfo participant, DataPacket packet) {
		if(data.size()>0&&participant.getSsrc()==data.get(0).getParticipant().getSsrc()){
			session.sendDataPacket(packet);
		}
		
		SoundPacket soundPacket = SoundPacket.getPacket(data, participant);
		if(soundPacket == null){
			soundPacket = new SoundPacket(participant, getCurrentSequenceOffset());
			addNewPacket(soundPacket);
		}
		soundPacket.setData(packet);
	}
	
	private void addNewPacket(SoundPacket packet){
		data.add(packet);
	}
	
	public int getSequenceNumber(RtpParticipant participant){
		SoundPacket p = SoundPacket.getPacket(data, participant.getInfo());
		if(p != null){
			return p.getRelativeSequenceNumber();
		}else{
			return 0;
		}
	}
	
	public int getCurrentSequenceOffset(){
		int offset = 0;
		for(int i = 0; i<data.size(); i++){
			offset = Math.max(offset, data.get(i).getRelativeSequenceNumber());
		}
		
		return offset;
	}
}
