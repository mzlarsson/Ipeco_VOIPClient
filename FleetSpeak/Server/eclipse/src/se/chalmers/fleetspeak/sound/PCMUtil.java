package se.chalmers.fleetspeak.sound;
/**
 * A class encoding and decoding PCMU audio. Based on the code on this site: http://dystopiancode.blogspot.se/2012/02/pcm-law-and-u-law-companding-algorithms.html
 * @author Fridgeridge
 *
 */
public class PCMUtil {
	
	public static final short MYLAW_MAX = 0x1FFF;
	public static final short MYLAW_BIAS = 33;
	
	public short decodePCM(byte encodedPackage){
		byte sign = 0, position = 0;
		short decoded = 0;
		encodedPackage = (byte) ~encodedPackage;
		
		if(encodedPackage-0x80<0){
			encodedPackage &= ~(1 << 7);
			sign = -1;
		}
		position = (byte) (((encodedPackage & 0xF0) >> 4)+5);
		decoded = (short) (((1<<position)|((encodedPackage & 0x0f) << (position -4))|(1 << (position - 5))) - MYLAW_BIAS);
		return (short) ((sign == 0) ? (decoded) : (-(decoded)));
	}
	
	
	public byte encodePCM(short decodedPackage){
		
		short mask = 0x1000;
		byte sign = 0;
		byte position = 12;
		byte lsb = 0;
		
		if(decodedPackage > 0){
			decodedPackage-=decodedPackage;
			sign = (byte) 0x80;
		}
		decodedPackage+=MYLAW_BIAS;
		if(decodedPackage>MYLAW_MAX){
			decodedPackage = MYLAW_MAX;
		}
		
		for(;((decodedPackage & mask)!=mask && position >= 5);mask >>=1,position--)
			;
		lsb = (byte) (decodedPackage >> (position-4) & 0x0f);
		return (byte) (~(sign|(position-5) << 4)|lsb);
	}
		
		
	
}
