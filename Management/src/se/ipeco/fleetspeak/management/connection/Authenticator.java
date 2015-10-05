package se.ipeco.fleetspeak.management.connection;

import org.json.JSONException;
import org.json.JSONObject;

import se.ipeco.fleetspeak.management.connection.UDPHandler.StunListener;
import se.ipeco.fleetspeak.management.connection.UDPHandler.StunStatus;

public class Authenticator implements CommandHandler, StunListener{
	
	//Data
	private TCPHandler tcp;
	private UDPHandler udp;
	private String username;
	private String password;
	
	private int userID;
	
	//Status
	private AuthenticatorListener listener;
	
	protected Authenticator(TCPHandler tcp, String username, String password) {
		this.tcp = tcp;
		this.tcp.setCommandHandler(this);
		this.username = username;
		this.password = password;
	}
	
	public void setListener(AuthenticatorListener listener){
		this.listener = listener;
	}
	
	public UDPHandler getUDPHandler(){
		return udp;
	}
	
	public int getUserID(){
		return userID;
	}

	@Override
	public void commandReceived(String json) {
		System.out.println("Got command "+json);
		if(json == null){
			System.out.println("Ignoring empty command.");
			return;
		}
		
		try{
			JSONObject obj = new JSONObject(json);
			switch(obj.getString("command").toLowerCase()){
				case "sendauthenticationdetails":	JSONObject sendObj = new JSONObject();
													sendObj.put("command", "authenticationdetails");
													sendObj.put("username", username);
													sendObj.put("password", password);
													sendObj.put("clienttype", "android");
													tcp.send(sendObj.toString());break;
				case "setinfo":						this.userID = obj.getInt("userid");break;
				case "initiatesoundport":			udp = new UDPHandler(tcp.getIP(), obj.getInt("port"), Byte.parseByte(obj.getString("controlcode")));
													udp.setStunListener(this);
													udp.start();
													break;
				case "authenticationresult":		setResult(!obj.getBoolean("result"), (obj.has("rejection")?obj.getString("rejection"):null));break;
			}
		}catch(JSONException e){
			System.out.println("Got invalid command: [JSONException] "+e.getMessage());
			System.out.println("\tFrom JSON: "+json);
		}
	}

	private void setResult(boolean failed, String msg){
		if(listener != null){
			if(failed){
				listener.authenticationFailed(msg);
			}else{
				listener.authenticationDone();
			}
		}
	}
	
	@Override
	public void stunDone(StunStatus status) {
		if(status == StunStatus.DONE){
			try {
				tcp.send(new JSONObject().put("command", "clientudptestok").toString());
			} catch (JSONException e) {
				System.out.println("Could not send clientUdpTestOk: [JSONException] "+e.getMessage());
			}
		}else{
			if(listener != null){
				listener.authenticationFailed("STUN failed");
			}
		}
	}
	
	public void terminate(){
		if(udp != null){
			udp.terminate();
		}
		if(tcp != null){
			tcp.terminate();
		}
	}

	public interface AuthenticatorListener{
		public void authenticationDone();
		public void authenticationFailed(String msg);
	}
}
