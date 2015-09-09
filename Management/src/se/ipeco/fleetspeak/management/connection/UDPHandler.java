package se.ipeco.fleetspeak.management.connection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

import javax.swing.Timer;

public class UDPHandler extends Thread{

	//Stun status
	private StunStatus stunStatus = StunStatus.PENDING;
	private StunListener listener;
	
	//Data
	private int localPort = new Random().nextInt(2000)+2000;
	private InetAddress serverIP;
	private int serverPort;
	private byte controlCode;
	
	//Connection
	private DatagramSocket socket;
	private DatagramPacket packet;
	private volatile boolean running;

	protected UDPHandler(InetAddress serverIP, int serverPort, byte controlCode){
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		this.controlCode = controlCode;
	}
	
	public void setStunListener(StunListener listener){
		this.listener = listener;
	}
	
	public void run(){
		Thread.currentThread().setName("UDPHandler");
		//Perform stun
		doStun();

		//Ready to send audio
		running = true;
		while(running){
			//DO stuff
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}
	
	private void doStun(){
		socket = null;
		packet = null;
		try {
			System.out.println("Creating udp socket");
			socket = new DatagramSocket(localPort);
			socket.connect(serverIP, serverPort);
			packet = new DatagramPacket(new byte[172], 172, serverIP, serverPort);
			
			System.out.println("Connected. Initiated read (udp)");
			final DatagramSocket currentSocket = socket;
			Thread udpListener = new Thread(new Runnable(){
				@Override
				public void run() {
					Thread.currentThread().setName("UDP Stun receiver");
					DatagramPacket p = new DatagramPacket(new byte[172], 172, serverIP, serverPort);
					try{
						Timer timeoutTimer = new Timer(5000, new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent ae) {
								//STUN timed out
								if(stunStatus==StunStatus.PENDING){
									setStunStatus(StunStatus.FAILED);
								}
							}
						});
						timeoutTimer.setRepeats(false);
						timeoutTimer.start();
						while(!currentSocket.isClosed() && stunStatus==StunStatus.PENDING){
							currentSocket.receive(p);
							System.out.println("Got packet: "+p);
							if(p.getData().length>0 && p.getData()[0]==controlCode){
								setStunStatus(StunStatus.DONE);
							}
						}
					}catch(IOException ioe){
						System.out.println("Error receiving UDP.");
					}
				}
			});
			udpListener.start();
			
			byte[] data = new byte[172];
			data[0] = controlCode;
			packet.setData(data);
			while(stunStatus==StunStatus.PENDING){
				try {
					socket.send(packet);
				} catch (IOException e) {
					System.out.println("UDPHandler could not connect");
					System.exit(1);
				}
			}
		} catch (SocketException e) {
			System.out.println("Could not create UDP socket: "+e.getMessage());
			System.exit(1);
		}
	}
	
	public void setStunStatus(StunStatus status){
		this.stunStatus = status;
		if(listener != null){
			listener.stunDone(status);
		}
	}
	
	public void terminate(){
		running = false;
		socket.close();
	}
	
	public enum StunStatus{
		PENDING, DONE, FAILED
	}
	
	public interface StunListener{
		public void stunDone(StunStatus status);
	}
}
