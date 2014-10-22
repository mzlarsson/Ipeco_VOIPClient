package se.chalmers.fleetspeak.tests;

import se.chalmers.fleetspeak.sound.RTPSoundMixer;

public class testDynamicRange {
	
	public static void main(String args[]){
	
		for(short s =(short) -160;s<=160;s++){
			System.out.println(s+" is compressed to: "+(RTPSoundMixer.dynamicRangeCompression(s, (short) 150)));
		}
		
	}
}
