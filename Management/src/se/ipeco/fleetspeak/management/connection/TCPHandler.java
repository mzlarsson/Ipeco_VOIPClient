package se.ipeco.fleetspeak.management.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

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
		this.logger = Logger.getLogger("Debug");
		
		this.socket = socket;
		this.out = new PrintWriter(socket.getOutputStream());
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		socket.setSoTimeout(10000);
		
		
		Runnable reader = () -> {
			Thread.currentThread().setName("TCPHandler: Read");
			String indata = null;
			while(running){
				try {
					indata = in.readLine();					
					if(indata != null && !indata.equals("ping") && handler != null){
						handler.commandReceived(indata);
					}
				} catch(SocketException e){
					if(handler != null && running){
						try {
							JSONObject obj = new JSONObject();
							obj.put("command", "lostconnection");
							handler.commandReceived(obj.toString());
						} catch (JSONException je) {
							System.out.println("Could not notify connection lost");
						}
					}
				} catch(SocketTimeoutException ste){
					logger.info("Got no indata in 10sec. Keeping hope up though...");
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
			logger.info("Sending "+json);
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
