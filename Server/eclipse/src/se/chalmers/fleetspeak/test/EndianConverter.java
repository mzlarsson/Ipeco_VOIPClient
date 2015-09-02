package se.chalmers.fleetspeak.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import se.chalmers.fleetspeak.test.sound.Constants;

public class EndianConverter {

	public EndianConverter(String filename){
		File f = new File(Constants.MUSIC_BASEDIR+filename+".bad");
		try {
			InputStream in = new FileInputStream(f);
			OutputStream out = new FileOutputStream(new File(Constants.MUSIC_BASEDIR+filename+"_bigendian.bad"));
			byte[] buf = new byte[320];
			byte tmp = 0;
			while(in.available()>0){
				//Read input
				in.read(buf);
				//Turn order
				for(int i = 0; i<buf.length; i+=2){
					tmp = buf[i];
					buf[i] = buf[i+1];
					buf[i+1] = tmp;
				}
				//Write output
				out.write(buf, 0, buf.length);
			}
			
			out.flush();
			out.close();
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		new EndianConverter("test1");
		new EndianConverter("test2");
		new EndianConverter("test3");
	}
	
}
