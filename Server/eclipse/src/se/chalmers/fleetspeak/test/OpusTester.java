package se.chalmers.fleetspeak.test;

import java.util.logging.Logger;

import se.chalmers.fleetspeak.sound.opus.OpusDecoder;
import se.chalmers.fleetspeak.sound.opus.OpusEncoder;
import se.chalmers.fleetspeak.sound.opus.OpusException;

public class OpusTester {

	public static void main(String[] args){
		OpusEncoder encoder = null;
		OpusDecoder decoder = null;
		
		try {
			encoder = new OpusEncoder();
			decoder = new OpusDecoder();
		} catch (OpusException e) {
			Logger.getLogger("Debug").severe("Could not start opus. Dammit!");
		}
		
		byte[] bytedata = new byte[160];
		for(int i = 0; i<bytedata.length; i++){
			bytedata[i] = (byte)120;
		}
		
		byte[] middleData = encoder.encode(bytedata);
		byte[] newData = decoder.decode(middleData);
		
		int errorCounter = 0;
		for(int i = 0; i<bytedata.length; i++){
			System.out.println(bytedata[i]+"\t-->\t"+middleData[i]+"\t-->\t"+newData[i]+"\t=\t"+(bytedata[i]==newData[i]?"SAME":"DIFF"));
			if(bytedata[i] != newData[i]){
				errorCounter++;
			}
		}
		System.out.println("\nDone. Found "+errorCounter+" errors of 160 bytes.");
	}
	
}
