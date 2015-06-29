package se.chalmers.fleetspeak.test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import se.chalmers.fleetspeak.util.Command;

class TCPBot extends Thread{

	private String name;
	
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket;
	private boolean isRunning;
	
	private Map<String, Integer> rooms;
	private Map<Integer, Integer> inPorts;
	private int soundPort;
	
	public TCPBot(String name, String serverIP, int serverPort){
		this.name = name;
		this.rooms = new HashMap<String, Integer>();
		this.inPorts = new HashMap<Integer, Integer>();
		this.soundPort = -1;
		try {
			socket = new Socket(serverIP, serverPort);
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			System.out.println("Invalid IP: "+serverIP);
		} catch (IOException e) {
			System.out.println("Unknown IO Error: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public void run(){
		isRunning = true;
		send(new Command("setName", this.name, null));
		try {
			while (isRunning && in != null) {
				Object o = in.readObject();
				Command c = (Command)o;
				processCommand(c);
			}
		}catch(IOException ioe){
			System.out.println("IO error: "+ioe.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found: "+e.getMessage());
		}
	}
	
	private void processCommand(Command c){
		System.out.println("Test got:\n\tCommand:["+c.getCommand()+", key:"+(c.getKey()==null?"null":c.getKey())+", value:"+(c.getValue()==null?"null":c.getValue())+"]");
		
		switch(c.getCommand().toLowerCase()){
			case "setid":			System.out.println(name+":\n\tClient setting ID to "+c.getKey());break;
			case "addeduser":		System.out.println(name+":\n\tUPDATE: Added user");break;
			case "changedusername":	System.out.println(name+":\n\tUPDATE: Changed username");break;
			case "changedroomname":	System.out.println(name+":\n\tUPDATE: Changed room name");break;
			case "moveduser":		System.out.println(name+":\n\tUPDATE: Moved user");break;
			case "createdroom":		rooms.put((String)c.getValue(), (Integer)c.getKey());
									System.out.println(name+":\n\tUPDATE: Created room");break;
			case "removeduser":		System.out.println(name+":\n\tUPDATE: Removed user");break;
			case "removedroom":		String roomName = null;
									int id = (Integer)c.getKey();
									for(String key : rooms.keySet()){
										if(rooms.get(key) == id){
											roomName = key;break;
										}
									}
									rooms.remove(roomName);
									System.out.println(name+":\n\tUPDATE: Removed room");break;
			case "requestsoundport":int port = ((int)(Math.random()*5000))+1024;
									inPorts.put((Integer)c.getKey(), port);
									System.out.println(name+":\n\tOpening port "+port);
									send(new Command("setSoundPort", c.getKey(), port));
									break;
			case "usesoundport":	System.out.println(name+":\n\tClient now using port "+c.getKey());
									this.soundPort = (Integer)c.getKey();break;
			case "closesoundport":	System.out.println(name+":\n\tClosed sound port for clientID="+c.getKey());
									inPorts.remove((Integer)c.getKey());break;
		}
	}
	
	protected Map<Integer, Integer> getInPorts(){
		return inPorts;
	}
	
	protected InetAddress getServerIP(){
		return socket.getInetAddress();
	}
	
	protected int getSoundPort(){
		return soundPort;
	}
	
	protected Integer getRoomID(String name){
		return rooms.get(name);
	}
	
	protected void send(Command com){
		System.out.println("Sending:\n\tCommand:["+com.getCommand()+", key:"+com.getKey()+", value:"+com.getValue()+"]");
		try {
			out.writeObject(com);
		} catch (IOException e) {
			System.out.println("Could not send data...");
		}
	}
	
	public void close(){
		isRunning = false;
		try {
			if(in != null){
				in.close();
			}
			if(out != null){
				out.close();
			}
			if(socket != null){
				socket.close();
			}
		} catch (IOException e) {}
	}
	
}
