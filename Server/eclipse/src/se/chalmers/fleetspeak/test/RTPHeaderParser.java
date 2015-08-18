package se.chalmers.fleetspeak.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class RTPHeaderParser {

	public static void main(String[] args){
		try {
			Scanner sc = new Scanner(new File("rtpHeaders.txt"));
			long prevNum = -1;
			while(sc.hasNextInt()){
				sc.nextInt();
				sc.nextInt();
				sc.nextInt();
				sc.nextInt();
				
				long num = 0, tmp = 0;
				for(int i = 0; i<4; i++){
					tmp = sc.nextInt();
					num = num*256 + (tmp<0?256+tmp:tmp);
				}
				
				System.out.println("["+num+"] "+(prevNum>=0?num-prevNum:""));
				prevNum = num;
				
				sc.nextInt();
				sc.nextInt();
				sc.nextInt();
				sc.nextInt();
			}
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error while reading file");
		}
	}
	
}
