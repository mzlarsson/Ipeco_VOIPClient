package se.chalmers.fleetspeak.test.bots;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.json.JSONException;
import org.json.JSONObject;

class TCPBot extends Thread{

	protected static final int TLS_STATUS_DONE = 1;
	protected static final int TLS_STATUS_BROKEN = 2;
	protected static final int TLS_STATUS_UNINITIATED = -1;
	
	private String name;
	private int id;
	
	private BufferedReader in;
	private PrintWriter out;
	private Socket socket;
	private boolean isRunning;
	
	private Map<Integer, Integer> clientPosition;
	private Map<String, Integer> rooms;
	private int soundPort;
	private int controlCode;
	private boolean hasControlCode;
	private int tlsStatus = TLS_STATUS_UNINITIATED;
	
	public TCPBot(String name, String serverIP, int serverPort){
		this.name = name;
		this.clientPosition = new HashMap<Integer, Integer>();
		this.rooms = new HashMap<String, Integer>();
		this.soundPort = -1;
		try {
			socket = getTLSSocket(serverIP, serverPort);
			if(socket != null){
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream());
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
			String input = null;
			while (isRunning && in != null) {
				try {
					input = in.readLine();
					processCommand(input);
				} catch (JSONException e) {
					System.out.println("Got JSONException: "+e.getMessage());
					System.out.println("\tJSON: "+input);
				}
			}
		}catch(IOException ioe){
			System.out.println("IO error: "+ioe.getMessage());
		}
	}
	
	private void processCommand(String json) throws JSONException{
		System.out.println("Test got:\n\tCommand:"+json);
		if(json == null){
			System.out.println("Got null command. Ignored it.");
			return;
		}
		JSONObject obj = new JSONObject(json);
		
		switch(obj.getString("command").toLowerCase()){
			case "setinfo":			System.out.println(name+":\n\tClient info: [ID:"+obj.getInt("userid")+"]");
									this.id = obj.getInt("userid");break;
			case "addeduser":		clientPosition.put(obj.getInt("userid"), obj.getInt("roomid"));
									System.out.println(name+":\n\tUPDATE: Added user");break;
			case "changedusername":	System.out.println(name+":\n\tUPDATE: Changed username");break;
			case "changedroomname":	System.out.println(name+":\n\tUPDATE: Changed room name");break;
			case "moveduser":		clientPosition.put(obj.getInt("userid"), obj.getInt("destinationroom"));
									System.out.println(name+":\n\tUPDATE: Moved user");break;
			case "createdroom":		rooms.put(obj.getString("roomname"), obj.getInt("roomid"));
									System.out.println(name+":\n\tUPDATE: Created room");break;
			case "removeduser":		clientPosition.remove(obj.getInt("userid"));
									System.out.println(name+":\n\tUPDATE: Removed user");break;
			case "removedroom":		String roomName = null;
									int id = obj.getInt("roomid");
									for(String key : rooms.keySet()){
										if(rooms.get(key) == id){
											roomName = key;break;
										}
									}
									rooms.remove(roomName);
									System.out.println(name+":\n\tUPDATE: Removed room");break;
			case "initiatesoundport":	System.out.println("setting status");
										tlsStatus = TLS_STATUS_DONE;
										this.soundPort = obj.getInt("port");
										this.controlCode = obj.getInt("controlcode");
										this.hasControlCode = true;break;
			case "sendauthenticationdetails":	JSONObject authObj = new JSONObject();
												authObj.put("command", "authenticationdetails");
												authObj.put("username", "bottenanja");
												authObj.put("password", "");
												authObj.put("clienttype", "android");
												send(authObj.toString());
											break;
			case "authenticationresult":	if(obj.getBoolean("result")){
												tlsStatus = TLS_STATUS_DONE;
											}else{
												System.out.println("Did not authenticate: "+obj.getString("rejection"));
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
	
	protected int getUserID(){
		return id;
	}
	
	protected int getRoom(){
		return clientPosition.get(getUserID());
	}
	
	protected boolean hasControlCode(){
		return hasControlCode;
	}
	
	protected Integer getRoomID(String name){
		return rooms.get(name);
	}
	
	protected void send(String json){
		System.out.println("Sending:\n\tCommand:"+json);
		out.println(json);
		out.flush();
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
			System.out.println("Got an exception while creating TLS socket. ");
			e.printStackTrace();
			return null;
		}
	}
}
