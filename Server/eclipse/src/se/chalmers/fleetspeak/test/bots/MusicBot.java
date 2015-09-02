package se.chalmers.fleetspeak.test.bots;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;

import javax.swing.Timer;

import se.chalmers.fleetspeak.network.udp.RTPHandler;
import se.chalmers.fleetspeak.test.sound.SongHandler;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.RtpPackageHandler;

public class MusicBot extends Thread {
	
	//State codes STUN
	private static final int STUN_STATUS_DONE = 1;
	private static final int STUN_STATUS_BROKEN = 2;
	private static final int STUN_STATUS_UNINITIATED = -1;
	
	//Static values
	private final static String musicRoomName = "Music Hangaround";
	private static final int sendFreq = 10;

	//Connection values
	private TCPBot tcpBot;
	private RtpPackageHandler handler;
	private int localPort = new Random().nextInt(2000)+2000;		// 2000 <= localPort < 4000
	private int stunStatus = STUN_STATUS_UNINITIATED;

	//Socket values
	private DatagramSocket socket = null;
	private DatagramPacket packet = null;
	private RTPHandler rtp;
	
	public MusicBot(String name, String ip, int port){
		this.tcpBot = new TCPBot(name, ip, port);
		tcpBot.start();
		
		handler = RtpPackageHandler.createHandler();
		handler.setTimeInterval(sendFreq);
	}
	
	@Override
	public void run(){
		//Wait for the server connections to stabilize.
		waitForTLS();
		
		//Do the STUN protocol
		performSTUN();
		
		//Start RTP functionality
		rtp = new RTPHandler(socket);
		
		//Fix all stuff
		if(stunStatus==STUN_STATUS_DONE || stunStatus>-1000){
			tcpBot.send(new Command("clientUdpTestOk", null, null));
			sleep(1000);
			System.out.println("Entering music room");
			moveToMusicRoom();
			sleep(1000);
			int playTimes = 5;
			for(int i = 0; i<playTimes; i++){
				System.out.println(i==0?"Playing audio":"Starting over"+(i+1==playTimes?" (last time)":""));		//Info message.
				playRandomAudioOverIP();
				sleep(1000);
			}
			System.out.println("Audio playing done. Disconnecting...");
			leaveAndCleanUp();
		}else{
			System.out.println("STUN failed. Aborting...");
			System.exit(1);
		}
	}
	
	private void waitForTLS(){
		while(tcpBot.getTLSStatus()==TCPBot.TLS_STATUS_UNINITIATED){
			sleep(50);
		}
		System.out.println("Done with TLS");
		
		if(tcpBot.getTLSStatus()==TCPBot.TLS_STATUS_BROKEN){
			System.out.println("Broken TLS");
			System.exit(1);
		}
	}
	
	private void performSTUN(){
		while(!tcpBot.hasControlCode()){
			sleep(50);
		}
		
		System.out.println("Received control code. Proceeding with STUN");
		
		//Load socket
		socket = null;
		packet = null;
		try {
			socket = new DatagramSocket(localPort);
			socket.connect(tcpBot.getServerIP(), tcpBot.getSoundPort());
			packet = new DatagramPacket(new byte[172], 172, tcpBot.getServerIP(), tcpBot.getSoundPort());
			
			final DatagramSocket currentSocket = socket;
			Thread udpListener = new Thread(new Runnable(){
				@Override
				public void run() {
					DatagramPacket p = new DatagramPacket(new byte[172], 172, tcpBot.getServerIP(), tcpBot.getSoundPort());
					try{
						Timer timeoutTimer = new Timer(5000, new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent ae) {
								//STUN timed out
								setStunStatus(STUN_STATUS_BROKEN);
							}
						});
						timeoutTimer.setRepeats(false);
						timeoutTimer.start();
						while(!currentSocket.isClosed() && stunStatus==STUN_STATUS_UNINITIATED){
							currentSocket.receive(p);
							if(p.getData().length>0 && p.getData()[0]==tcpBot.getControlCode()){
								setStunStatus(STUN_STATUS_DONE);
							}
						}
					}catch(IOException ioe){
						System.out.println("Error receiving UDP.");
					}
				}
			});
			udpListener.start();
			
			byte[] data = new byte[172];
			data[0] = (byte)tcpBot.getControlCode();
			packet.setData(data);
			while(stunStatus==STUN_STATUS_UNINITIATED){
				try {
					socket.send(packet);
				} catch (IOException e) {
					System.out.println("MusicBot could not connect");
					System.exit(1);
				}
			}
		} catch (SocketException e) {
			System.out.println("Could not create RTP socket: "+e.getMessage());
			System.exit(1);
		}
	}
		
	private void setStunStatus(int status){
		this.stunStatus = status;
	}
	
	private void moveToMusicRoom(){
		Integer roomID = tcpBot.getRoomID(musicRoomName);
		if(roomID == null){
			tcpBot.send(new Command("movenewroom", musicRoomName, null));
		}else{
			tcpBot.send(new Command("move", roomID, null));
		}
	}
	
	private void playRandomAudioOverIP(){
		InputStream audioStream = SongHandler.getSong((new Random().nextInt(SongHandler.getSongCount())));
		if(audioStream != null && packet != null && socket != null){
			//Play it
			try {
				byte[] tmp = new byte[320];
				int readBytes = 0;
				System.out.println("Sending audio data");
				while(audioStream.available()>0){
					readBytes = audioStream.read(tmp);
					if(readBytes < tmp.length){
						for(int i = readBytes; i<tmp.length; i++){
							tmp[i] = 0;
						}
					}
					
					rtp.sendPacket(tmp);
					sleep(sendFreq-1);
				}
				
				//All audio sent.
				audioStream.close();
			} catch (IOException e) {
				System.out.println("Error while reading sound file");
			}
		}
	}
	
	private void leaveAndCleanUp(){
		rtp.terminate();
		tcpBot.send(new Command("disconnect", null, null));
		tcpBot.close();
	}
	
	private void sleep(int ms){
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {}
	}
	
	public static void main(String[] args){
		new Thread(new MusicBot("bottenanja", "46.239.103.195", 8867)).start();
	}
}
