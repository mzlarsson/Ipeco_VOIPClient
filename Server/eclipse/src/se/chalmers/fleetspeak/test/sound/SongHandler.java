package se.chalmers.fleetspeak.test.sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SongHandler {

	private static List<File> songs;

	public static void init(){
		//Load possible songs
		if(songs == null){
			loadSongs();
		}
		
		//Check for loading errors
		if(songs == null || songs.size() == 0){
			System.out.println("Could not find any songs to play. Please add some in the folder '"+Constants.MUSIC_BASEDIR+"'");
		}
	}
	

	public static void playAudioLocally(int songIndex){
		if(songs == null){
			init();
		}
		
		if(songIndex>=songs.size()){
			System.out.println("Invalid song index.");
			return;
		}
		
		InputStream audioStream = getByteInputStream(songs.get(songIndex));
		playAudioLocally(audioStream);
	}
		
	protected static void playAudioLocally(InputStream audioStream){
		SourceDataLine output = getOutputLine();
		if(audioStream != null){
			//Play it
			try {
				byte[] tmp = new byte[320];
				int readBytes = 0;
				while(audioStream.available()>0){
					readBytes = audioStream.read(tmp);
					if(readBytes < tmp.length){
						for(int i = readBytes; i<tmp.length; i++){
							tmp[i] = 0;
						}
					}
					
					output.write(tmp, 0, readBytes);
				}
				
				//All audio sent.
				output.drain();
				output.close();
				audioStream.close();
			} catch (IOException e) {
				System.out.println("Error while reading sound file/writing to speaker: "+e.getMessage());
			}
		}
	}
	
	public static InputStream getSong(int index){
		if(songs == null){
			init();
		}

		if(songs != null && index<songs.size()){
			File source = songs.get(index);
			if(source.getName().toLowerCase().endsWith(".wav")){
				return getAudioInputStream(source);
			}else if(source.getName().toLowerCase().endsWith(".bad")){
				return getByteInputStream(songs.get(index));
			}else{
				System.out.println("Illegal format of file ("+source.getName().substring(source.getName().lastIndexOf("."))+"). Only .bad and .wav supported.");
			}
		}
		
		return null;
	}
	
	public static int getSongCount(){
		if(songs == null){
			init();
		}
		
		return songs==null?0:songs.size();
	}
	
	private static void loadSongs(){
		File source = new File(Constants.MUSIC_BASEDIR);
		if(source.exists() && source.isDirectory()){
			songs = new ArrayList<File>();
			for(File f : source.listFiles()){
				if(f.isFile() && f.canRead() && (f.getName().endsWith(".wav") || f.getName().endsWith(".bad"))){
					songs.add(f);
				}
			}
		}
		
		System.out.println("SongHandler loaded following songs:");
		for(File file : songs){
			System.out.println("\t"+file.getName());
		}
	}

	private static InputStream getAudioInputStream(File file){
		try {
			AudioInputStream in = AudioSystem.getAudioInputStream(file);
			AudioInputStream din = null;
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
			                                            baseFormat.getSampleRate(),
			                                            16,
			                                            baseFormat.getChannels(),
			                                            baseFormat.getChannels() * 2,
			                                            baseFormat.getSampleRate(),
			                                            false);
			din = AudioSystem.getAudioInputStream(decodedFormat, in);
			return din;
		} catch (UnsupportedAudioFileException e) {
			System.out.println("Does not support the audio file '"+file.getName()+"'.");
		} catch (IOException e) {
			System.out.println("IOException while reading file '"+file.getName()+"': "+e.getMessage());
		}
		
		return null;
	}
	
	private static InputStream getByteInputStream(File file){
		try {
			InputStream inputStream = new FileInputStream(file);
			return inputStream;
		} catch (FileNotFoundException e) {
			System.out.println("File not found: "+file);
			return null;
		}
	}

	private static SourceDataLine getOutputLine(){
		SourceDataLine sourceLine = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, Constants.DEFAULT_AUDIO_FORMAT);
		try {
			sourceLine = (SourceDataLine) AudioSystem.getLine(info);
			sourceLine.open(Constants.DEFAULT_AUDIO_FORMAT);
			sourceLine.start();
		} catch (LineUnavailableException e) {
			System.out.println("Unable to start output: "+e.getMessage()+ "["+e.getClass()+"]");
			return null;
		} catch (Exception e) {
			System.out.println("Unable to start output: "+e.getMessage()+ "["+e.getClass()+"]");
			return null;
		}
		
		return sourceLine;
	}
}
