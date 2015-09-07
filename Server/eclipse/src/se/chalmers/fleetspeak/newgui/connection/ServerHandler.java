package se.chalmers.fleetspeak.newgui.connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import se.chalmers.fleetspeak.newgui.connection.Authenticator.AuthenticatorListener;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.UserInfoPacket;

public class ServerHandler {
	
	private static ServerHandler server;
	
	private TCPHandler tcp;
	private UDPHandler udp;
	private UserInfoPacket userInfo;
	
	private ServerHandler(TCPHandler tcp, UDPHandler udp, UserInfoPacket userInfo){
		this.tcp = tcp;
		this.udp = udp;
		this.userInfo = userInfo;
	}
	
	public String getUsername(){
		return userInfo.getName();
	}
	
	public int getUserID(){
		return userInfo.getID();
	}
	
	public int getUserRoomID(){
		return userInfo.getRoomID();
	}
	
	public void setCommandHandler(CommandHandler handler){
		tcp.setCommandHandler(handler);
	}
	
	public void sendCommand(Command c){
		if(c != null){
			tcp.send(c);
		}
	}
	
	public void terminate(){
		tcp.terminate();
		udp.terminate();
	}
	
	public static ServerHandler getConnectedServer(){
		return server;
	}

	public static void connect(String ip, int port, String username, String password, ConnectionListener listener){
		Socket tlsSocket = getTLSSocket(ip, port);
		if(tlsSocket == null){
			listener.onConnectionFailure("Error starting TLS");
			return;
		}
		System.out.println("Got socket");
		
		System.out.println("Initiating data tests");
		try {
			System.out.println("starting tcp");
			final TCPHandler tcp = new TCPHandler(tlsSocket);
			System.out.println("Initiating auth");
			Authenticator authenticator = new Authenticator(tcp, username, password);
			authenticator.setListener(new AuthenticatorListener() {
				
				@Override
				public void authenticationFailed(String msg) {
					listener.onConnectionFailure("Authentification failed.");
				}
				
				@Override
				public void authenticationDone() {
					server = new ServerHandler(tcp, authenticator.getUDPHandler(), authenticator.getUserInfo());
					listener.onConnect();
				}
			});
		} catch (IOException e) {
			listener.onConnectionFailure("Could not connect to server");
			return;
		}
	}
	
	public static void disconnect(){
		if(server != null){
			server.sendCommand(new Command("disconnect", null, null));
			server.terminate();
			server = null;
		}
	}
	
	private static Socket getTLSSocket(String host, int port){
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
			return null;
		}
	}
}
