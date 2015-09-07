package se.chalmers.fleetspeak.newgui.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import se.chalmers.fleetspeak.util.Command;

import com.sun.istack.internal.logging.Logger;

public class TCPHandler {
	
	//Logging
	private Logger logger;

	//Connection
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private boolean running;
	private Executor executor;
	
	//Data receiver
	private CommandHandler handler;
	
	protected TCPHandler(Socket socket) throws IOException{
		this.logger = Logger.getLogger(this.getClass());
		
		this.socket = socket;
		this.out = new ObjectOutputStream(socket.getOutputStream());
		this.in = new ObjectInputStream(socket.getInputStream());
		
		
		Runnable reader = () -> {
			Thread.currentThread().setName("TCPHandler: Read");
			Object obj;
			while(running){
				try {
					obj = in.readObject();
					if(obj instanceof Command && handler != null){
						handler.commandReceived((Command)obj);
					}
				} catch (ClassNotFoundException e) {
					logger.warning("Got invalid class to TCPHandler: "+e.getMessage());
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
	
	public void send(Command cmd){
		System.out.println("Sending "+cmd);
		if(out != null){
			try {
				out.writeObject(cmd);
			} catch (IOException e) {
				logger.warning("Could not write object via TCP.");
			}
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
