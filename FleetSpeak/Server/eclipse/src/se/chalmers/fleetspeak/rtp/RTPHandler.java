package se.chalmers.fleetspeak.rtp;
import java.io.IOException;
import java.net.InetAddress;

import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.protocol.DataSource;
import javax.media.rtp.InvalidSessionAddressException;
import javax.media.rtp.RTPManager;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SendStream;
import javax.media.rtp.SessionAddress;

import se.chalmers.fleetspeak.ConnectionHandler;

public abstract class RTPHandler implements ConnectionHandler, ReceiveStreamListener{
	
	private RTPManager manager;
	private SendStream output;
	private SessionAddress client;

	public RTPHandler(InetAddress ip, int port) throws IOException{
		init(ip, port);
	}
	
	public void init(InetAddress ip, int port) throws IOException{
		/*
		try{
			this.manager = RTPManager.newInstance();
			SessionAddress localAddress = new SessionAddress();
			manager.initialize(localAddress);
			manager.addReceiveStreamListener(this);
			
			client = new SessionAddress(ip, port);
			manager.addTarget(client);
			MediaLocator locator = new MediaLocator("rtp://"+ip.getHostAddress()+":"+port);
			DataSource dataSource = Manager.createDataSource(locator);
			this.output = manager.createSendStream(dataSource, 1);
			this.output.start();
		}catch(Exception e){
			throw new IOException("Could not create RTP connection ["+e.getClass().getCanonicalName()+"]");
		}*/
	}

	public void close(){
		try {
			manager.removeTarget(client, "Client disconnected");
		} catch (InvalidSessionAddressException e) {}
		try {
			output.stop();
		} catch (IOException e) {}
		
		output.close();
		manager.dispose();
	}
}