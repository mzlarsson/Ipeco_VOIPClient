package se.chalmers.fleetspeak.newgui.core;

import se.chalmers.fleetspeak.newgui.core.UDPHandler.StunListener;
import se.chalmers.fleetspeak.newgui.core.UDPHandler.StunStatus;
import se.chalmers.fleetspeak.util.Command;

public class Authenticator implements CommandHandler, StunListener{
	
	//Data
	private TCPHandler tcp;
	private UDPHandler udp;
	private String username;
	private String password;
	
	//Status
	private AuthenticatorListener listener;
	private boolean done;
	private boolean failed;
	
	protected Authenticator(TCPHandler tcp, String username, String password) {
		this.tcp = tcp;
		this.tcp.setCommandHandler(this);
		this.username = username;
		this.password = password;
	}
	
	public void setListener(AuthenticatorListener listener){
		this.listener = listener;
	}
	
	public boolean isDone(){
		return done;
	}
	
	public boolean didFail(){
		return failed;
	}
	
	public UDPHandler getUDPHandler(){
		return udp;
	}

	@Override
	public void commandReceived(Command cmd) {
		System.out.println("Got command "+cmd);
		switch(cmd.getCommand().toLowerCase()){
			case "sendauthenticationdetails":	tcp.send(new Command("authenticationDetails", username, password));break;
			case "initiatesoundport":			udp = new UDPHandler(tcp.getIP(), (int)cmd.getKey(), (byte)cmd.getValue());
												udp.setStunListener(this);
												udp.start();
												break;
			case "authenticationresult":		setResult(!(boolean)cmd.getKey());break;
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
			tcp.send(new Command("clientUdpTestOk", null, null));
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
