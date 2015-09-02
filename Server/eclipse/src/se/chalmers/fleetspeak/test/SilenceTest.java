package se.chalmers.fleetspeak.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;


public class SilenceTest {

	public static void main(String[] args){
		try {
			InputStream in = new FileInputStream(new File("tmpSilence.txt"));
			Scanner sc = new Scanner(in);
			while(sc.hasNextByte()){
				System.out.print("[");
				for(int i = 0; i<320; i+=2){
					byte low = sc.nextByte();
					byte high = sc.nextByte();
					System.out.print(bytesToShort(high, low)+", ");
				}
				System.out.println("]");
			}
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private static short bytesToShort(byte big, byte small){
		return (short) ((big << 8) | (small & 0xff));
	}
	
}
