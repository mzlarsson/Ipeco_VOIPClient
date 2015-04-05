package se.chalmers.fleetspeak.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class DatagramSender implements Runnable {

	DatagramSocket socket;
	DatagramPacket packet;
	int counter = 0;
	/**
	 *
	 * @param localport the port for the socket to use
	 * @param remoteport port to send packets to
	 */
	public DatagramSender(int localport, int remoteport){
		try {
			socket = new DatagramSocket(localport);
			packet = new DatagramPacket(new byte[2], 2, InetAddress.getByName("localhost"), remoteport);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	@Override
	public void run() {
		for(; counter < 1000; counter++){
			try {
				socket.send(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
