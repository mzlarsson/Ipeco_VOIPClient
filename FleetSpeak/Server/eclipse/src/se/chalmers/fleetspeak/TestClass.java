package se.chalmers.fleetspeak;

import se.chalmers.fleetspeak.sound.PCMUtil;

public class TestClass {

	public static void main(String[] args){
		for(int i = -128; i<128; i++){
			byte b = (byte)i;
			short decoded = PCMUtil.decodePCM(b);
			byte encoded = PCMUtil.encodePCM(decoded);
			System.out.println(b+" --> "+decoded+" --> "+encoded+" --> "+(b==encoded));
		}
	}
	
}
