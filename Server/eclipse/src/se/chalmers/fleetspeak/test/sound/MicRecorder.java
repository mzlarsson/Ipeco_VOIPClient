package se.chalmers.fleetspeak.test.sound;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class MicRecorder extends Thread{

	private boolean running = true;
	private String saveFile;

	public MicRecorder(String saveFile){
		this.saveFile = saveFile;
	}

	@Override
	public void run(){
		TargetDataLine line = getRecorder();
		ByteArrayOutputStream out = null;
		OutputStream output = null;
		try {
			out  = new ByteArrayOutputStream(1600);
			output = new FileOutputStream(Constants.MUSIC_BASEDIR+saveFile+".bad");
		} catch (IOException e) {
			System.out.println("IO Exception of output setup.");
			e.printStackTrace();
			System.exit(1);
		}

		int numBytesRead;
		byte[] data = new byte[160];

		// Begin audio capture.
		line.start();

		try{
			while (running) {
				// Read the next chunk of data from the TargetDataLine.
				numBytesRead =  line.read(data, 0, data.length);
				// Save this chunk of data.
//				out.write(data, 0, numBytesRead);
				output.write(data, 0, numBytesRead);
			}
			
			out.flush();
			output.flush();
			out.close();
			output.close();
		}catch(IOException ioe){
			System.out.println("Got an error: "+ioe.getMessage());
			ioe.printStackTrace();
		}
	}

	private TargetDataLine getRecorder(){
		TargetDataLine line = null;
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, Constants.DEFAULT_AUDIO_FORMAT);
		if (!AudioSystem.isLineSupported(info)) {
			System.out.println("Invalid audio format.");
			System.exit(1);
		}

		// Obtain and open the line.
		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(Constants.DEFAULT_AUDIO_FORMAT);
		} catch (LineUnavailableException ex) {
			System.out.println("Line unavailable: "+ex.getMessage());
			System.exit(1);
		}

		return line;
	}

	public void terminate(){
		running = false;
	}

	public static void main(String[] args){
		MicRecorder m = new MicRecorder("test1.1");
		m.start();

		Scanner sc = new Scanner(System.in);
		while(!(sc.next().equals("terminate")));
		sc.close();
		m.terminate();
	}

}
