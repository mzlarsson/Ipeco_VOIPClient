package se.chalmers.fleetspeak.test.bots;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import se.chalmers.fleetspeak.util.Command;

class TCPBot extends Thread{

	protected static final int TLS_STATUS_DONE = 1;
	protected static final int TLS_STATUS_BROKEN = 2;
	protected static final int TLS_STATUS_UNINITIATED = -1;
	
	private String name;
	
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket;
	private boolean isRunning;
	
	private Map<String, Integer> rooms;
	private int soundPort;
	private int controlCode;
	private boolean hasControlCode;
	private int tlsStatus = TLS_STATUS_UNINITIATED;
	
	public TCPBot(String name, String serverIP, int serverPort){
		this.name = name;
		this.rooms = new HashMap<String, Integer>();
		this.soundPort = -1;
		try {
			socket = getTLSSocket(serverIP, serverPort);
			if(socket != null){
				in = new ObjectInputStream(socket.getInputStream());
				out = new ObjectOutputStream(socket.getOutputStream());
			}else{
				System.out.println("No connecting. Closing.");
				System.exit(0);
			}
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
			case "initiatesoundport":	System.out.println("setting status");
										tlsStatus = TLS_STATUS_DONE;
										this.soundPort = (Integer)c.getKey();
										this.controlCode = (byte)c.getValue();
										this.hasControlCode = true;break;
			case "sendauthenticationdetails":	send(new Command("authenticationDetails", "bottenanja", null));break;
			case "authenticationresult":	if(c.getKey().getClass()==boolean.class && (boolean)(c.getKey())){
												tlsStatus = TLS_STATUS_DONE;
											}else{
												tlsStatus = TLS_STATUS_BROKEN;
											}break;
		}
	}
	
	protected int getTLSStatus(){
		return tlsStatus;
	}
	
	protected InetAddress getServerIP(){
		return socket.getInetAddress();
	}
	
	protected int getSoundPort(){
		return soundPort;
	}
	
	protected int getControlCode(){
		return controlCode;
	}
	
	protected boolean hasControlCode(){
		return hasControlCode;
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
	
	private Socket getTLSSocket(String host, int port){
		try{
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream("certificate/truststore"), "fleetspeak".toCharArray());
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);
			SSLContext ssl = SSLContext.getInstance("TLSv1");
			ssl.init(null, tmf.getTrustManagers(), null);
			
			return ssl.getSocketFactory().createSocket(host, port);
		}catch(Exception e){
			System.out.println("Got fucking exception while creating TLS socket. ");
			e.printStackTrace();
			return null;
		}
	}
}
