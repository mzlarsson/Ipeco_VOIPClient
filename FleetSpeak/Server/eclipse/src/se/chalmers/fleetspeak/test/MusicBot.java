package se.chalmers.fleetspeak.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.RtpPackageHandler;

public class MusicBot extends Thread {
	
	private final static String basedir = "data/";
	private final static String musicRoomName = "Music Hangaround";
	private static final int sendFreq = 20;
	private static List<File> songs;

	private TCPBot tcpBot;
	private RtpPackageHandler handler;
	private int localPort = new Random().nextInt(2000)+2000;		// 2000 <= localPort < 4000
	
	public MusicBot(String name, String ip, int port){
		this.tcpBot = new TCPBot(name, ip, port);
		tcpBot.start();
		
		handler = RtpPackageHandler.createHandler();
		handler.setTimeInterval(sendFreq);
	}
	
	@Override
	public void run(){
		//Load possible songs
		if(songs == null){
			loadSongs();
		}
		
		//Check for loading errors
		if(songs == null || songs.size() == 0){
			System.out.println("Could not find any songs to play. Please add some in the folder '"+basedir+"'");
		}
		
		//Wait for the server connections to stabilize.
		sleep(1000);
		
		//Enter or create music room
		Integer roomID = tcpBot.getRoomID(musicRoomName);
		if(roomID == null){
			tcpBot.send(new Command("moveNewRoom", musicRoomName, null));
		}else{
			tcpBot.send(new Command("move", roomID, null));
		}
		
		//Wait for commands to be executed
		sleep(1000);
		
		//Load socket
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		try {
			socket = new DatagramSocket(localPort);
			packet = new DatagramPacket(new byte[172], 172, tcpBot.getServerIP(), tcpBot.getSoundPort());
		} catch (SocketException e) {
			System.out.println("Could not create RTP socket: "+e.getMessage());
		}
		
		//Load song
		InputStream input = getInputStream(songs.get(new Random().nextInt(songs.size())));
		if(input != null && packet != null && socket != null){
			//Play it
			try {
				byte[] tmp = new byte[160];
				int readBytes = 0;
				System.out.println("Sending audio data");
				while(input.available()>0){
					readBytes = input.read(tmp);
					if(readBytes < tmp.length){
						for(int i = readBytes; i<tmp.length; i++){
							tmp[i] = 0;
						}
					}
					
					packet.setData(handler.toRtpPackage(tmp));
					socket.send(packet);
					
					sleep(sendFreq-1);
				}
				System.out.println("All audio data sent. Closing...");
				input.close();
				socket.close();
			} catch (IOException e) {
				System.out.println("Error while reading sound file");
			}
		}
	}
	
	private static void loadSongs(){
		File source = new File(basedir);
		if(source.exists() && source.isDirectory()){
			songs = new ArrayList<File>();
			for(File f : source.listFiles()){
				if(f.isFile() && f.canRead() && f.getName().endsWith(".wav")){
					songs.add(f);
				}
			}
		}
		
		for(File file : songs){
			System.out.println(file.getName());
		}
	}
	
	private InputStream getInputStream(File file){
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
	
	private void sleep(int ms){
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {}
	}
}
