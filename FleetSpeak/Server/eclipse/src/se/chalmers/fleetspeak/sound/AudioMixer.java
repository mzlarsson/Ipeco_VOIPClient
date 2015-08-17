package se.chalmers.fleetspeak.sound;

import java.util.Random;

public class AudioMixer {

	private static final int AUDIO_ARRAY_LENGTH = 160; 
	
	private byte[] mixAudio(byte[]... audioArr) {
		int buffer = 0;
		byte[] mixedAudio = new byte[AUDIO_ARRAY_LENGTH];
		for (int i=0; i<AUDIO_ARRAY_LENGTH; i++) {
			buffer = 0;
			for (int j=0; j<audioArr.length; j++) {
				buffer += audioArr[j][i];
			}
			if (buffer<-128) {
				mixedAudio[i] = -128;
			} else if (buffer>128) {
				mixedAudio[i] = 127;
			} else {
				mixedAudio[i] = (byte)buffer;
			}
		}
		return mixedAudio;
	}
	
	private byte randByte() {
		return (byte) new Random().nextInt();
	}
	
	public static void main(String[] args) {
		AudioMixer am = new AudioMixer();
		byte[] b1, b2, b3, b4, b5;
		b1 = new byte[AUDIO_ARRAY_LENGTH];
		b2 = new byte[AUDIO_ARRAY_LENGTH];
		b3 = new byte[AUDIO_ARRAY_LENGTH];
		b4 = new byte[AUDIO_ARRAY_LENGTH];
		for (int i=0; i<AUDIO_ARRAY_LENGTH; i++) {
			b1[i] = am.randByte();
			b2[i] = am.randByte();
			b3[i] = am.randByte();
			b4[i] = am.randByte();
		}
		b5 = am.mixAudio(b1, b2, b3, b4);
		int capped = 0;
		for(int i=0; i<AUDIO_ARRAY_LENGTH; i++) {
			System.out.print(b1[i] + "\t+\t" + b2[i] + "\t+\t" + b3[i] + "\t+\t" + b4[i] + "\t=\t" + b5[i] + "\t");
			if ((b1[i]+b2[i]+b3[i]+b4[i])==b5[i]) {
				System.out.println("OK");
			} else {
				capped++;
				System.out.println("NOOO!");
			}
		}
		System.out.println(capped + "/" + AUDIO_ARRAY_LENGTH + " was capped.");
	}
}
