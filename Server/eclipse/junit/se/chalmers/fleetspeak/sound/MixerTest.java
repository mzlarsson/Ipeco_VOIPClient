package se.chalmers.fleetspeak.sound;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

import org.junit.Test;

public class MixerTest {
	
	@Test
	public void test() throws NoSuchMethodException, SecurityException {
		//Fetch mixers
		Mixer mixer = MixerFactory.getMixerByName("short");
		Mixer newMixer = MixerFactory.getMixerByName("optimized");
		
		//Fetch data
		byte[][][] data = readData();
		
		//Add streams
		int people = 4;
		for(int i = 0; i<people; i++){
			mixer.addStream(createStream(data[i]), null);
			newMixer.addStream(createStream(data[i]), null);
		}

		Method mixMethod = mixer.getClass().getDeclaredMethod("getMixed");
		Method newMixMethod = newMixer.getClass().getDeclaredMethod("getMixed");
		
		System.out.println("Comparing mixes for "+data[0].length+" arrays of length "+data[0][0].length);
		for(int a = 0; a<data[0].length; a++){
			byte[][] mixed = null, newMixed = null;
			try {
				mixed = (byte[][])mixMethod.invoke(mixer);
				newMixed = (byte[][])newMixMethod.invoke(newMixer);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for(int i = 0; i<mixed.length; i++){
				for(int j = 0; j<mixed[i].length; j++){
					assertTrue(mixed[i][j]==newMixed[i][j]);
				}
			}
		}
	}
	
	private static byte[][][] readData(){
		Scanner sc = null;
		try {
			sc = new Scanner(new File("mixerdata.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("Could not find data source");
		}
		
		byte[][][] data = new byte[10][600][320];
		for(int i = 0; i<data.length; i++){
			for(int j = 0; j<data[i].length; j++){
				if(i<2){
					for(int k = 0; k<data[i][j].length; k++){
						data[i][j][k] = sc.nextByte();
					}
				}else{
					data[i][j] = null;
				}
			}
		}
		sc.close();
		
		return data;
	}
	
	private BufferedAudioStream createStream(byte[]... bytes){
		if(bytes.length==1){
			return new SingleByteBufferedAudioStream(bytes[0]);
		}else{
			return new MultiByteBufferedAudioStream(bytes);
		}
	}
	
	private class SingleByteBufferedAudioStream implements BufferedAudioStream{
		private byte[] b;
		public SingleByteBufferedAudioStream(byte[] b){
			this.b = b;
		}
		
		@Override
		public byte[] read(){
			return b;
		}
	}
	
	private class MultiByteBufferedAudioStream implements BufferedAudioStream{
		private byte[][] b;
		private int counter = 0;
		public MultiByteBufferedAudioStream(byte[][] b){
			this.b = b;
		}
		
		@Override
		public byte[] read(){
			byte[] res = b[counter];
			counter = (counter+1)%b.length;
			return res;
		}
	}
}
