package se.chalmers.fleetspeak.test;

import se.chalmers.fleetspeak.sound.opus.OpusDecoderWrapper;
import se.chalmers.fleetspeak.sound.opus.OpusEncoderWrapper;
import se.chalmers.fleetspeak.sound.opus.OpusException;

public class OpusTester {

	public static void main(String[] args){
		OpusEncoderWrapper encoder = null;
		OpusDecoderWrapper decoder = null;

		try {
			encoder = new OpusEncoderWrapper();
			decoder = new OpusDecoderWrapper();
		} catch (OpusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
