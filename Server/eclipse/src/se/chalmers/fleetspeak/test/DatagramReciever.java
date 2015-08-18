package se.chalmers.fleetspeak.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class DatagramReciever implements Runnable{

	int received;
	DatagramSocket socket;


	public DatagramReciever(int port){
		this.received = 0;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		DatagramPacket recv = new DatagramPacket(new byte[1], 1);
		while(true){
			try {
				socket.receive(recv);
				received++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


	}

}
