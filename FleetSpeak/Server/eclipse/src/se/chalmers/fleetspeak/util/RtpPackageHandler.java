package se.chalmers.fleetspeak.util;

import java.nio.ByteBuffer;
import java.util.Random;

public class RtpPackageHandler {

	private final static byte headerByte1 = 0x10;
	private final static byte headerByte2 = (byte)0;

	private short seqNumber = 1337;
	private int timestamp = 133700;
	private int ssrc;
	
	private long time = -1;
	private int timeInterval = -1;
	
	private RtpPackageHandler(int ssrc){
		this.ssrc = ssrc;
	}
	
	public static RtpPackageHandler createHandler(){
		return new RtpPackageHandler(new Random().nextInt());
	}
	
	public void setTimeInterval(int timeInterval){
		this.timeInterval = timeInterval;
	}
	
	public byte[] toRtpPackage(byte[] data){
		ByteBuffer rtpPackage = ByteBuffer.allocate(data.length+12);
		rtpPackage.put(headerByte1);
		rtpPackage.put(headerByte2);
		rtpPackage.putShort(seqNumber++);
		updateTimestamp();
		rtpPackage.putInt(timestamp);
		rtpPackage.putInt(ssrc);
		rtpPackage.put(data);
		
		return rtpPackage.array();
	}
	
	private void updateTimestamp(){
		if(timeInterval > 0){
			timestamp += timeInterval;
		}else{
			if(time > 0){
				timestamp += System.currentTimeMillis()-time;
			}
			time = System.currentTimeMillis();
		}
	}
}
