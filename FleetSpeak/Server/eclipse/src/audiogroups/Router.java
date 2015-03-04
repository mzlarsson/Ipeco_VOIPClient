package audiogroups;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import se.chalmers.fleetspeak.util.Log;

public class Router implements Runnable{
	private boolean running;
	private DatagramSocket inport;
	private DatagramSocket outport;
	private Map<Integer, DatagramPacket> clients;
	private DatagramPacket buffer;
	public Router(int inPort){
		try {
			inport = new DatagramSocket(inPort);
			outport = new DatagramSocket();
		} catch (SocketException e) {
			Log.log("Failed to create datagram sockets");
			e.printStackTrace();
		}
		clients = new HashMap<Integer, DatagramPacket>();
		byte[] b = new byte[172];
		buffer = new DatagramPacket(b, b.length);
	}
	@Override
	public void run() {
			running = true;
		while(running){
			try {
				inport.receive(buffer);
			} catch (IOException e) {
				Log.log("Error while reading packet");
				e.printStackTrace();
			}
			send(buffer.getData());
		}
		
	}
	
	private synchronized void send(byte[] data){
		
		for(DatagramPacket p: clients.values()){
			p.setData(data);
			try {
				outport.send(p);
			} catch (IOException e) {
				Log.log("failed sending packet");
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void addClient(int id, InetAddress addres, int port){
		byte[] buf = new byte[172];
		DatagramPacket p = new DatagramPacket(buf, buf.length, addres, port);
		clients.put(id, p);
	}
	public synchronized void removeClient(int id){
		clients.remove(id);
	}
	public synchronized void removeAllClients(){
		clients.clear();
	}
	
	public void shutdown(){
		running = false;
	}
}
