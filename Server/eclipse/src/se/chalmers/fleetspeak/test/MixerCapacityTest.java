package se.chalmers.fleetspeak.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.Scanner;

import se.chalmers.fleetspeak.sound.AbstractMixer;
import se.chalmers.fleetspeak.sound.BufferedAudioStream;
import se.chalmers.fleetspeak.sound.MixerFactory;

public class MixerCapacityTest {

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		byte[][][] data = readData();
		
		for(int a = 3; a<10; a++){
			System.out.println(a+" people.");
			
			String[] mixerTypes = {"short", "super", "enhanced"};
			for(int c = 0; c<mixerTypes.length; c++){
				AbstractMixer m = (AbstractMixer)MixerFactory.getMixerByName(mixerTypes[c]);
				for(int b = 0; b<a; b++){
					TestBufferStream s = new TestBufferStream(data[b]);
					m.addStream(s, null);
				}
				
				System.out.print("\t"+mixerTypes[c]+": ");
				
				//Print stuff
				long startTime = System.currentTimeMillis();
				Method method = m.getClass().getDeclaredMethod("getMixed");
				method.setAccessible(true);
				for(int i = 0; i<1000000; i++){
					method.invoke(m);
				}
				System.out.println((System.currentTimeMillis()-startTime)+" ms for 1m mixes");			//\u00B5
				
				m.close();
			}
		}
	}
	
	public static boolean printData() throws IOException{
		Random r = new Random();
		PrintWriter writer = new PrintWriter(new File("mixerdata.txt"));
		byte[][][] printer = new byte[10][600][320];
		for(int b = 0; b<printer.length; b++){
			for(int i = 0; i<printer[b].length; i++){
				r.nextBytes(printer[b][i]);
				StringBuffer buffer = new StringBuffer(printer[b][i][0]);
				for(int a = 1; a<printer[b][i].length; a++){
					buffer.append(" ").append(printer[b][i][a]);
				}
				
				writer.println(buffer.toString());
				writer.flush();
			}
		}
		writer.close();
		
		return true;
	}
	
	public static byte[][][] readData(){
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
	
	
	public static class TestBufferStream implements BufferedAudioStream{
		
		private byte[][] bytes;
		private int index = 0;
		
		public TestBufferStream(byte[][] bytes) {
			this.bytes = bytes;
		}

		@Override
		public byte[] read() {
			byte[] data = bytes[index];
			
			index = (index+1)%bytes.length;
			return data;
		}
		
	}
}
