package se.chalmers.fleetspeak.newgui.connection;

import org.json.JSONException;
import org.json.JSONObject;

import se.chalmers.fleetspeak.newgui.connection.UDPHandler.StunListener;
import se.chalmers.fleetspeak.newgui.connection.UDPHandler.StunStatus;

public class Authenticator implements CommandHandler, StunListener{
	
	//Data
	private TCPHandler tcp;
	private UDPHandler udp;
	private String username;
	private String password;
	
	private String alias;
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
	
	public String getAlias(){
		return alias;
	}
	
	public int getUserID(){
		return userID;
	}

	@Override
	public void commandReceived(String json) {
		System.out.println("Got command "+json);
		try{
			JSONObject obj = new JSONObject(json);
			switch(obj.getString("command").toLowerCase()){
				case "sendauthenticationdetails":	JSONObject sendObj = new JSONObject();
													sendObj.put("command", "authenticationDetails");
													sendObj.put("username", username);
													sendObj.put("password", password);
													tcp.send(sendObj.toString());break;
				case "setinfo":						this.username = obj.getString("alias");
													this.userID = obj.getInt("id");break;
				case "initiatesoundport":			udp = new UDPHandler(tcp.getIP(), obj.getInt("port"), Byte.parseByte(obj.getString("controlcode")));
													udp.setStunListener(this);
													udp.start();
													break;
				case "authenticationresult":		setResult(!obj.getBoolean("authenticationResult"));break;
			}
		}catch(JSONException e){
			System.out.println("Got invalid command: [JSONException] "+e.getMessage());
			System.out.println("\tFrom JSON: "+json);
		}
	}

	private void setResult(boolean failed){
		if(listener != null){
			if(failed){
				listener.authenticationFailed("Authentification failed. Check username/password combination");
			}else{
				listener.authenticationDone();
			}
		}
	}
	
	@Override
	public void stunDone(StunStatus status) {
		if(status == StunStatus.DONE){
			try {
				tcp.send(new JSONObject().put("command", "clientUdpTestOk").toString());
			} catch (JSONException e) {
				System.out.println("Could not send clientUdpTestOk: [JSONException] "+e.getMessage());
			}
		}else{
			if(listener != null){
				listener.authenticationFailed("STUN failed");
			}
		}
	}

	public interface AuthenticatorListener{
		public void authenticationDone();
		public void authenticationFailed(String msg);
	}
}