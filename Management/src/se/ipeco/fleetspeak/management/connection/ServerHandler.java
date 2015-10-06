package se.ipeco.fleetspeak.management.connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyStore;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.json.JSONException;
import org.json.JSONObject;

import se.ipeco.fleetspeak.management.connection.Authenticator.AuthenticatorListener;

public class ServerHandler {
	
	private static ServerHandler server;
	private static Logger logger = Logger.getLogger("Debug");
	
	private TCPHandler tcp;
	private UDPHandler udp;
	private int userID;
	
	private ServerHandler(TCPHandler tcp, UDPHandler udp, int userID){
		this.tcp = tcp;
		this.udp = udp;
		this.userID = userID;
	}

	public int getUserID(){
		return userID;
	}
	
	public void setCommandHandler(CommandHandler handler){
		tcp.setCommandHandler(handler);
	}
	
	public void sendCommand(String json){
		if(json != null){
			tcp.send(json);
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
			listener.onConnectionFailure("Could not establish a connection. Check IP and port.");
			return;
		}else{
			try {
				tlsSocket.setSoTimeout(5000);
			} catch (SocketException e) {
				logger.warning("Could not set timeout time");
			}
		}
		logger.info("Got socket");
		
		logger.info("Initiating data tests");
		try {
			logger.info("starting tcp");
			final TCPHandler tcp = new TCPHandler(tlsSocket);
			logger.info("Initiating auth");
			Authenticator authenticator = new Authenticator(tcp, username, password);
			authenticator.setListener(new AuthenticatorListener() {
				
				@Override
				public void authenticationFailed(String msg) {
					//Close all half-open streams
					authenticator.terminate();
					listener.onConnectionFailure(msg);
				}
				
				@Override
				public void authenticationDone() {
					server = new ServerHandler(tcp, authenticator.getUDPHandler(), authenticator.getUserID());
					listener.onConnect();
				}
			});
		} catch (IOException e) {
			listener.onConnectionFailure("Could not connect to server: Unknown error.");
			return;
		}
	}
	
	public static void disconnect(){
		if(server != null){
			try {
				server.sendCommand(new JSONObject().put("command", "disconnect").toString());
			} catch (JSONException e) {
				logger.severe("Could not send disconnect: [JSONException] "+e.getMessage());
			}
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
			logger.severe("Got an exception while creating TLS socket. ");
			return null;
		}
	}
}
