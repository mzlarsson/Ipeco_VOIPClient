package se.ipeco.fleetspeak.management.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.istack.internal.logging.Logger;

public class TCPHandler {
	
	//Logging
	private Logger logger;

	//Connection
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private boolean running;
	private Executor executor;
	
	//Data receiver
	private CommandHandler handler;
	
	protected TCPHandler(Socket socket) throws IOException{
		this.logger = Logger.getLogger(this.getClass());
		
		this.socket = socket;
		this.out = new PrintWriter(socket.getOutputStream());
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		
		Runnable reader = () -> {
			Thread.currentThread().setName("TCPHandler: Read");
			String json = null;
			while(running){
				try {
					json = in.readLine();
					if(handler != null){
						handler.commandReceived(json);
					}
				} catch(SocketException e){
					if(handler != null){
						try {
							JSONObject obj = new JSONObject();
							obj.put("command", "lostconnection");
							handler.commandReceived(obj.toString());
						} catch (JSONException je) {
							System.out.println("Could not notify connection lost");
						}
					}
				} catch(IOException ioe){
					logger.warning("IO Exception while reading object from TCP");
				}
			}
		};
		this.running = true;
		executor = Executors.newSingleThreadExecutor();
		executor.execute(reader);
	}
	
	public void setCommandHandler(CommandHandler handler){
		this.handler = handler;
	}
	
	public InetAddress getIP(){
		return socket.getInetAddress();
	}
	
	public void send(String json){
		if(out != null){
			System.out.println("Sending "+json);
			out.println(json);
			out.flush();
		}
	}
	
	public void terminate(){
		running = false;
		try {
			if(socket != null){
				socket.close();
			}
		} catch (IOException e) {
			logger.severe("Could not close socket");
		}
		
		if(executor != null && executor instanceof ExecutorService){
			((ExecutorService)executor).shutdown();
		}
	}
}
